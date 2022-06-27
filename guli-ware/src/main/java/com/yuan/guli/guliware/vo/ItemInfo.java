package com.yuan.guli.guliware.vo;

import lombok.Data;

@Data
public class ItemInfo {

   private Long  itemId; //采购项id
   private Integer  status; //状态
    private  String reason;//状态的原因
}
