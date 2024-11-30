package com.example.umfeed.models;

public class Result<T> {
    private T data;
    private Throwable error;

    private Result(T data, Throwable error) {
        this.data = data;
        this.error = error;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(data, null);
    }

    public static <T> Result<T> failure(Throwable error) {
        return new Result<>(null, error);
    }

    public T getData() {
        return data;
    }

    public Throwable getError() {
        return error;
    }

    public boolean isSuccess() {
        return error == null;
    }
}
