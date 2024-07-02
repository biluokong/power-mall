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
import java.util.Set;

/**
    * 菜单管理
    */
@ApiModel(value="com-biluo-domain-SysMenu")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_menu")
public class SysMenu implements Serializable {
    @TableId(value = "menu_id", type = IdType.AUTO)
    @ApiModelProperty(value="")
    private Long menuId;

    /**
     * 父菜单ID，一级菜单为0
     */
    @TableField(value = "parent_id")
    @ApiModelProperty(value="父菜单ID，一级菜单为0")
    private Long parentId;

    /**
     * 菜单名称
     */
    @TableField(value = "name")
    @ApiModelProperty(value="菜单名称")
    private String name;

    /**
     * 菜单URL
     */
    @TableField(value = "url")
    @ApiModelProperty(value="菜单URL")
    private String url;

    /**
     * 授权(多个用逗号分隔，如：user:list,user:create)
     */
    @TableField(value = "perms")
    @ApiModelProperty(value="授权(多个用逗号分隔，如：user:list,user:create)")
    private String perms;

    /**
     * 类型   0：目录   1：菜单   2：按钮
     */
    @TableField(value = "type")
    @ApiModelProperty(value="类型   0：目录   1：菜单   2：按钮")
    private Integer type;

    /**
     * 菜单图标
     */
    @TableField(value = "icon")
    @ApiModelProperty(value="菜单图标")
    private String icon;

    /**
     * 排序
     */
    @TableField(value = "order_num")
    @ApiModelProperty(value="排序")
    private Integer orderNum;


    // 子节点集合
    @TableField(exist = false)
    @ApiModelProperty("子节点集合")
    private Set<SysMenu> list;

    private static final long serialVersionUID = 1L;
}
