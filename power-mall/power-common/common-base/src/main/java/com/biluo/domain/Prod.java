package com.biluo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
    * 商品
    */
@ApiModel(value="com-biluo-domain-Prod")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "prod")
public class Prod implements Serializable {
    /**
     * 产品ID
     */
    @TableId(value = "prod_id", type = IdType.AUTO)
    @ApiModelProperty(value="产品ID")
    private Long prodId;

    /**
     * 商品名称
     */
    @TableField(value = "prod_name")
    @ApiModelProperty(value="商品名称")
    private String prodName;

    /**
     * 店铺id
     */
    @TableField(value = "shop_id")
    @ApiModelProperty(value="店铺id")
    private Long shopId;

    /**
     * 原价
     */
    @TableField(value = "ori_price")
    @ApiModelProperty(value="原价")
    private BigDecimal oriPrice;

    /**
     * 现价
     */
    @TableField(value = "price")
    @ApiModelProperty(value="现价")
    private BigDecimal price;

    /**
     * 简要描述,卖点等
     */
    @TableField(value = "brief")
    @ApiModelProperty(value="简要描述,卖点等")
    private String brief;

    /**
     * 详细描述
     */
    @TableField(value = "content")
    @ApiModelProperty(value="详细描述")
    private String content;

    /**
     * 商品主图
     */
    @TableField(value = "pic")
    @ApiModelProperty(value="商品主图")
    private String pic;

    /**
     * 商品图片，以,分割
     */
    @TableField(value = "imgs")
    @ApiModelProperty(value="商品图片，以,分割")
    private String imgs;

    /**
     * 默认是1，表示正常状态, -1表示删除, 0下架
     */
    @TableField(value = "status")
    @ApiModelProperty(value="默认是1，表示正常状态, -1表示删除, 0下架")
    private Integer status;

    /**
     * 商品分类
     */
    @TableField(value = "category_id")
    @ApiModelProperty(value="商品分类")
    private Long categoryId;

    /**
     * 销量
     */
    @TableField(value = "sold_num")
    @ApiModelProperty(value="销量")
    private Integer soldNum;

    /**
     * 总库存
     */
    @TableField(value = "total_stocks")
    @ApiModelProperty(value="总库存")
    private Integer totalStocks;

    /**
     * 配送方式json见TransportModeVO
     */
    @TableField(value = "delivery_mode")
    @ApiModelProperty(value="配送方式json见TransportModeVO")
    private String deliveryMode;

    /**
     * 运费模板id
     */
    @TableField(value = "delivery_template_id")
    @ApiModelProperty(value="运费模板id")
    private Long deliveryTemplateId;

    /**
     * 录入时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value="录入时间")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField(value = "update_time")
    @ApiModelProperty(value="修改时间")
    private Date updateTime;

    /**
     * 上架时间
     */
    @TableField(value = "putaway_time")
    @ApiModelProperty(value="上架时间")
    private Date putawayTime;

    /**
     * 版本 乐观锁
     */
    @TableField(value = "version")
    @ApiModelProperty(value="版本 乐观锁")
    private Integer version;

    ///////////////////////////////////////////////////
    @TableField(exist = false)
    private DeliveryModeVo deliveryModeVo;

    @TableField(exist = false)
    private List<Sku> skuList;

    @TableField(exist = false)
    private List<Long> tagList;

    private static final long serialVersionUID = 1L;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class DeliveryModeVo {
        private Boolean hasShopDelivery;
        private Boolean hasUserPickUp;
    }
}
