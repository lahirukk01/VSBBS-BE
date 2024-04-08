package com.lkksoftdev.loanservice.customAnnotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BearerToken {
}
