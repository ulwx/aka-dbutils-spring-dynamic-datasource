package com.github.ulwx.aka.dbutils.spring.multids;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class AkaDataSourceContext {

    private final static ThreadLocal<Stack<StackDsInfo>> LOCAL_DATASOURCE =
            new ThreadLocal<Stack<StackDsInfo>>(){
                @Override
                protected Stack<StackDsInfo> initialValue() {
                    return new Stack<>();
                }
            };

    private final static ThreadLocal<Map<String,Object>> PARAMATERS_MAP =
            new ThreadLocal<Map<String,Object>>(){
                @Override
                protected Map<String,Object> initialValue() {
                    return new HashMap<>();
                }
           };

    public static void push(String name) {
        StackDsInfo stackDsInfo=new StackDsInfo();
        stackDsInfo.setDsName(name);
        LOCAL_DATASOURCE.get().push(stackDsInfo);
    }

    public static String  get() {
        return LOCAL_DATASOURCE.get().size()==0?null:LOCAL_DATASOURCE.get().peek().getDsName();
    }

    public static void pop() {
        LOCAL_DATASOURCE.get().pop();
    }


    public static void executeMethod(String dsName,Call function){
        try {
             push(dsName);
             function.call();
        }finally {
            pop();
        }
    }
    public static void varPut(String name,Object value) {
         PARAMATERS_MAP.get().put(name,value);
    }
    public static void varRemove(String name) {
        PARAMATERS_MAP.get().remove(name);
    }
    static  Map<String,Object> getParamatersMap() {
       return  PARAMATERS_MAP.get();
    }

    public static DataSourceInfo getCurrentDS(){
        return AkaDynamicDataSource.getCurrentDS();
    }
    public static interface  Call{
        void call() ;
    }

}
