package com.biluo.vo;

import com.biluo.model.ShopCart;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 购物车对象
 */
@ApiModel("购物车对象")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartVo {

    @ApiModelProperty("购物车店铺对象集合")
    private List<ShopCart> shopCarts;
}
