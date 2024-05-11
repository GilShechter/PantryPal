package com.pantrypal.pantrypal.model;

public class IdentifierResponse {
    private String finishReason;
    private int index;
    private Object logprobs;
    private Message message;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}

