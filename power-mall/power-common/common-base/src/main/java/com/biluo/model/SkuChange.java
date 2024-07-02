package com.biluo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品sku购买数量对象
 */
@ApiModel("商品sku购买数量对象")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SkuChange {

    @ApiModelProperty("商品skuId")
    private Long skuId;

    @ApiModelProperty("商品购买数量")
    private Integer count;
}
