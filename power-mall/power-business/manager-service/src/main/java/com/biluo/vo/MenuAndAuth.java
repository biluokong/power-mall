package com.biluo.vo;

import com.biluo.domain.SysMenu;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 菜单和操作权限对象
 */
@ApiModel("菜单和操作权限对象")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuAndAuth {

    @ApiModelProperty("菜单权限集合")
    private Set<SysMenu> menuList;
    @ApiModelProperty("操作权限集合")
    private Set<String> authorities;
}
