package com.svk.nexora_be.service;

import java.util.List;

public interface KnowledgeBaseService {
    /**
     * Search knowledge base for relevant documents
     * @param query Search query
     * @param limit Maximum number of results to return
     * @return List of relevant document contents
     */
    List<String> search(String query, int limit);
    
    /**
     * Get a specific document by name
     * @param documentName Name of the document (e.g., "presence-tracking")
     * @return Document content
     */
    String getDocument(String documentName);
    
    /**
     * Get all available documents
     * @return List of all document names
     */
    List<String> getAllDocuments();
    
    /**
     * Build augmented context from search results
     * @param query User question
     * @return Formatted knowledge context for AI prompt
     */
    String buildAugmentedContext(String query);
}
