package com.batalla.demoquiz.websocket;

public class WebSocketEvent {
    private String type;
    private Object payload;

    public WebSocketEvent(String type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public String getType() { return type; }
    public Object getPayload() { return payload; }
}
