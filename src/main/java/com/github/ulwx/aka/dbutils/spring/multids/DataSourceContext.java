package com.github.ulwx.aka.dbutils.database.multids;

import java.util.Stack;

public class DataSourceContext {

    private final static ThreadLocal<Stack<String>> LOCAL_DATASOURCE =
            new ThreadLocal<Stack<String>>(){
                @Override
                protected Stack<String> initialValue() {
                    return new Stack<>();
                }
            };

    public static void push(String name) {
        LOCAL_DATASOURCE.get().push(name);
    }

    public static String get() {
        return LOCAL_DATASOURCE.get().size()==0?null:LOCAL_DATASOURCE.get().peek();
    }

    public static void pop() {
        LOCAL_DATASOURCE.get().pop();
    }
}
