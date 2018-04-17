package com.yeucheng.renatationdemo.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Administrator on 2017/6/10.
 * 检查网络
 * Target 代表Annotation的位置   FIELD属性   METHOD方法  TYPE类  CONSTRUCTOR构造函数
 * Retention 什么时候生效  CLASS编译时  RUNTIME运行时  SOURCE源码资源
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckNet {
}
