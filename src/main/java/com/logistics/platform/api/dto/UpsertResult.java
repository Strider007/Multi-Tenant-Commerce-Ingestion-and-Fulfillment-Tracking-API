package com.logistics.platform.api.dto;

public class UpsertResult<T> {
    private final T data;
    private final boolean created;

    public UpsertResult(T data, boolean created) {
        this.data = data;
        this.created = created;
    }

    public T getData() { return data; }
    public boolean isCreated() { return created; }
}
