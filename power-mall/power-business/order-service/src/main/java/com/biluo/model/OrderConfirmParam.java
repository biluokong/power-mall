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
 * 订单确认页面参数对象
 */
@ApiModel("订单确认页面参数对象")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderConfirmParam {

    /**
     * 接收请求来自于商品详情页面的参数
     */
    @ApiModelProperty("订单商品条目对象(接收请求来自于商品详情页面的参数)")
    private OrderItem orderItem;

    @ApiModelProperty("购物车id集合(接收请求来自于购物车页面参数)")
    private List<Long> basketIds;
}
