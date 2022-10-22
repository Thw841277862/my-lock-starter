package com.my.lock.spel;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 分布式锁key生成器
 */
public class DefaultLockKeyBuilder implements LockKeyBuilder {
    private static final ParameterNameDiscoverer NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    private static final ExpressionParser PARSER = new SpelExpressionParser();
    @Override
    public String buildKey(MethodInvocation invocation, String[] definitionKeys) {
        Method method = invocation.getMethod();
        if (definitionKeys.length > 1 || !"".equals(definitionKeys[0])) {
            return getSpelDefinitionKey(definitionKeys, method, invocation.getArguments());
        }
        return "";
    }

    /**
     * 获取spel表达式定义的key
     *
     * @param definitionKeys  定义的key
     * @param method          拦截的方法
     * @param parameterValues 参数值
     * @return 返回spel表达式处理的值
     */
    protected String getSpelDefinitionKey(String[] definitionKeys, Method method, Object[] parameterValues) {
        EvaluationContext context = new MethodBasedEvaluationContext(null, method, parameterValues, NAME_DISCOVERER);
        List<String> definitionKeyList = new ArrayList<>(definitionKeys.length);
        for (String definitionKey : definitionKeys) {
            if (definitionKey != null && !definitionKey.isEmpty()) {
                String key = PARSER.parseExpression(definitionKey).getValue(context, String.class);
                definitionKeyList.add(key);
            }
        }
        return StringUtils.collectionToDelimitedString(definitionKeyList, "#", "", "");
    }
}
