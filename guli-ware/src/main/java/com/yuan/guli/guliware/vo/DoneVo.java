package com.yuan.guli.guliware.vo;

import com.yuan.guli.guliware.entity.PurchaseDetailEntity;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class DoneVo {
    @NotNull
    private Long id;  //采购单id
    private List<ItemInfo>items;
}
