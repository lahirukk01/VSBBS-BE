package com.lkksoftdev.accountservice.feign;

import com.lkksoftdev.accountservice.auth.IntrospectRequestDto;
import com.lkksoftdev.accountservice.auth.IntrospectResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "introspectClient", url = "${introspect.url}")
public interface IntrospectClient {
    @PostMapping
    ResponseEntity<IntrospectResponseDto> validateToken(IntrospectRequestDto request);
}
