package com.biluo.model;

import com.biluo.domain.OrderItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 订单店铺对象
 */
@ApiModel("订单店铺对象")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopOrder {

    @ApiModelProperty("店铺标识")
    private Long shopId;

    @ApiModelProperty("订单商品条目对象集合")
    private List<OrderItem> shopOrderItems;
}
