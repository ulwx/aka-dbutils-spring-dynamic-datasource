package com.github.ulwx.aka.dbutils.spring.multids;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public abstract class AkaAbsctractDataSourceAspect implements ApplicationContextAware {
    protected ApplicationContext applicationContext;
    private static final Logger log = LoggerFactory.getLogger(AkaDataSourceAspect.class);
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    final static ExpressionParser parser = new SpelExpressionParser();
    final static ParserContext parserContext = new TemplateParserContext("{", "}");

    abstract public  DataSourceAspectInfo getDataSourceAspectInfo(ProceedingJoinPoint point);

    @Pointcut("@annotation(com.github.ulwx.aka.dbutils.spring.multids.AkaDS)"
             + "|| @within(com.github.ulwx.aka.dbutils.spring.multids.AkaDS)"
    )
    public void dsPointCut() {
    }

    @Around("dsPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        DataSourceAspectInfo dataSourceAspectInfo=getDataSourceAspectInfo(point);
        return handle(point,dataSourceAspectInfo);

    }

    public  boolean isShardingJdbcDSAndAt( DataSourceInfo dataSourceInfo){
        String seata= dataSourceInfo.getSeata()==null?"":dataSourceInfo.getSeata().trim();
        String poolType=dataSourceInfo.getPoolType()==null?"":dataSourceInfo.getPoolType().trim();
        boolean isAtAndShardingJdbc=false;
        if( seata.equalsIgnoreCase("AT") &&
                poolType.equalsIgnoreCase("ShardingJDBC")){
            isAtAndShardingJdbc=true;
        }
        return isAtAndShardingJdbc;
    }

    public   Object handle(ProceedingJoinPoint point, DataSourceAspectInfo dataSourceAspectInfo)throws Throwable {

        String dynamicDataSourceBeanName=dataSourceAspectInfo.getDynamicDataSourceBeanName()==null?"":
                dataSourceAspectInfo.getDynamicDataSourceBeanName().trim();
        String dsName=dataSourceAspectInfo.getDsName();
        String laodBalancer=dataSourceAspectInfo.getLaodBalancer();

        AkaDynamicDataSource akaDynamicDataSource=null;
        if(!dynamicDataSourceBeanName.trim().isEmpty()
            && !dynamicDataSourceBeanName.equals("NONE")){
            akaDynamicDataSource= applicationContext.getBean(dynamicDataSourceBeanName,AkaDynamicDataSource.class);
        }else{
            akaDynamicDataSource= applicationContext.getBean(AkaDynamicDataSource.class);
        }

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
            String groupName=dsName;
            Set<String> dsSet=akaDynamicDataSource.getDataSourcesByGroupName(groupName);
            if(laodBalancer.isEmpty()){
                laodBalancer=GroupDecider.Random;
            }
            dsName=GroupDecider.getDecider(laodBalancer).decide(new ArrayList<>(dsSet));
        }

        Map<String, DataSourceInfo> map= akaDynamicDataSource.getTargetDataSources();
        DataSourceInfo dataSourceInfo=map.get(dsName);
        boolean isAtAndShardingJdbc=isShardingJdbcDSAndAt(dataSourceInfo);;
        try {
            StackDsInfo parent=AkaDataSourceContext.getDsInfo();
            StackDsInfo stackDsInfo=new StackDsInfo();
            stackDsInfo.setDsName(dsName);
            stackDsInfo.setDataSourceInfo(dataSourceInfo);
            stackDsInfo.setShardingjdbcAndSeataAtContext(false);
            stackDsInfo.setShardingjdbcAndSeataAtContextStart(false);
            log.debug("当前数据源" + dsName);
            AkaDataSourceContext.push(stackDsInfo);
            if(isAtAndShardingJdbc){
                if(parent==null|| !parent.isShardingjdbcAndSeataAtContext()) {
                    org.apache.shardingsphere.transaction.core.TransactionTypeHolder.set(
                            org.apache.shardingsphere.transaction.api.TransactionType.BASE);
                    stackDsInfo.setShardingjdbcAndSeataAtContextStart(true);
                }
                stackDsInfo.setShardingjdbcAndSeataAtContext(true);
            }else {
                if ((parent != null && parent.isShardingjdbcAndSeataAtContext())) {
                    stackDsInfo.setShardingjdbcAndSeataAtContext(true);
                }

            }
            return point.proceed();
        } finally {
            StackDsInfo stackDsInfo=AkaDataSourceContext.pop();
            // 销毁数据源 在执行方法之后
            log.debug("弹出数据源" + dsName);
            if( stackDsInfo.isShardingjdbcAndSeataAtContextStart()){
                org.apache.shardingsphere.transaction.core.TransactionTypeHolder.clear();

            }


        }
    }
}
