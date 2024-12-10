package com.lvchuan.common.page;

import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.conditions.SharedString;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.baomidou.mybatisplus.extension.conditions.AbstractChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.ChainQuery;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static com.baomidou.mybatisplus.core.enums.SqlKeyword.IN;
import static java.util.stream.Collectors.joining;


/**
 * @description: 自定义mybatis-plus查询
 * @author: lvchuan
 * @createTime: 2024-06-26 13:49
 */
public class CustomerQueryChainWrapper<T> extends AbstractChainWrapper<T, SFunction<T, ?>, CustomerQueryChainWrapper<T>, LambdaQueryWrapper<T>>
        implements ChainQuery<T>, Query<CustomerQueryChainWrapper<T>, T, SFunction<T, ?>> {

    private final BaseMapper<T> baseMapper;

    private SharedString sqlSelect = new SharedString();

    public CustomerQueryChainWrapper(BaseMapper<T> baseMapper) {
        super();
        this.baseMapper = baseMapper;
        super.wrapperChildren = new LambdaQueryWrapper<>();
        ReflectUtil.setFieldValue(wrapperChildren, "sqlSelect", this.sqlSelect);
    }

    @SafeVarargs
    @Override
    public final CustomerQueryChainWrapper<T> select(SFunction<T, ?>... columns) {
        wrapperChildren.select(columns);
        return typedThis;
    }

    public CustomerQueryChainWrapper<T> selectDistinct(SFunction<T, ?>... columns) {
        if (ArrayUtils.isNotEmpty(columns)) {
            this.sqlSelect.setStringValue("distinct " + columnsToString(false, columns));
        }
        return typedThis;
    }

    @Override
    public CustomerQueryChainWrapper<T> select(Class<T> entityClass, Predicate<TableFieldInfo> predicate) {
        wrapperChildren.select(entityClass, predicate);
        return typedThis;
    }

    @Override
    public String getSqlSelect() {
        throw ExceptionUtils.mpe("can not use this method for \"%s\"", "getSqlSelect");
    }

    @Override
    public BaseMapper<T> getBaseMapper() {
        return baseMapper;
    }

    public LambdaQueryWrapper<T> inOfParam2(SFunction<T, ?> column1, SFunction<T, ?> column2,
                                            List<?> coll1, List<?> coll2) {
        return doIt(true,
                () -> StringPool.LEFT_BRACKET + columnsToString(true, column1, column2) + StringPool.RIGHT_BRACKET,
                IN,
                inExpressionOfParam(coll1, coll2));
    }

    public LambdaQueryWrapper<T> inOfParam3(SFunction<T, ?> column1, SFunction<T, ?> column2,
                                            SFunction<T, ?> column3,
                                            List<?> coll1, List<?> coll2, List<?> coll3) {
        return doIt(true,
                () -> StringPool.LEFT_BRACKET + columnsToString(true, column1, column2, column3) + StringPool.RIGHT_BRACKET,
                IN,
                inExpressionOfParam(coll1, coll2, coll3));
    }

    /**
     * 获取 columnName
     */
    protected String columnsToString(boolean onlyColumn, SFunction<T, ?>... columns) {
        return Arrays.stream(columns).map(i -> columnToString(i, onlyColumn)).collect(joining(StringPool.COMMA));
    }

    protected String columnToString(SFunction<T, ?> column, boolean onlyColumn) {
        Method method = ReflectUtil.getMethod(AbstractLambdaWrapper.class, "getColumn", SerializedLambda.class, Boolean.class);
        try {
            ReflectionUtils.makeAccessible(method);
            return (String) method.invoke(wrapperChildren, LambdaUtils.resolve(column), onlyColumn);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private ISqlSegment inExpressionOfParam(List<?>... coll) {
        int collSize = coll.length;
        if (collSize == 0) {
            return () -> "";
        }
        int size1 = coll[0].size();
        if (size1 > 1000) {
            size1 = 1000;
        }
        List<String> resultList = new ArrayList<>();
        List<String> formatList = new ArrayList<>();
        for (int i = 0; i<collSize; i++) {
            formatList.add("{" + i + "}");
        }
        String format = formatList.stream().collect(joining(StringPool.COMMA, StringPool.LEFT_BRACKET, StringPool.RIGHT_BRACKET));
        for (int i=0; i < size1; i++) {
            Object[] objectArr = new Object[collSize];
            for (int j = 0; j < collSize; j++) {
                if (coll[j].size() >= i) {
                    objectArr[j] = coll[j].get(i);
                }
            }
            resultList.add(formatSql(format , objectArr));
        }
        return () -> resultList.stream()
                .collect(joining(StringPool.COMMA, StringPool.LEFT_BRACKET, StringPool.RIGHT_BRACKET));
    }

    protected final String formatSql(String sqlStr, Object... params) {
        Method method = ReflectUtil.getMethod(AbstractWrapper.class, "formatSql");
        try {
            ReflectionUtils.makeAccessible(method);
            return (String) method.invoke(wrapperChildren, sqlStr, params);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    protected LambdaQueryWrapper<T> doIt(boolean condition, ISqlSegment... sqlSegments) {
        Method method = ReflectUtil.getMethod(AbstractWrapper.class, "doIt");;
        try {
            ReflectionUtils.makeAccessible(method);
            return (LambdaQueryWrapper<T>) method.invoke(wrapperChildren, true, sqlSegments);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
