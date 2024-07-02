package com.biluo.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.biluo.constant.BusinessEnum;
import com.biluo.constant.ManagerConstants;
import com.biluo.domain.SysRole;
import com.biluo.domain.SysRoleMenu;
import com.biluo.ex.handler.BusinessException;
import com.biluo.mapper.SysRoleMapper;
import com.biluo.service.SysRoleMenuService;
import com.biluo.service.SysRoleService;
import com.biluo.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = ManagerConstants.SYS_KEY_PREFIX)
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService{
    private final SysRoleMenuService sysRoleMenuService;

    @Override
    @Cacheable(key = ManagerConstants.SYS_ALL_ROLE_KEY)
    public List<SysRole> querySysRoleList() {
        return lambdaQuery().orderByDesc(SysRole::getCreateTime).list();
    }

    @Override
    @CacheEvict(key = ManagerConstants.SYS_ALL_ROLE_KEY)
    @Transactional(rollbackFor = Exception.class)
    public void saveSysRole(SysRole sysRole) {
        //1.新增角色
        sysRole.setCreateTime(new Date());
        sysRole.setCreateUserId(AuthUtils.getLoginUserId());
        boolean success = save(sysRole);
        if (!success) {
            throw new BusinessException(BusinessEnum.ADD_SYS_ROLE_FAIL);
        }
        //2.新增角色和权限的关系
        List<Long> menuIdList = sysRole.getMenuIdList();
        if (ObjectUtil.isNotEmpty(menuIdList)) {
            ArrayList<SysRoleMenu> sysRoleMenus = new ArrayList<>();
            Long roleId = sysRole.getRoleId();
            menuIdList.forEach(menuId -> {
                SysRoleMenu sysRoleMenu = new SysRoleMenu();
                sysRoleMenu.setRoleId(roleId);
                sysRoleMenu.setMenuId(menuId);
                sysRoleMenus.add(sysRoleMenu);
            });
            success = sysRoleMenuService.saveBatch(sysRoleMenus);
            if (!success) {
                throw new BusinessException(BusinessEnum.ADD_SYS_ROLE_FAIL);
            }
        }
    }

    @Override
    public SysRole querySysRoleInfoByRoleId(Long roleId) {
        SysRole sysRole = baseMapper.selectById(roleId);
        List<SysRoleMenu> sysRoleMenus = sysRoleMenuService.lambdaQuery()
                .eq(SysRoleMenu::getRoleId, roleId).list();
        sysRole.setMenuIdList(sysRoleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList()));
        return sysRole;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifySysRole(SysRole sysRole) {
        //删除原有权限
        Long roleId = sysRole.getRoleId();
        sysRoleMenuService.lambdaUpdate().eq(SysRoleMenu::getRoleId, roleId).remove();
        //保存权限
        List<Long> menuIdList = sysRole.getMenuIdList();
        if (ObjectUtil.isNotEmpty(menuIdList)) {
            ArrayList<SysRoleMenu> sysRoleMenus = new ArrayList<>();
            menuIdList.forEach(menuId -> {
                SysRoleMenu sysRoleMenu = new SysRoleMenu();
                sysRoleMenu.setRoleId(roleId);
                sysRoleMenu.setMenuId(menuId);
                sysRoleMenus.add(sysRoleMenu);
            });
            boolean success = sysRoleMenuService.saveBatch(sysRoleMenus);
            if (!success) {
                throw new BusinessException(BusinessEnum.MODIFY_SYS_ROLE_FAIL);
            }
        }
        //更新角色
        boolean success = updateById(sysRole);
        if (!success) {
            throw new BusinessException(BusinessEnum.MODIFY_SYS_ROLE_FAIL);
        }
    }

    @Override
    @CacheEvict(key = ManagerConstants.SYS_ALL_ROLE_KEY)
    @Transactional(rollbackFor = Exception.class)
    public void removeSysRoleListByIds(List<Long> roleIdList) {
        // 批量或单个删除角色与权限关系集合
        sysRoleMenuService.lambdaUpdate().in(SysRoleMenu::getRoleId,roleIdList).remove();
        // 批量或单个删除角色
        boolean success = removeBatchByIds(roleIdList);
        if (!success) {
            throw new BusinessException(BusinessEnum.REMOVE_SYS_ROLE_FAIL);
        }
    }
}
