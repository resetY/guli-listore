package com.yuan.guli.service;

import com.sun.media.jfxmedia.logging.Logger;
import com.yuan.guli.guliproduct.entity.AttrGroupEntity;
import com.yuan.guli.guliproduct.service.AttrGroupService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AttrGroupTest {

    @Autowired
    AttrGroupService attrGroupService;

    @Test
    public void test(){
        AttrGroupEntity attrgoup = attrGroupService.queryByPath(1L);
        System.out.println("完整路径:"+attrgoup);
    }
}
