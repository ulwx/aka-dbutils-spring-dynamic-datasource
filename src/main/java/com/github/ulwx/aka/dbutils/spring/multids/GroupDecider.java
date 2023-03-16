package com.github.ulwx.aka.dbutils.spring.multids;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract  class GroupDecider {
    public static Map<String,GroupDecider> map=new HashMap();
    static{
        map.put(RandomGroupDecider.instance.getType(),RandomGroupDecider.instance);
    }
    abstract public String decide(List<String> dsList);

    abstract public String getType();

    public static GroupDecider getDecider(String type){
        return map.get(type);
    }

}
