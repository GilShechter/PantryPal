package com.pantrypal.pantrypal.model;

public class Message {
    private String content;
    private String role;
    private Object functionCall;
    private Object toolCalls;

    public String getContent() {
        return content;
    }

}
