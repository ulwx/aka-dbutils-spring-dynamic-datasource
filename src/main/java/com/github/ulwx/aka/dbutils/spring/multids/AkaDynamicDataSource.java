package com.github.ulwx.aka.dbutils.spring.multids;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class AkaDynamicDataSource extends AkaAbstractRoutingDataSource {
    private final Map<String,DataSourceStat> statMap=new ConcurrentHashMap<>();
    private final Set<String> groupNames=new ConcurrentSkipListSet<>();

    public AkaDynamicDataSource() {
    }

    @Override
    public  String determineCurrentLookupKey() {
        String lookupKey= AkaDataSourceContext.get();
        if(lookupKey==null || lookupKey.trim().isEmpty()){
            lookupKey=this.getDefaultTargetDataSourceName();
        }
        if(lookupKey==null || lookupKey.trim().isEmpty()){
            throw new RuntimeException("不能根据lookupKey找到数据源！[determineCurrentLookupKey()返回空]");

        }
        return lookupKey;
    }


    public Set<String> getDataSourcesByGroupName(String groupName){
        Set<String> list = new TreeSet<>();
        for(String key: this.getTargetDataSources().keySet()){
            if(key.startsWith(groupName)){
                list.add(key);
            }
        }
        return list;
    }
    public void IncreaseErr(){
        String dsName=this.determineCurrentLookupKey();

        statMap.get(dsName).inc();
    }


    @Override
    public Connection getConnection() throws SQLException {
        return new AkaProxyConnection(this);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public Set<String> getGroupNames() {
        return groupNames;
    }

    @Override
    public void afterPropertiesSet() {
        Map<String, DataSource> map = this.getTargetDataSources();
        for(String key: map.keySet()){
            DataSource dataSource=map.get(key);
            statMap.put(key.toString(),new DataSourceStat(dataSource));
            if(key.contains("--")){
                groupNames.add(key.substring(0,key.indexOf("--")));
            }
        }
        if(this.getDefaultTargetDataSourceName()!=null && !this.getDefaultTargetDataSourceName().trim().isEmpty()) {
            if (map.get(this.getDefaultTargetDataSourceName()) == null) {
                throw new RuntimeException("设置的默认数据源名称" +this.getDefaultTargetDataSourceName()+
                        "在targetDataSources里没有找到！");
            }
        }
    }

}

