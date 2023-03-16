package com.github.ulwx.aka.dbutils.database.spring.boot;

import com.github.ulwx.aka.dbutils.database.spring.MDataBaseTemplate;
import com.github.ulwx.aka.dbutils.spring.multids.AkaDS;
import com.github.ulwx.aka.dbutils.tool.MD;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Component
public class AddressDao {

    private MDataBaseTemplate mDataBaseTemplate1;
    private MDataBaseTemplate mDataBaseTemplate2;
    public void init(){
        mDataBaseTemplate1.exeScript("","test.sql",
                false,true,";","utf-8");
        mDataBaseTemplate2.exeScript("","test.sql",
                false,true,";","utf-8");
    }
    public AddressDao(MDataBaseTemplate mDataBaseTemplate1,MDataBaseTemplate mDataBaseTemplate2){
        this.mDataBaseTemplate1=mDataBaseTemplate1;
        this.mDataBaseTemplate2=mDataBaseTemplate2;
    }

    public Address getListMd1(){
        Map<String, Object> mp=new HashMap<>();
        mp.put("id",1);
        Address address=mDataBaseTemplate1.queryOne(Address.class,
                MD.md(),mp);
        return address;

    }
    public Address getListMd2(){
        Map<String, Object> mp=new HashMap<>();
        mp.put("id",2);
        Address address=mDataBaseTemplate1.queryOne(Address.class,
                MD.md(),mp);
        return address;

    }
    @Transactional(propagation = Propagation.REQUIRED)
    @AkaDS("dataSource1")
    public void updateMd1(int id,String name){
        Map<String, Object> mp=new HashMap<>();
        mp.put("name",name);
        mp.put("id",id);
        mDataBaseTemplate1.update(MD.md(),mp);

    }
    @Transactional(propagation = Propagation.NESTED)
    @AkaDS("dataSource2")
    public void updateMd2(int id,String name){
        Map<String, Object> mp=new HashMap<>();
        mp.put("name",name);
        mp.put("id",id);
        mDataBaseTemplate2.update(MD.md(),mp);
        throw new RuntimeException();

    }
}
