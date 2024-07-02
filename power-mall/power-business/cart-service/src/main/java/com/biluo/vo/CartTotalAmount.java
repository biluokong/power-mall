package com.biluo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 购物车商品总金额对象
 */
@ApiModel("购物车商品总金额对象")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartTotalAmount {

    @ApiModelProperty("合计")
    private BigDecimal finalMoney = BigDecimal.ZERO;

    @ApiModelProperty("总额")
    private BigDecimal totalMoney = BigDecimal.ZERO;

    @ApiModelProperty("优惠金额")
    private BigDecimal subtractMoney = BigDecimal.ZERO;

    @ApiModelProperty("运费")
    private BigDecimal transMoney = BigDecimal.ZERO;
}
