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

/**
    * 用户表
    */
@ApiModel(value="com-biluo-domain-Member")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "`member`")
public class Member implements Serializable {
    /**
     * 会员表的主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value="会员表的主键")
    private Integer id;

    /**
     * ID
     */
    @TableField(value = "open_id")
    @ApiModelProperty(value="ID")
    private String openId;

    /**
     * 用户昵称
     */
    @TableField(value = "nick_name")
    @ApiModelProperty(value="用户昵称")
    private String nickName;

    /**
     * 真实姓名
     */
    @TableField(value = "real_name")
    @ApiModelProperty(value="真实姓名")
    private String realName;

    /**
     * 用户邮箱
     */
    @TableField(value = "user_mail")
    @ApiModelProperty(value="用户邮箱")
    private String userMail;

    /**
     * 支付密码
     */
    @TableField(value = "pay_password")
    @ApiModelProperty(value="支付密码")
    private String payPassword;

    /**
     * 手机号码
     */
    @TableField(value = "user_mobile")
    @ApiModelProperty(value="手机号码")
    private String userMobile;

    /**
     * 修改时间
     */
    @TableField(value = "update_time")
    @ApiModelProperty(value="修改时间")
    private Date updateTime;

    /**
     * 注册时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value="注册时间")
    private Date createTime;

    /**
     * 注册IP
     */
    @TableField(value = "user_regip")
    @ApiModelProperty(value="注册IP")
    private String userRegip;

    /**
     * 最后登录时间
     */
    @TableField(value = "user_lasttime")
    @ApiModelProperty(value="最后登录时间")
    private Date userLasttime;

    /**
     * 最后登录IP
     */
    @TableField(value = "user_lastip")
    @ApiModelProperty(value="最后登录IP")
    private String userLastip;

    /**
     * M(男) or F(女)
     */
    @TableField(value = "sex")
    @ApiModelProperty(value="M(男) or F(女)")
    private String sex;

    /**
     * 例如：2009-11-27
     */
    @TableField(value = "birth_date")
    @ApiModelProperty(value="例如：2009-11-27")
    private String birthDate;

    /**
     * 头像图片路径
     */
    @TableField(value = "pic")
    @ApiModelProperty(value="头像图片路径")
    private String pic;

    /**
     * 状态 1 正常 0 无效
     */
    @TableField(value = "status")
    @ApiModelProperty(value="状态 1 正常 0 无效")
    private Integer status;

    /**
     * 用户积分
     */
    @TableField(value = "score")
    @ApiModelProperty(value="用户积分")
    private Integer score;

    private static final long serialVersionUID = 1L;
}
