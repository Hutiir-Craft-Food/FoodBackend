package com.khutircraftubackend.common;

public record UpsertResult<T>(T value, boolean created) {
}
