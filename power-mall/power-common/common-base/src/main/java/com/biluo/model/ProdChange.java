package com.biluo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品prod购买数量对象
 */
@ApiModel("商品prod购买数量对象")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProdChange {

    @ApiModelProperty("商品id")
    private Long prodId;

    @ApiModelProperty("商品购买数量")
    private Integer count;
}
