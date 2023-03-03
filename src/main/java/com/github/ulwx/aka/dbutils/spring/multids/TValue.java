package com.github.ulwx.aka.dbutils.database.multids;

public class TValue<T> {
    T Object;

    public TValue(T value) {
        Object = value;
    }

    public void setValue(T value){
        this.Object=value;
    }

    public T getValue(){
        return Object;
    }
}
