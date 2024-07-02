package com.biluo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
    * 产品类目
    */
@ApiModel(value="com-biluo-domain-Category")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "category")
public class Category implements Serializable {
    /**
     * 类目ID
     */
    @TableId(value = "category_id", type = IdType.AUTO)
    @ApiModelProperty(value="类目ID")
    private Long categoryId;

    /**
     * 父节点
     */
    @TableField(value = "parent_id")
    @ApiModelProperty(value="父节点")
    private Long parentId;

    /**
     * 产品类目名称
     */
    @TableField(value = "category_name")
    @ApiModelProperty(value="产品类目名称")
    private String categoryName;

    /**
     * 类目图标
     */
    @TableField(value = "icon")
    @ApiModelProperty(value="类目图标")
    private String icon;

    /**
     * 类目的显示图片
     */
    @TableField(value = "pic")
    @ApiModelProperty(value="类目的显示图片")
    private String pic;

    /**
     * 排序
     */
    @TableField(value = "seq")
    @ApiModelProperty(value="排序")
    private Integer seq;

    /**
     * 默认是1，表示正常状态,0为下线状态
     */
    @TableField(value = "status")
    @ApiModelProperty(value="默认是1，表示正常状态,0为下线状态")
    private Integer status;

    /**
     * 记录时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value="记录时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    @ApiModelProperty(value="更新时间")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
