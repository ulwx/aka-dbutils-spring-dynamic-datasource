package com.github.ulwx.aka.dbutils.spring.multids;

import java.lang.reflect.Method;

public class ReflectionUtil {



    /**
     * 通过反射调用对象的方法
     *
     * @param object
     * @param methodName
     * @param argTypes
     * @param argValues
     */
    public static Object invoke(Object object, String methodName,
                                Class<?>[] argTypes, Object[] argValues) throws
            Exception {
        // 获取obj类的字节文件对象
        Class c=null;
        if(object instanceof Class){
            c=(Class)object;
            object=null;
        }else {
            c = object.getClass();
        }
        // 获取该类的成员变量
        Method method = c.getMethod(methodName, argTypes);
        return method.invoke(object, argValues);
    }

    public static Object invoke(Object object, String methodName,
                                Class<?> argType, Object argValue) throws
            Exception {
        Class c=null;
        if(object instanceof Class){
            c=(Class)object;
            object=null;
        }else {
            c = object.getClass();
        }
        // 获取该类的成员变量
        Method method = c.getMethod(methodName, argType);
        return method.invoke(object, argValue);
    }

    public static Object invoke(Object object, String methodName) throws
            Exception {
        Class c=null;
        if(object instanceof Class){
            c=(Class)object;
            object=null;
        }else {
            c = object.getClass();
        }
        // 获取该类的成员变量
        Method method = c.getMethod(methodName);
        return method.invoke(object);
    }



    public static Object invoke(Object object, String methodName,
                                String argClassName, Object argValue) throws
            Exception {
        Class c=null;
        if(object instanceof Class){
            c=(Class)object;
            object=null;
        }else {
            c = object.getClass();
        }
        // 获取该类的成员变量
        Class<?> argType = Class.forName(argClassName);
        Method method = c.getMethod(methodName, argType);
        return method.invoke(object, argValue);
    }

}
