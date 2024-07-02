package com.biluo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
    * 单品SKU表
    */
@ApiModel(value="com-biluo-domain-Sku")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sku")
public class Sku implements Serializable {
    /**
     * 单品ID
     */
    @TableId(value = "sku_id", type = IdType.AUTO)
    @ApiModelProperty(value="单品ID")
    private Long skuId;

    /**
     * 商品ID
     */
    @TableField(value = "prod_id")
    @ApiModelProperty(value="商品ID")
    private Long prodId;

    /**
     * 销售属性组合字符串 格式是p1:v1;p2:v2
     */
    @TableField(value = "properties")
    @ApiModelProperty(value="销售属性组合字符串 格式是p1:v1;p2:v2")
    private String properties;

    /**
     * 原价
     */
    @TableField(value = "ori_price")
    @ApiModelProperty(value="原价")
    private BigDecimal oriPrice;

    /**
     * 价格
     */
    @TableField(value = "price")
    @ApiModelProperty(value="价格")
    private BigDecimal price;

    /**
     * 商品在付款减库存的状态下，该sku上未付款的订单数量
     */
    @TableField(value = "stocks")
    @ApiModelProperty(value="商品在付款减库存的状态下，该sku上未付款的订单数量")
    private Integer stocks;

    /**
     * 实际库存
     */
    @TableField(value = "actual_stocks")
    @ApiModelProperty(value="实际库存")
    private Integer actualStocks;

    /**
     * 修改时间
     */
    @TableField(value = "update_time")
    @ApiModelProperty(value="修改时间")
    private Date updateTime;

    /**
     * 记录时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value="记录时间")
    private Date createTime;

    /**
     * 商品条形码
     */
    @TableField(value = "model_id")
    @ApiModelProperty(value="商品条形码")
    private String modelId;

    /**
     * sku图片
     */
    @TableField(value = "pic")
    @ApiModelProperty(value="sku图片")
    private String pic;

    /**
     * sku名称
     */
    @TableField(value = "sku_name")
    @ApiModelProperty(value="sku名称")
    private String skuName;

    /**
     * 商品名称
     */
    @TableField(value = "prod_name")
    @ApiModelProperty(value="商品名称")
    private String prodName;

    /**
     * 版本号
     */
    @TableField(value = "version")
    @ApiModelProperty(value="版本号")
    private Integer version;

    /**
     * 商品重量
     */
    @TableField(value = "weight")
    @ApiModelProperty(value="商品重量")
    private Double weight;

    /**
     * 商品体积
     */
    @TableField(value = "volume")
    @ApiModelProperty(value="商品体积")
    private Double volume;

    /**
     * 0 禁用 1 启用
     */
    @TableField(value = "status")
    @ApiModelProperty(value="0 禁用 1 启用")
    private Integer status;

    private static final long serialVersionUID = 1L;
}
