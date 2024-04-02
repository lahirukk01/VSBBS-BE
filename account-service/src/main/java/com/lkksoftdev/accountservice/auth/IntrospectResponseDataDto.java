package com.lkksoftdev.accountservice.auth;

public record IntrospectResponseDataDto(Long userId, String scope, String onlineAccountStatus) {
}
