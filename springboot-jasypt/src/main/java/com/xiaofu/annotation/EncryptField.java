package com.xiaofu.annotation;

import java.lang.annotation.*;
@Documented
@Target({ElementType.FIELD,ElementType.PARAMETER,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EncryptField {

    String[] value() default "";
}


