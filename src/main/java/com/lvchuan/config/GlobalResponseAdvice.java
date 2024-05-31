package com.lvchuan.config;

import com.lvchuan.common.exception.BusinessException;
import com.lvchuan.common.web.CommonResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @description: 统一反参
 * @author: lvchuan
 * @createTime: 2024-05-30 17:38
 */
@RestControllerAdvice
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {
    private static final int ERROR_CODE = 500;


    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (o instanceof CommonResponse) {
            return o;
        }
        return CommonResponse.returnSuccess(o);
    }

    @ExceptionHandler({Exception.class})
    @ResponseBody
    public ResponseEntity<Object> exception(Exception e, HttpServletRequest request) {
        if (e instanceof MethodArgumentNotValidException) {
            BindingResult bindingResult = ((MethodArgumentNotValidException)e).getBindingResult();
            StringBuilder error = new StringBuilder();
            for (ObjectError objectError : bindingResult.getAllErrors()) {
                FieldError fieldError = (FieldError) objectError;
                error.append(fieldError.getDefaultMessage()).append(";");
            }
            return this.exceptionResponse(CommonResponse.returnError(ERROR_CODE, error));
        } else if (e instanceof BusinessException) {
            return this.exceptionResponse(CommonResponse.returnError(((BusinessException) e).getCode(), e.getMessage()));
        } else {
            return this.exceptionResponse(CommonResponse.returnError("系统错误"));
        }
    }

    private ResponseEntity exceptionResponse(Object body) {
        return ResponseEntity.status(ERROR_CODE).body(body);
    }
}
