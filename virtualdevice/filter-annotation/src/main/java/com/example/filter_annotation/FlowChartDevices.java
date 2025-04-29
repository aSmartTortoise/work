package com.example.filter_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)//编译期
@Target(value = {ElementType.TYPE})//注解用在类上
public @interface FlowChartDevices {
}
