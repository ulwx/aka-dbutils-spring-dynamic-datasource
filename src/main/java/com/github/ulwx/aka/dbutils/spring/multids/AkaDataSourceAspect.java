package com.github.ulwx.aka.dbutils.spring.multids;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Order(-1)
@Component("com.github.ulwx.aka.dbutils.spring.multids.AkaDataSourceAspect")
public class AkaDataSourceAspect  extends AkaAbsctractDataSourceAspect{

    @Pointcut("@annotation(com.github.ulwx.aka.dbutils.spring.multids.AkaDS)"
            + "|| @within(com.github.ulwx.aka.dbutils.spring.multids.AkaDS)"
    )
    public void dsPointCut() {
    }
    @Override
    public DataSourceAspectInfo getDataSourceAspectInfo(ProceedingJoinPoint point){
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        AkaDS akaDsAnnotation = method.getAnnotation(AkaDS.class);
        if(akaDsAnnotation==null){
            akaDsAnnotation=point.getTarget().getClass().getAnnotation(AkaDS.class);
        }
        String dsName=akaDsAnnotation.value();;
        String dynamicDataSourceBeanName=akaDsAnnotation.dynamicDataSourceBeanName();
        String laodBalancer=  akaDsAnnotation.groupLoadBalancer()==null?"":akaDsAnnotation.groupLoadBalancer().trim();
        DataSourceAspectInfo dataSourceAspectInfo=new DataSourceAspectInfo(dsName,dynamicDataSourceBeanName,laodBalancer);
        return dataSourceAspectInfo;

    }



}

