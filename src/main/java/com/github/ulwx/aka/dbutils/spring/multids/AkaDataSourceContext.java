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
                    return new HashMap<String,Object>();
                }
           };

    public static void push(String name) {
        StackDsInfo stackDsInfo=new StackDsInfo();
        stackDsInfo.setDsName(name);
        LOCAL_DATASOURCE.get().push(stackDsInfo);
    }
    public static void push(StackDsInfo stackDsInfo) {
        LOCAL_DATASOURCE.get().push(stackDsInfo);
    }
    public static StackDsInfo getDsInfo(){
        return LOCAL_DATASOURCE.get().size()==0?null:LOCAL_DATASOURCE.get().peek();
    }
    public static String  get() {
        return LOCAL_DATASOURCE.get().size()==0?null:LOCAL_DATASOURCE.get().peek().getDsName();
    }

    public static StackDsInfo pop(String name) {
        if(LOCAL_DATASOURCE.get().size()>0) {
            StackDsInfo pop= LOCAL_DATASOURCE.get().pop();
            if(name.equals(pop.getDsName())){
                return pop;
            }else{
                throw new RuntimeException("内存状态不一致！name="+name+",pop="+pop.getDsName());
            }
        }
        return null;
    }


    public static void executeMethod(String dsName,Call function){
        push(dsName);
        try {
             function.call();
        }finally {
            pop(dsName);
        }
    }
    public static void varPut(String name,Object value) {
         PARAMATERS_MAP.get().put(name,value);
    }
    public static void varRemove(String name) {
        PARAMATERS_MAP.get().remove(name);
    }
    public static  Map<String,Object> getParamatersMap() {
       return  PARAMATERS_MAP.get();
    }

    public static DataSourceInfo getCurrentDS(){
        return AkaDynamicDataSource.getCurrentDS();
    }
    public static interface  Call{
        void call() ;
    }

}
