package com.github.ulwx.aka.dbutils.database.spring.boot;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApplicationTest {
    @Test
    public  void test() throws Exception{
        AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext(MyConfiguration.class);
        MyService myService=(MyService)ctx.getBean("myService");//AddressDao

        myService.init();
        myService.updateMdb();
        AddressDao addressDao=(AddressDao)ctx.getBean("addressDao");
        Address address1=addressDao.getListMd1();
        Address address2= addressDao.getListMd2();
        Assert.assertTrue(address1.getAddressId()==1 && address1.getName().equals("123"));
        Assert.assertTrue(address2.getAddressId()==2 && address2.getName().equals("2"));
        ctx.close();

        int i=0;

    }


}
