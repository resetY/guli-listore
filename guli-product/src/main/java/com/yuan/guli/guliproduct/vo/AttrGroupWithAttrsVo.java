package com.yuan.guli.guliproduct.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.yuan.guli.guliproduct.entity.AttrEntity;
import lombok.Data;

import java.util.List;

@Data
public class AttrGroupWithAttrsVo {

    /**
     * 分组id
     */
    @TableId
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */

    private Long catelogId;

    /**
     * 获取菜单路径
     * */
    @TableField(exist = false)
    private Long[] catelogPath;

    /**
    Attr的数据,：封装Attr的所有数据
     **/
  private List<AttrEntity> attrs;

}
