package com.biluo.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.biluo.constant.BusinessEnum;
import com.biluo.domain.SysUser;
import com.biluo.domain.SysUserRole;
import com.biluo.ex.handler.BusinessException;
import com.biluo.mapper.SysUserMapper;
import com.biluo.service.SysUserRoleService;
import com.biluo.service.SysUserService;
import com.biluo.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService{
    private final SysUserRoleService sysUserRoleService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSysUser(SysUser sysUser) {
        // 新增管理员
        sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        sysUser.setCreateUserId(AuthUtils.getLoginUserId());
        sysUser.setCreateTime(new Date());
        sysUser.setShopId(1L);
        int count = baseMapper.insert(sysUser);
        if (count > 0) {
            // 保存管理员和角色的关系
            saveUserRoleRelationInfo(sysUser);
        } else {
            throw new BusinessException(BusinessEnum.ADD_SYS_USER_FAIL);
        }
    }

    @Override
    public SysUser querySysUserInfoByUserId(Long id) {
        // 根据id查询用户信息
        SysUser sysUser = baseMapper.selectById(id);
        // 根据用户id查询用户与角色的关联信息
        List<SysUserRole> sysUserRoles = sysUserRoleService.lambdaQuery()
                .eq(SysUserRole::getUserId, id)
                .list();
        if (ObjectUtil.isNotEmpty(sysUserRoles)) {
            sysUser.setRoleIdList(sysUserRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList()));
        }
        return sysUser;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifySysUserInfo(SysUser sysUser) {
        //1.删除原因管理员与角色的关联信息
        boolean success = sysUserRoleService.lambdaUpdate()
                .in(SysUserRole::getUserId, sysUser.getUserId())
                .remove();
        if (!success) {
            throw new BusinessException(BusinessEnum.MODIFY_SYS_USER_FAIL);
        }
        //2.保存管理员与角色的关联信息
        // 保存管理员和角色的关系
        saveUserRoleRelationInfo(sysUser);
        //3.保存管理员信息
        //判断是否要修改密码
        String password = sysUser.getPassword();
        if (StringUtils.hasText(password)) {
            sysUser.setPassword(passwordEncoder.encode(password));
        }
        int count = baseMapper.updateById(sysUser);
        if (count == 0) {
            throw new BusinessException(BusinessEnum.MODIFY_SYS_USER_FAIL);
        }
    }

    /**
     * 保存管理员和角色的关系
     * @param sysUser 管理员用户
     */
    private void saveUserRoleRelationInfo(SysUser sysUser) {
        List<Long> roleIdList = sysUser.getRoleIdList();
        if (ObjectUtil.isNotEmpty(roleIdList)) {
            Long userId = sysUser.getUserId();
            ArrayList<SysUserRole> sysUserRoleList = new ArrayList<>();
            roleIdList.forEach(roleId -> {
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setUserId(userId);
                sysUserRole.setRoleId(roleId);
                sysUserRoleList.add(sysUserRole);
            });
            sysUserRoleService.saveBatch(sysUserRoleList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeSysUserListByUserIds(List<Long> userIds) {
        //1.删除原因管理员与角色的关联信息
        boolean success = sysUserRoleService.lambdaUpdate()
                .in(SysUserRole::getUserId, userIds)
                .remove();
        if (!success) {
            throw new BusinessException(BusinessEnum.REMOVE_SYS_USER_FAIL);
        }
        //2.删除原因管理员信息
        int count = baseMapper.deleteBatchIds(userIds);
        if (count == 0) {
            throw new BusinessException(BusinessEnum.REMOVE_SYS_USER_FAIL);
        }
    }
}
