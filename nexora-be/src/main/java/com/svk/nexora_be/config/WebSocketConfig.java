package com.svk.nexora_be.config;

import com.svk.nexora_be.enums.OrganizationMemberStatus;
import com.svk.nexora_be.entity.User;
import com.svk.nexora_be.enums.UserRole;
import com.svk.nexora_be.repository.OrganizationMemberRepository;
import com.svk.nexora_be.repository.UserRepository;
import com.svk.nexora_be.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.nio.file.AccessDeniedException;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private static final String SESSION_USER_KEY = "userPublicId";
    private static final String SESSION_ORG_KEY = "organizationPublicId";

    private final JwtUtil jwtUtil;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final UserRepository userRepository;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                StompCommand command = accessor.getCommand();
                try {
                    if (StompCommand.CONNECT.equals(command)) {
                        validateConnect(accessor);
                    } else if (StompCommand.SUBSCRIBE.equals(command) || StompCommand.SEND.equals(command)) {
                        validateOrgDestination(accessor);
                    }
                } catch (AccessDeniedException ex) {
                    throw new IllegalArgumentException(ex.getMessage(), ex);
                }
                return message;
            }
        });
    }

    private void validateConnect(StompHeaderAccessor accessor) throws AccessDeniedException {
        String token = extractBearerToken(accessor.getFirstNativeHeader("Authorization"));
        String organizationPublicId = accessor.getFirstNativeHeader("X-Organization-Id");
        String userPublicId = token != null ? jwtUtil.validateAndExtractUsername(token) : null;

        if (userPublicId == null || organizationPublicId == null || organizationPublicId.isBlank()) {
            throw new AccessDeniedException("WebSocket Authorization and X-Organization-Id headers are required");
        }

        ensureApprovedMember(userPublicId, organizationPublicId);
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes != null) {
            sessionAttributes.put(SESSION_USER_KEY, userPublicId);
            sessionAttributes.put(SESSION_ORG_KEY, organizationPublicId);
        }
    }

    private void validateOrgDestination(StompHeaderAccessor accessor) throws AccessDeniedException {
        String destination = accessor.getDestination();
        String destinationOrgId = extractOrganizationFromDestination(destination);
        if (destinationOrgId == null) {
            return;
        }

        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        String userPublicId = sessionAttributes != null
                ? (String) sessionAttributes.get(SESSION_USER_KEY)
                : null;
        String sessionOrgId = sessionAttributes != null
                ? (String) sessionAttributes.get(SESSION_ORG_KEY)
                : null;

        if (userPublicId == null || sessionOrgId == null || !sessionOrgId.equals(destinationOrgId)) {
            throw new AccessDeniedException("WebSocket organization access denied");
        }
        ensureApprovedMember(userPublicId, destinationOrgId);
    }

    private void ensureApprovedMember(String userPublicId, String organizationPublicId) throws AccessDeniedException {
        User user = userRepository.findByPublicId(userPublicId)
                .orElseThrow(() -> new AccessDeniedException("User not found"));
        if (user.getRole() == UserRole.ADMIN) {
            return;
        }

        boolean approved = organizationMemberRepository.existsByOrganizationPublicIdAndUserPublicIdAndStatus(
                organizationPublicId,
                userPublicId,
                OrganizationMemberStatus.APPROVED
        );
        if (!approved) {
            throw new AccessDeniedException("User is not an approved member of this organization");
        }
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        return authorizationHeader.substring(7);
    }

    private String extractOrganizationFromDestination(String destination) {
        if (destination == null) {
            return null;
        }
        String marker = "/org/";
        int markerIndex = destination.indexOf(marker);
        if (markerIndex < 0) {
            return null;
        }
        int orgStart = markerIndex + marker.length();
        int orgEnd = destination.indexOf('/', orgStart);
        if (orgEnd < 0) {
            return destination.substring(orgStart);
        }
        return destination.substring(orgStart, orgEnd);
    }
}
