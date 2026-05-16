package com.svk.nexora_be.service.impl;

import com.svk.nexora_be.service.KnowledgeBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing and searching Nexora knowledge base documents.
 * Implements simple RAG (Retrieval Augmented Generation) without vector database.
 */
@Service
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {
    private static final Logger logger = LoggerFactory.getLogger(KnowledgeBaseServiceImpl.class);
    
    private final ResourceLoader resourceLoader;
    private Map<String, String> knowledgeBase;
    
    // Knowledge base document names
    private static final String[] KNOWLEDGE_DOCS = {
            "overview",
            "presence-tracking",
            "messaging",
            "social-features",
            "file-management",
            "authentication-security",
            "ai-chatbot"
    };
    
    @Autowired
    public KnowledgeBaseServiceImpl(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        this.knowledgeBase = new HashMap<>();
        loadKnowledgeBase();
    }
    
    /**
     * Load all knowledge documents from classpath resources
     */
    private void loadKnowledgeBase() {
        try {
            for (String docName : KNOWLEDGE_DOCS) {
                String content = loadDocumentContent(docName);
                if (content != null) {
                    knowledgeBase.put(docName, content);
                    logger.info("Loaded knowledge document: {}", docName);
                }
            }
            logger.info("Knowledge base loaded successfully with {} documents", knowledgeBase.size());
        } catch (Exception e) {
            logger.error("Error loading knowledge base", e);
        }
    }
    
    /**
     * Load a single document from resources
     */
    private String loadDocumentContent(String docName) {
        try {
            Resource resource = resourceLoader.getResource(
                    "classpath:knowledge/" + docName + ".md"
            );
            
            if (!resource.exists()) {
                logger.warn("Knowledge document not found: {}", docName);
                return null;
            }
            
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            logger.error("Error loading document: {}", docName, e);
            return null;
        }
    }
    
    /**
     * Search knowledge base for relevant documents
     * Simple text-based search using keyword matching
     */
    @Override
    public List<String> search(String query, int limit) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String[] keywords = query.toLowerCase().split("\\s+");
        
        return knowledgeBase.entrySet().stream()
                .map(entry -> new DocumentScore(entry.getKey(), entry.getValue(), keywords))
                .filter(doc -> doc.score > 0)
                .sorted(Comparator.comparingDouble(DocumentScore::getScore).reversed())
                .limit(limit)
                .map(DocumentScore::getContent)
                .collect(Collectors.toList());
    }
    
    /**
     * Get a specific document by name
     */
    @Override
    public String getDocument(String documentName) {
        return knowledgeBase.getOrDefault(documentName, null);
    }
    
    /**
     * Get all available documents
     */
    @Override
    public List<String> getAllDocuments() {
        return new ArrayList<>(knowledgeBase.keySet());
    }
    
    /**
     * Build augmented context from search results
     * Combines relevant documents into a formatted context for the AI prompt
     */
    @Override
    public String buildAugmentedContext(String query) {
        List<String> relevantDocs = search(query, 3);
        
        if (relevantDocs.isEmpty()) {
            return "";
        }
        
        StringBuilder context = new StringBuilder();
        context.append("Knowledge Base:\n");
        context.append("================\n\n");
        
        for (int i = 0; i < relevantDocs.size(); i++) {
            if (i > 0) {
                context.append("\n---\n\n");
            }
            context.append(relevantDocs.get(i));
        }
        
        return context.toString();
    }
    
    /**
     * Helper class for scoring document relevance
     */
    private static class DocumentScore {
        private final String name;
        private final String content;
        private final double score;
        
        DocumentScore(String name, String content, String[] keywords) {
            this.name = name;
            this.content = content;
            this.score = calculateScore(content, keywords);
        }
        
        private double calculateScore(String content, String[] keywords) {
            String contentLower = content.toLowerCase();
            double score = 0;
            
            for (String keyword : keywords) {
                if (keyword.length() > 2) { // Ignore very short keywords
                    // Count occurrences of keyword
                    int count = 0;
                    int index = 0;
                    while ((index = contentLower.indexOf(keyword, index)) != -1) {
                        count++;
                        index += keyword.length();
                    }
                    score += count * 2;
                    
                    // Bonus for keyword at start of headings
                    if (contentLower.contains("# " + keyword) ||
                        contentLower.contains("## " + keyword)) {
                        score += 5;
                    }
                }
            }
            
            return score;
        }
        
        public double getScore() {
            return score;
        }
        
        public String getContent() {
            return content;
        }
    }
}
