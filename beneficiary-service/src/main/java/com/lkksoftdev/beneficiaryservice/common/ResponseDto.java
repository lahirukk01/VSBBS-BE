package com.lkksoftdev.beneficiaryservice.common;


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
    private ErrorDetails error;

    public ResponseDto(Object data, ErrorDetails error) {
        this.data = data;
        this.error = error;
    }

    public static ResponseDto BuildSuccessResponse(Object data, @NotNull Class<?> customEntityClass) {
        Map<String, Object> result = new HashMap<>();
        String className = customEntityClass.getSimpleName().toLowerCase();

        if (data instanceof List<?> list) {
            if (className.endsWith("y")) {
                className = className.substring(0, className.length() - 1) + "ies";
            } else {
                className += "s";
            }
            result.put(className, list);
        } else {
            result.put(className, data);
        }
        return new ResponseDto(result, null);
    }
}
