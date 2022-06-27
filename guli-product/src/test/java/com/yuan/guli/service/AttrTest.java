package com.yuan.guli.service;

import com.yuan.guli.guliproduct.service.AttrService;
import com.yuan.guli.guliproduct.vo.AttrRespVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AttrTest {

    @Autowired
    AttrService attrService;
    @Test
    public void test(){
        AttrRespVo info = attrService.getInfo(2L);
        System.out.println(info);
    }
}
