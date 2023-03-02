package com.github.ulwx.aka.dbutils.database.spring.boot;

import com.github.ulwx.aka.dbutils.database.multids.AkaDS;
import com.github.ulwx.aka.dbutils.database.spring.MDataBaseTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyService.class);
    private AddressDao addressDao;

    @Autowired
    private MDataBaseTemplate mDataBaseTemplate1;

    public void init(){
        mDataBaseTemplate1.exeScript("","test.sql",false,true,";","utf-8");
    }
    public MyService(AddressDao addressDao) {
        this.addressDao = addressDao;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @AkaDS("dataSource1")
    public void updateMdb(){

       // List<Address> list2= addressDao.getListMd();

        addressDao.updateMd1(1,"123");


        try {
            addressDao.updateMd2(2, "abc");
        }catch (Exception ex){
            LOGGER.debug(ex+"");
        }
       //MyService方法的内部调用会使用被调用方法上声明的事务失效，所以需要用下面方式调用
        int i=0;


    }



}
