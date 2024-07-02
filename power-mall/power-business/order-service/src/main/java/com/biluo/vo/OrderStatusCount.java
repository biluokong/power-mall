package com.biluo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单状态数量对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("订单状态数量对象")
public class OrderStatusCount {

    @ApiModelProperty("待支付数量")
    private Long unPay;

    @ApiModelProperty("待发货数量")
    private Long payed;

    @ApiModelProperty("待收货数量")
    private Long consignment;
}
