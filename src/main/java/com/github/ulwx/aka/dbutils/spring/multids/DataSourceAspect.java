package com.github.ulwx.aka.dbutils.database.multids;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Order(-1)
@Component
public class DataSourceAspect {
    private static final Logger log = LoggerFactory.getLogger(DataSourceAspect.class);
    @Pointcut("@annotation(com.github.ulwx.aka.dbutils.database.multids.AkaDS)")
    public void dsPointCut() {

    }

    @Around("dsPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();

        Method method = signature.getMethod();

        AkaDS dataSource = method.getAnnotation(AkaDS.class);
        DataSourceContext.push(dataSource.value());
        log.debug("当前数据源" + dataSource.value());
        try {
            return point.proceed();
        } finally {
            // 销毁数据源 在执行方法之后
            log.debug("销毁数据源" + dataSource.value());
            DataSourceContext.pop();
        }
    }

}

