package com.mrkresnofatihdev.gulugulu.models;

public class ResponseModel<T> {
    private T Data;
    private String ErrorMessage;

    public ResponseModel(T data, String errorMessage) {
        Data = data;
        ErrorMessage = errorMessage;
    }

    public T getData() {
        return Data;
    }

    public void setData(T data) {
        Data = data;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }
}
