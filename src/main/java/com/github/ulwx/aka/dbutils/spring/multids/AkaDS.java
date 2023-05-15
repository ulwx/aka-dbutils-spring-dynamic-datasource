package com.github.ulwx.aka.dbutils.spring.multids;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
@Inherited
public @interface AkaDS {
    /**
     * 指定{@link com.github.ulwx.aka.dbutils.spring.multids.AkaDynamicDataSource}的Bean名称，如果没有则默认在Spring
     * 上下文里查找一个 {@link com.github.ulwx.aka.dbutils.spring.multids.AkaDynamicDataSource}类型的Bean
     * @return
     */
     String dynamicDataSourceBeanName() default "";

    /** <p>
     *   指定动态数据源{@link com.github.ulwx.aka.dbutils.spring.multids.AkaDynamicDataSource}里的数据源名称，
     * 即AkaDynamicDataSource里targetDataSources属性包含的名称。如果不指定或指定空字符串("")，则使用AkaDynamicDataSource指定的
     * 默认数据源名称（即defaultTargetDataSourceName）。</p>
     * <p>也可以指定Spring EL表达式，表达式计算的结果必须是targetDataSources（原始数据源）里的
     * 一个数据源名称，例如 <code>master{#id % 2} </code>。注意：{ }里的内容为 Spring EL表达式。
     * Spring EL表达式里引用的变量只能通过{@link com.github.ulwx.aka.dbutils.spring.multids.AkaDataSourceContext#varPut(String, Object)}方法设置。
     * </p>
     *
     * @return
     */
    String value() default "";

    /**
     * 如果是组，则指定负载均衡的策略。动态数据源{@link com.github.ulwx.aka.dbutils.spring.multids.AkaDynamicDataSource}里的targetDataSources属性
     * 原始数据的名称以“--数字”结尾的会认为是某个组的成员，这个组的名称是“--数字”之前的部分。
     * 的
     * @return
     */
    String groupLoadBalancer() default "random";


}
