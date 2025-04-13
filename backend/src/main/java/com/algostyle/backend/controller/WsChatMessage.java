package com.algostyle.backend.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class WsChatMessage {
    private String sender;
    private String content;
    private WsChatMessageType type;

    public WsChatMessage() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public WsChatMessageType getType() {
        return type;
    }

    public void setType(WsChatMessageType type) {
        this.type = type;
    }
}
