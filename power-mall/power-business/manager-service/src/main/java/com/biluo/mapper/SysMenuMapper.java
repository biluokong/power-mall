package com.biluo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.biluo.domain.SysMenu;

import java.util.Set;

public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 根据用户标识查询菜单权限集合
     * @param loginUserId
     * @return
     */
    Set<SysMenu> selectUserMenuListByUserId(Long loginUserId);
}
