package com.biluo.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.biluo.constant.BusinessEnum;
import com.biluo.constant.ManagerConstants;
import com.biluo.domain.SysMenu;
import com.biluo.ex.handler.BusinessException;
import com.biluo.mapper.SysMenuMapper;
import com.biluo.service.SysMenuService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = ManagerConstants.SYS_MENU_AND_AUTH_KEY_PREFIX)
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService{

    @Override
    @Cacheable(key = "#loginUserId")
    public Set<SysMenu> queryMenusByUserId(Long loginUserId) {
        Set<SysMenu> menus = baseMapper.selectUserMenuListByUserId(loginUserId);
        // 将菜单集合转换为菜单树
        return transformTree(menus, 0L);
    }

    /**
     * 将集合转换为树结构
     * @param menus 集合
     * @param pid 根节点
     * @return 树结构的集合
     */
    private Set<SysMenu> transformTree(Set<SysMenu> menus, long pid) {
        // 获取根节点集合
        Set<SysMenu> roots = menus.stream()
                .filter(menu -> menu.getParentId().equals(pid))
                .collect(Collectors.toSet());
        // 只有两层时
        // 遍历根节点集合
        /*roots.forEach(root -> {
            // 获取子节点集合
            Set<SysMenu> children = menus.stream()
                    .filter(menu -> menu.getParentId().equals(root.getMenuId()))
                    .collect(Collectors.toSet());
            root.setList(children);
        });*/

        // 大于两层时
        roots.forEach(root -> root.setList(transformTree(menus, root.getMenuId())));
        return roots;
    }

    @Override
    @Cacheable(key = ManagerConstants.SYS_ALL_MENU_KEY)
    public List<SysMenu> queryAllSysMenuList() {
        return baseMapper.selectList(null);
    }

    @Override
    @CacheEvict(key = ManagerConstants.SYS_ALL_MENU_KEY)
    @Transactional(rollbackFor = Exception.class)
    public void saveSysMenu(SysMenu sysMenu) {
        boolean success = save(sysMenu);
        if (!success) {
            throw new BusinessException(BusinessEnum.ADD_SYS_MENU_FAIL);
        }
    }

    @Override
    @CacheEvict(key = ManagerConstants.SYS_ALL_MENU_KEY)
    public void modifySysMenu(SysMenu sysMenu) {
        // 获取菜单类型
        Integer type = sysMenu.getType();
        if (0 == type) {
            sysMenu.setParentId(0L);
        }
        boolean success = updateById(sysMenu);
        if (!success) {
            throw new BusinessException(BusinessEnum.MODIFY_SYS_MENU_FAIL);
        }
    }


    @Override
    @CacheEvict(key = ManagerConstants.SYS_ALL_MENU_KEY)
    public void removeSysMenuById(Long menuId) {
        // 根据菜单标识查询子菜单集合
        List<SysMenu> sysMenuList = lambdaQuery().eq(SysMenu::getParentId, menuId).list();
        // 判断子菜单集合是否有值
        if (ObjectUtil.isNotEmpty(sysMenuList)) {
            // 说明：当前菜单节点包含子节点集合
            throw new BusinessException(BusinessEnum.REMOVE_SYS_MENU_BY_CHILD_FAIL);
        }
        // 说明：当前菜单节点不包含子节点集合，可以删除
        boolean success = removeById(menuId);
        if (!success) {
            throw new BusinessException(BusinessEnum.REMOVE_SYS_MENU_FAIL);
        }
    }
}
