package com.manulife.codingtest.bookstore.config.payload;

public class MessageResponse {
    private ResponseType responseType;
    private String message;

    public MessageResponse(ResponseType responseType, String message) {
        this.responseType = responseType;
        this.message = message;
    }

    public MessageResponse(String message) {
        this.message = message;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
