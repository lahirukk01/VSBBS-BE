package com.lkksoftdev.registrationservice.auth;

public record IntrospectResponseDataDto(Integer userId, String scope, String onlineAccountStatus) {
}
