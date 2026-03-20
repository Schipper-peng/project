package com.bank.antifraud.aop;

import com.bank.antifraud.enums.OperationType;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auditable {
    OperationType value();
}
