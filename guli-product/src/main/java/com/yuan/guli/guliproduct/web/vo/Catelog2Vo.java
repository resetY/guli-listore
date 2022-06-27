package com.yuan.guli.guliproduct.web.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Catelog2Vo {

    private  String catalog1Id;
    private List<Catelog3Vo> catalog3List; //三级子分类
    private  String id; //当前节点id
    private String name;//当前节点name

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Catelog3Vo{
        private String catalog2Id;
        private String id;
        private  String name;
    }

}
