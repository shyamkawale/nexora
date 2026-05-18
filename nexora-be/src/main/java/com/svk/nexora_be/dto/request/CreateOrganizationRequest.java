package com.svk.nexora_be.dto.request;

import lombok.Data;

@Data
public class CreateOrganizationRequest {
    private String name;
    private String description;
}
