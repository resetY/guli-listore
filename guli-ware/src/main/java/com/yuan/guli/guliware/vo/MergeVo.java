package com.yuan.guli.guliware.vo;

import lombok.Data;

import java.util.List;

//合并采购需求的类对象
@Data
public class MergeVo {
    private Long purchaseId; //采购单id
    private List<Long> items; //合并项集合
}
