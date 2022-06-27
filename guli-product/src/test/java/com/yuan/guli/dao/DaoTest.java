package com.yuan.guli.dao;

import com.yuan.guli.guliproduct.dao.AttrGroupDao;
import com.yuan.guli.guliproduct.dao.SkuSaleAttrValueDao;
import com.yuan.guli.guliproduct.vo.SkuItemVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class DaoTest {

    @Autowired
    AttrGroupDao attrGroupDao;
    @Test
    public void test(){
        List<SkuItemVo.SpuItemAttrGroupAttrVo> A = attrGroupDao.getAttrGroupWithAttrsBySpuid(225L, 37L);
        A.forEach(System.out::println);
    }
    @Autowired
    SkuSaleAttrValueDao attrValueDao;
    @Test
    public void test2(){
        List<SkuItemVo.SkuItemSaleAttrVo> A = attrValueDao.getSaleAttrsBySpuid(37L);
        A.forEach(System.out::println);
    }
}
