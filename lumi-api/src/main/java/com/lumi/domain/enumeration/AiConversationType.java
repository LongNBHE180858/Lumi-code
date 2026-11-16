package com.lumi.domain.enumeration;

/** Conversation types supported by the RAG chat services. */
public enum AiConversationType {
    RAG_TUTOR,
    /** Legacy conversations remain readable; new conversations use RAG_TUTOR. */
    DEMO,
}
