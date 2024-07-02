package com.biluo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 商品prod和sku购买数量对象
 */
@ApiModel("商品prod和sku购买数量对象")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeStock {

    @ApiModelProperty("商品prod购买数量对象集合")
    private List<ProdChange> prodChangeList;

    @ApiModelProperty("商品sku购买数量对象集合")
    private List<SkuChange> skuChangeList;
}
