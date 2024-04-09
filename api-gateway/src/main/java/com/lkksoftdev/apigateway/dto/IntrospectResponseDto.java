package com.lkksoftdev.apigateway.dto;

import lombok.Getter;

@Getter
public class IntrospectResponseDto {
    private final IntrospectResponseDataDto data;
    private final ErrorDetails error;

    public IntrospectResponseDto(IntrospectResponseDataDto data, ErrorDetails error) {
        this.data = data;
        this.error = error;
    }

    public String toString() {
        return "IntrospectResponseDto(data=" + data + ", error=" + error + ")";
    }
}
