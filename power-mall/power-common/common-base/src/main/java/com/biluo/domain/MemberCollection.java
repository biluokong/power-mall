package com.biluo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value="com-biluo-domain-MemberCollection")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "member_collection")
public class MemberCollection implements Serializable {
    /**
     * 收藏表
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value="收藏表")
    private Long id;

    /**
     * 商品id
     */
    @TableField(value = "prod_id")
    @ApiModelProperty(value="商品id")
    private Long prodId;

    /**
     * 用户id
     */
    @TableField(value = "open_id")
    @ApiModelProperty(value="用户id")
    private String openId;

    /**
     * 收藏时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value="收藏时间")
    private Date createTime;

    private static final long serialVersionUID = 1L;
}
