package com.biluo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 商品评论总览信息对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("商品评论总览信息对象")
public class ProdCommData {

    @ApiModelProperty("商品好评率")
    private BigDecimal goodLv;
    @ApiModelProperty("商品评论总数量")
    private Long allCount;
    @ApiModelProperty("商品好评数量")
    private Long goodCount;
    @ApiModelProperty("商品中评数量")
    private Long secondCount;
    @ApiModelProperty("商品差评数量")
    private Long badCount;
    @ApiModelProperty("商品有图评论数量")
    private Long picCount;
}
