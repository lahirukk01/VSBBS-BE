package com.lkksoftdev.registrationservice.common;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ResponseDto {
    private Object data;
    private Object error;

    public ResponseDto(Object data, Object error) {
        this.data = data;
        this.error = error;
    }

    public static ResponseDto BuildSuccessResponse(Object data, @NotNull Class<?> customEntityClass) {
        String className = customEntityClass.getSimpleName().toLowerCase();
        return buildSuccessResponse(data, className);
    }

    public static ResponseDto BuildSuccessResponse(Object data, String customEntityClassName) {
        String className = customEntityClassName.toLowerCase();
        return buildSuccessResponse(data, className);
    }

    private static ResponseDto buildSuccessResponse(Object data, String customEntityClassName) {
        Map<String, Object> result = new HashMap<>();
        String className = customEntityClassName.toLowerCase();

        if (data instanceof List<?> list) {
            result.put(className + "s", list);
        } else {
            result.put(className, data);
        }
        return new ResponseDto(result, null);
    }
}
