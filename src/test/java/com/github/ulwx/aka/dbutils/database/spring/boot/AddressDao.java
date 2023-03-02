package com.github.ulwx.aka.dbutils.database.spring.boot;

import com.github.ulwx.aka.dbutils.database.multids.AkaDS;
import com.github.ulwx.aka.dbutils.database.spring.MDataBaseTemplate;
import com.github.ulwx.aka.dbutils.tool.MD;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AddressDao {

    private MDataBaseTemplate mDataBaseTemplate1;
    private MDataBaseTemplate mDataBaseTemplate2;
    public void init(){
        mDataBaseTemplate1.exeScript("/","test.sql",
                false,true,";","utf-8");
        mDataBaseTemplate2.exeScript("/","test2.sql",
                false,true,";","utf-8");
    }
    public AddressDao(MDataBaseTemplate mDataBaseTemplate1,MDataBaseTemplate mDataBaseTemplate2){
        this.mDataBaseTemplate1=mDataBaseTemplate1;
        this.mDataBaseTemplate2=mDataBaseTemplate2;
    }

    public List<Address> getListMd1(){
        Map<String, Object> mp=new HashMap<>();

        List<Address> list=mDataBaseTemplate1.queryList(Address.class,
                MD.md(),mp);
        return list;

    }
    public List<Address> getListMd2(){
        Map<String, Object> mp=new HashMap<>();

        List<Address> list=mDataBaseTemplate2.queryList(Address.class,
                MD.md(),mp);
        return list;

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
