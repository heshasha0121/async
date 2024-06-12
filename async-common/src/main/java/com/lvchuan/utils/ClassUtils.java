package com.lvchuan.utils;

import org.springframework.aop.support.AopUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @description: 类utils
 * @author: lvchuan
 * @createTime: 2023-12-10 10:54
 */
public class ClassUtils {
    /**
     * 获取类型class
     * @param type 类
     * @param index 位数
     * @return class
     */
    public static Class<?> getClass(Type type, int index) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Type t = pt.getActualTypeArguments()[index];
            return getClass(t, index);
        } else {
            String className = type == null ? "null" : type.getClass().getName();
            throw new IllegalArgumentException("Expected a Class, ParameterizedType"
                    + ", but <" + type + "> is of type " + className);
        }
    }

    /**
     * 获取接口实现的泛型class
     * @param object 类对象
     * @param index 第几个泛型参数
     * @return class
     */
    public static Class<?> getGenericInterfacesClass(Object object, int index) {
        Class c = AopUtils.getTargetClass(object);
        Type[] typeArr = c.getGenericInterfaces();
        if (typeArr == null || typeArr.length == 0) {
            return null;
        }
        ParameterizedType genericSuperclass = (ParameterizedType) typeArr[index];
        Type type = genericSuperclass.getActualTypeArguments()[index];
        return getClass(type, index);
    }
}
