package com.biluo.vo;

import com.biluo.domain.MemberAddr;
import com.biluo.model.ShopOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单确认页面对象
 */
@ApiModel("订单确认页面对象")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderVo {

    @ApiModelProperty("会员收货地址对象")
    private MemberAddr memberAddr;

    @ApiModelProperty("订单店铺对象集合")
    private List<ShopOrder> shopCartOrders;

    @ApiModelProperty("商品总数量")
    private Integer totalCount;

    @ApiModelProperty("合计|订单总额")
    private BigDecimal total = BigDecimal.ZERO;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("运费")
    private BigDecimal transfee = BigDecimal.ZERO;

    @ApiModelProperty("优惠金额")
    private BigDecimal shopReduce = BigDecimal.ZERO;

    @ApiModelProperty("小计")
    private BigDecimal actualTotal = BigDecimal.ZERO;

    @ApiModelProperty("订单请求来源，0商品详情页面，1购物车页面")
    private Integer source = 0;
}
