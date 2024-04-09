package com.lkksoftdev.accountservice.legacy.auth;

import com.lkksoftdev.accountservice.common.ErrorDetails;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IntrospectResponseDto {
    private IntrospectResponseDataDto data;
    private ErrorDetails error;

    public IntrospectResponseDto(IntrospectResponseDataDto data, ErrorDetails error) {
        this.data = data;
        this.error = error;
    }
}
