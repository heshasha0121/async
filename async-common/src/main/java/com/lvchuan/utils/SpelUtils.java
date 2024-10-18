package com.lvchuan.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;

import java.lang.reflect.Method;
import java.util.Objects;

@Slf4j
public class SpelUtils {


    /**
     * 解析spel表达式
     *
     * @param method 方法
     * @param args 参数值
     * @param spelExpression  表达式
     * @param clz  返回结果的类型
     * @param defaultResult 默认结果
     * @return 执行spel表达式后的结果
     */
    public static <T> T parse(Method method, Object[] args, String spelExpression, Class<T> clz, T defaultResult) {
        DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
        String[] params = parameterNameDiscoverer.getParameterNames(method);
        EvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().withRootObject(args).build();
        //设置上下文变量
        for (int i = 0; i < params.length; i++) {
            context.setVariable(params[i], args[i]);
        }
        T result = getResult(context, spelExpression,clz);
        if(Objects.isNull(result)){
            return defaultResult;
        }
        return result;
    }


    /**
     * 获取spel表达式后的结果
     *
     * @param context 解析器上下文接口
     * @param spelExpression  表达式
     * @param clz  返回结果的类型
     * @return 执行spel表达式后的结果
     */
    private static <T> T getResult(EvaluationContext context, String spelExpression, Class<T> clz){
        try {
            //解析表达式
            Expression expression = parseExpression(spelExpression);
            //获取表达式的值
            return expression.getValue(context, clz);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }


    /**
     * 解析表达式
     * @param spelExpression spel表达式
     * @return
     */
    private static Expression parseExpression(String spelExpression){
        ExpressionParser expressionParser = new SpelExpressionParser();
        // 如果表达式是一个#{}表达式，需要为解析传入模板解析器上下文
        return expressionParser.parseExpression(spelExpression,null);
    }

}