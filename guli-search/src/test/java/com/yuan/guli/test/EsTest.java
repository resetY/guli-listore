package com.yuan.guli.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class EsTest {
    @Test
    public void  PriceTest(){
        String str = "1500_5000";
        String str2 = "1500_";
        String str3 = "_500";
        String[] s = str.split("_"); //按照判断_分隔开字段
        System.out.println(s.length);  //结果为2
        System.out.println();
        System.out.println(s[0]);  //索引从0开始,结果为1500
        System.out.println(s[1]);   //结果为5000

        String[] s2 = str2.split("_"); //按照判断_分隔开字段
        System.out.println(s2[0]);
        System.out.println(s2.length);
        String[] s3 = str3.split("_"); //按照判断_分隔开字段
        System.out.println(s3[1]);  //s[0]输出为"空"
        System.out.println(s3.length);

    }
}
