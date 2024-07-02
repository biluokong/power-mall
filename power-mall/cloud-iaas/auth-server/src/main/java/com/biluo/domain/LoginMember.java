package com.biluo.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
    * 用户表
    */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "`member`")
public class LoginMember implements Serializable {
    /**
     * 会员表的主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * ID
     */
    @TableField(value = "open_id")
    private String openId;


    /**
     * 修改时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 注册时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 注册IP
     */
    @TableField(value = "user_regip")
    private String userRegip;

    /**
     * 最后登录时间
     */
    @TableField(value = "user_lasttime")
    private Date userLasttime;

    /**
     * 最后登录IP
     */
    @TableField(value = "user_lastip")
    private String userLastip;
    /**
     * 状态 1 正常 0 无效
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 用户积分
     */
    @TableField(value = "score")
    private Integer score;

    private static final long serialVersionUID = 1L;
}
