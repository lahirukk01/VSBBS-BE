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
        Map<String, Object> result = new HashMap<>();
        String className = customEntityClass.getSimpleName().toLowerCase();

        if (data instanceof List<?>) {
            List<?> list = (List<?>) data;

            result.put(className + "s", list);
        } else {
            result.put(className, data);
        }
        return new ResponseDto(result, null);
    }
}
