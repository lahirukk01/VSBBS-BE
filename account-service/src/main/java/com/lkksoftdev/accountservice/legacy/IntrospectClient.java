package com.lkksoftdev.accountservice.legacy;

import com.lkksoftdev.accountservice.legacy.auth.IntrospectRequestDto;
import com.lkksoftdev.accountservice.legacy.auth.IntrospectResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "introspectClient", url = "${introspect.url}")
public interface IntrospectClient {
    @PostMapping
    ResponseEntity<IntrospectResponseDto> validateToken(IntrospectRequestDto request);
}
