package com.biluo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
    * 订单项
    */
@ApiModel(value="com-biluo-domain-OrderItem")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "order_item")
public class OrderItem implements Serializable {
    /**
     * 订单项ID
     */
    @TableId(value = "order_item_id", type = IdType.AUTO)
    @ApiModelProperty(value="订单项ID")
    private Long orderItemId;

    /**
     * 店铺id
     */
    @TableField(value = "shop_id")
    @ApiModelProperty(value="店铺id")
    private Long shopId;

    /**
     * 订单order_number
     */
    @TableField(value = "order_number")
    @ApiModelProperty(value="订单order_number")
    private String orderNumber;

    /**
     * 产品ID
     */
    @TableField(value = "prod_id")
    @ApiModelProperty(value="产品ID")
    private Long prodId;

    /**
     * 产品SkuID
     */
    @TableField(value = "sku_id")
    @ApiModelProperty(value="产品SkuID")
    private Long skuId;

    /**
     * 购物车产品个数
     */
    @TableField(value = "prod_count")
    @ApiModelProperty(value="购物车产品个数")
    private Integer prodCount;

    /**
     * 产品名称
     */
    @TableField(value = "prod_name")
    @ApiModelProperty(value="产品名称")
    private String prodName;

    /**
     * sku名称
     */
    @TableField(value = "sku_name")
    @ApiModelProperty(value="sku名称")
    private String skuName;

    /**
     * 产品主图片路径
     */
    @TableField(value = "pic")
    @ApiModelProperty(value="产品主图片路径")
    private String pic;

    /**
     * 产品价格
     */
    @TableField(value = "price")
    @ApiModelProperty(value="产品价格")
    private BigDecimal price;

    /**
     * 商品总金额
     */
    @TableField(value = "product_total_amount")
    @ApiModelProperty(value="商品总金额")
    private BigDecimal productTotalAmount;

    /**
     * 购物时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value="购物时间")
    private Date createTime;

    /**
     * 评论状态： 0 未评价  1 已评价
     */
    @TableField(value = "comm_sts")
    @ApiModelProperty(value="评论状态： 0 未评价  1 已评价")
    private Integer commSts;

    private static final long serialVersionUID = 1L;
}
