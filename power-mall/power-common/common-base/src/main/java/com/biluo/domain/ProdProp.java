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
import java.util.List;

@ApiModel(value="com-biluo-domain-ProdProp")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "prod_prop")
public class ProdProp implements Serializable {
    /**
     * 属性id
     */
    @TableId(value = "prop_id", type = IdType.AUTO)
    @ApiModelProperty(value="属性id")
    private Long propId;

    /**
     * 属性名称
     */
    @TableField(value = "prop_name")
    @ApiModelProperty(value="属性名称")
    private String propName;

    /**
     * ProdPropRule 1:销售属性(规格); 2:参数属性;
     */
    @TableField(value = "rule")
    @ApiModelProperty(value="ProdPropRule 1:销售属性(规格); 2:参数属性;")
    private Integer rule;

    /**
     * 店铺id
     */
    @TableField(value = "shop_id")
    @ApiModelProperty(value="店铺id")
    private Long shopId;

    /////////////// 多条件分页查询 //////////////////////
    @TableField(exist = false)
    @ApiModelProperty("属性值集合")
    private List<ProdPropValue> prodPropValues;

    private static final long serialVersionUID = 1L;
}
