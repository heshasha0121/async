package com.lvchuan.utils;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description: 参数校验
 * @author: lvchuan
 * @createTime: 2024-05-30 15:32
 */
public class ValidateUtil {
    /**
     * 校验器
     * @param t         参数
     * @param <T>       参数类型
     * @return
     */
    public static <T> List<String> valid(T t){
        /*Validator validatorFactory = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<T>> errors = validatorFactory.validate(t);
        return errors.stream().map(error -> error.getMessage()).collect(Collectors.toList());*/
        return null;
    }
}
