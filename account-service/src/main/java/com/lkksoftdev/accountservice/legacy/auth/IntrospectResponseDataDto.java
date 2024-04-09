package com.lkksoftdev.accountservice.legacy.auth;

public record IntrospectResponseDataDto(Long userId, String scope, String onlineAccountStatus) {
}
