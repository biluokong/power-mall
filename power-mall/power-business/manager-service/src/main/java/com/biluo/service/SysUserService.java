package com.biluo.service;

import com.biluo.domain.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysUserService extends IService<SysUser>{


    /**
     * 新增管理员
     * @param sysUser
     */
    void saveSysUser(SysUser sysUser);

    /**
     * 根据标识查询系统管理员信息
     * @param id
     * @return
     */
    SysUser querySysUserInfoByUserId(Long id);

    /**
     * 修改管理员信息
     * @param sysUser
     */
    void modifySysUserInfo(SysUser sysUser);

    /**
     * 批量/单个删除管理员
     * @param userIds
     */
    void removeSysUserListByUserIds(List<Long> userIds);
}
