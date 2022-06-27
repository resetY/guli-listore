package com.yuan.guli.service;

import com.yuan.guli.guliproduct.entity.CategoryEntity;
import com.yuan.guli.guliproduct.service.CategoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CategoryTest {
    @Autowired
    private CategoryService categoryService;
    @Test
    public  void test1(){
        List<CategoryEntity> categoryEntities = categoryService.listWithTree();
        categoryEntities.forEach(System.out::println);
    }
}
