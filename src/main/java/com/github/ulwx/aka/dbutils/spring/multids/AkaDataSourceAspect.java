package com.github.ulwx.aka.dbutils.spring.multids;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

@Aspect
@Order(-1)
@Component
public class AkaDataSourceAspect implements ApplicationContextAware {
    private  ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private static final Logger log = LoggerFactory.getLogger(AkaDataSourceAspect.class);
    @Pointcut("@annotation(com.github.ulwx.aka.dbutils.spring.multids.AkaDS)")
    public void dsPointCut() {

    }
    static ExpressionParser parser = new SpelExpressionParser();
    static ParserContext parserContext = new TemplateParserContext("{", "}");
    @Around("dsPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();

        Method method = signature.getMethod();
        AkaDS akaDsAnnotation = method.getAnnotation(AkaDS.class);
        String dynamicDataSourceBeanName=akaDsAnnotation.dynamicDataSourceBeanName();
        AkaDynamicDataSource akaDynamicDataSource=null;
        if(dynamicDataSourceBeanName!=null && !dynamicDataSourceBeanName.trim().isEmpty()){
            akaDynamicDataSource= applicationContext.getBean(dynamicDataSourceBeanName,AkaDynamicDataSource.class);
        }else{
            akaDynamicDataSource= applicationContext.getBean(AkaDynamicDataSource.class);
        }

        String dsName=akaDsAnnotation.value();
        if(dsName==null || dsName.trim().isEmpty()){
            dsName=akaDynamicDataSource.getDefaultTargetDataSourceName();
        }else{
            dsName=dsName.trim();
        }
        if(dsName.contains("{") && dsName.contains("}")){//说明是表达式
            StandardEvaluationContext context = new StandardEvaluationContext();
            Map<String,Object> paramatersMap = AkaDataSourceContext.getParamatersMap();
            context.setVariables(paramatersMap);
            Expression expression = parser.parseExpression(dsName,parserContext);
            String elValue=expression.getValue(context).toString();
            dsName=elValue;
            if(!akaDynamicDataSource.getTargetDataSources().keySet().contains(dsName)){
                throw new RuntimeException("根据数据源名称" +dsName+
                        "无法确定一个数据源！");
            }

        }else if(akaDynamicDataSource.getGroupNames().contains(dsName)){//说明是分组
            //查找所有组的数据源列表
            String laodBalancer=akaDsAnnotation.groupLoadBalancer();
            String groupName=dsName;
            Set<String> dsSet=akaDynamicDataSource.getDataSourcesByGroupName(groupName);
            dsName=GroupDecider.getDecider(laodBalancer).decide(new ArrayList<>(dsSet));
        }


        AkaDataSourceContext.push(dsName);
        log.debug("当前数据源" + dsName);
        Map<String, DataSourceInfo> map= akaDynamicDataSource.getTargetDataSources();
        DataSourceInfo dataSourceInfo=map.get(dsName);
        String seata= dataSourceInfo.getSeata()==null?"":dataSourceInfo.getSeata().trim();
        String poolType=dataSourceInfo.getPoolType()==null?"":dataSourceInfo.getPoolType().trim();
        try {
            if( seata.equalsIgnoreCase("AT") &&
              poolType.equalsIgnoreCase("ShardingJDBC")){
                org.apache.shardingsphere.transaction.core.TransactionTypeHolder.set(
                        org.apache.shardingsphere.transaction.api.TransactionType.BASE);

            }
            return point.proceed();
        } finally {
            // 销毁数据源 在执行方法之后
            log.debug("弹出数据源" + dsName);
            if( seata.equalsIgnoreCase("AT") &&
                    poolType.equalsIgnoreCase("ShardingJDBC")){
                org.apache.shardingsphere.transaction.core.TransactionTypeHolder.clear();

            }
            AkaDataSourceContext.pop();

        }
    }

}

