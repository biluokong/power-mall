package com.biluo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 购物车店铺对象
 */
@ApiModel("购物车店铺对象")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopCart {

    @ApiModelProperty("店铺标识")
    private Long shopId;

    @ApiModelProperty("商品条目对象集合")
    private List<CartItem> shopCartItems;
}
