package com.github.ulwx.aka.dbutils.database.spring.boot;

import com.github.ulwx.aka.dbutils.database.multids.DataSourceAspect;
import com.github.ulwx.aka.dbutils.database.multids.DynamicDataSource;
import com.github.ulwx.aka.dbutils.database.spring.MDataBaseFactory;
import com.github.ulwx.aka.dbutils.database.spring.MDataBaseTemplate;
import com.github.ulwx.aka.dbutils.database.utils.DbConst;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@EnableTransactionManagement(proxyTargetClass = true)
@EnableAspectJAutoProxy(exposeProxy = true)
@Configuration
@ComponentScan
@Import(DataSourceAspect.class)
public class MyConfiguration {

    @Bean(name = "dynamicDataSource")
    public DynamicDataSource DataSource(@Qualifier("dataSource1") DataSource dataSource1,
                                        @Qualifier("dataSource2") DataSource dataSource2) {
        //targetDataSource 集合是我们数据库和名字之间的映射
        Map<Object, Object> targetDataSource = new HashMap<>();
        targetDataSource.put("dataSource1", dataSource1);
        targetDataSource.put("dataSource2", dataSource2);
        DynamicDataSource dataSource = new DynamicDataSource();
        dataSource.setTargetDataSources(targetDataSource);
        //设置默认对象
        dataSource.setDefaultTargetDataSource(dataSource1);
        return dataSource;
    }
    @Bean(destroyMethod = "close")
    public BasicDataSource dataSource1() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/test?x=1&useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8&useSSL=false");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        dataSource.setMaxWaitMillis(10000);
        dataSource.setInitialSize(1);
        dataSource.setMaxTotal(10);
        dataSource.setMinEvictableIdleTimeMillis(6000);
        return dataSource;

    }
    @Bean(destroyMethod = "close")
    public BasicDataSource dataSource2() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/test2?x=1&useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8&useSSL=false");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        dataSource.setMaxWaitMillis(10000);
        dataSource.setInitialSize(1);
        dataSource.setMaxTotal(10);
        dataSource.setMinEvictableIdleTimeMillis(6000);
        return dataSource;

    }

    @Bean
    public DataSourceTransactionManager transactionManager(DynamicDataSource dynamicDataSource) {
        DataSourceTransactionManager dt = new DataSourceTransactionManager();
        dt.setDataSource(dynamicDataSource);
        return dt;
    }
    // @Bean
    // public DataSourceTransactionManager transactionManager1() {
    //     DataSourceTransactionManager dt = new DataSourceTransactionManager();
    //     dt.setDataSource(dataSource1());
    //     return dt;
    // }
    //
    // @Bean
    // public DataSourceTransactionManager transactionManager2() {
    //     DataSourceTransactionManager dt = new DataSourceTransactionManager();
    //     dt.setDataSource(dataSource2());
    //     return dt;
    // }
    @Bean
    public MDataBaseFactory mDataBaseFactory1(DynamicDataSource dynamicDataSource) {
        MDataBaseFactory mDataBaseFactory = new MDataBaseFactory(dynamicDataSource);
        mDataBaseFactory.setTableColumRule(DbConst.TableNameRules.underline_to_camel);
        mDataBaseFactory.setTableNameRule(DbConst.TableColumRules.underline_to_camel);
        return mDataBaseFactory;

    }
    @Bean
    public MDataBaseFactory mDataBaseFactory2(DynamicDataSource dynamicDataSource) {
        MDataBaseFactory mDataBaseFactory = new MDataBaseFactory(dynamicDataSource);
        mDataBaseFactory.setTableColumRule(DbConst.TableNameRules.underline_to_camel);
        mDataBaseFactory.setTableNameRule(DbConst.TableColumRules.underline_to_camel);
        return mDataBaseFactory;

    }
    @Bean
    public MDataBaseTemplate mDataBaseTemplate1(MDataBaseFactory mDataBaseFactory1) {
        return new MDataBaseTemplate(mDataBaseFactory1);
    }
    @Bean
    public MDataBaseTemplate mDataBaseTemplate2(MDataBaseFactory mDataBaseFactory2) {
        return new MDataBaseTemplate(mDataBaseFactory2);
    }

}
