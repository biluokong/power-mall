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
    * 商品分组表
    */
@ApiModel(value="com-biluo-domain-ProdTag")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "prod_tag")
public class ProdTag implements Serializable {
    /**
     * 分组标签id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value="分组标签id")
    private Long id;

    /**
     * 分组标题
     */
    @TableField(value = "title")
    @ApiModelProperty(value="分组标题")
    private String title;

    /**
     * 状态(1为正常,0为删除)
     */
    @TableField(value = "status")
    @ApiModelProperty(value="状态(1为正常,0为删除)")
    private Integer status;

    /**
     * 列表样式(0:一列一个,1:一列两个,2:一列三个)
     */
    @TableField(value = "style")
    @ApiModelProperty(value="列表样式(0:一列一个,1:一列两个,2:一列三个)")
    private Integer style;

    /**
     * 排序
     */
    @TableField(value = "seq")
    @ApiModelProperty(value="排序")
    private Integer seq;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value="创建时间")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField(value = "update_time")
    @ApiModelProperty(value="修改时间")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
