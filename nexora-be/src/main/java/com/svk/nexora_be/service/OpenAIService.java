package com.svk.nexora_be.service;

/**
 * Service for interacting with OpenAI API
 * Supports both simple Q&A and RAG (Retrieval Augmented Generation)
 */
public interface OpenAIService {
    /**
     * Simple question without knowledge base context
     * @param userMessage User's question
     * @return AI response
     */
    String ask(String userMessage);
    
    /**
     * Ask question with knowledge base context (RAG)
     * @param userMessage User's question
     * @param knowledgeContext Relevant knowledge base content
     * @return AI response based on knowledge base
     */
    String askWithContext(String userMessage, String knowledgeContext);
}
