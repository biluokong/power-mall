package com.biluo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 购物车商品条目对象
 */
@ApiModel("购物车商品条目对象")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItem {

    @ApiModelProperty("商品id")
    private Long prodId;

    @ApiModelProperty("商品skuId")
    private Long skuId;

    @ApiModelProperty("商品图片")
    private String pic;

    @ApiModelProperty("商品名称")
    private String prodName;

    @ApiModelProperty("商品sku名称")
    private String skuName;

    @ApiModelProperty("商品单价")
    private BigDecimal price;

    @ApiModelProperty("购物车id")
    private Long basketId;

    @ApiModelProperty("商品购买数量")
    private Integer prodCount;


}
