package com.biluo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value="com-biluo-domain-ProdPropValue")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "prod_prop_value")
public class ProdPropValue implements Serializable {
    /**
     * 属性值ID
     */
    @TableId(value = "value_id", type = IdType.AUTO)
    @ApiModelProperty(value="属性值ID")
    private Long valueId;

    /**
     * 属性值名称
     */
    @TableField(value = "prop_value")
    @ApiModelProperty(value="属性值名称")
    private String propValue;

    /**
     * 属性ID
     */
    @TableField(value = "prop_id")
    @ApiModelProperty(value="属性ID")
    private Long propId;

    private static final long serialVersionUID = 1L;
}
