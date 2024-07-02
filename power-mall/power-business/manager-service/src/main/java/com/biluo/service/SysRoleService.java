package com.biluo.service;

import com.biluo.domain.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysRoleService extends IService<SysRole>{


    /**
     * 查询系统所有角色
     * @return
     */
    List<SysRole> querySysRoleList();

    /**
     * 新增角色
     * @param sysRole
     * @return
     */
    void saveSysRole(SysRole sysRole);

    /**
     * 根据标识查询角色详情
     * @param roleId
     * @return
     */
    SysRole querySysRoleInfoByRoleId(Long roleId);

    /**
     * 修改角色信息
     * @param sysRole
     * @return
     */
    void modifySysRole(SysRole sysRole);

    /**
     * 批量/单个删除角色
     * @param roleIdList
     * @return
     */
    void removeSysRoleListByIds(List<Long> roleIdList);
}
