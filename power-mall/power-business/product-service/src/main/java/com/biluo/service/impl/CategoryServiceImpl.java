package com.biluo.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.biluo.constant.BusinessEnum;
import com.biluo.constant.ProductConstants;
import com.biluo.domain.Category;
import com.biluo.ex.handler.BusinessException;
import com.biluo.mapper.CategoryMapper;
import com.biluo.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
@Service
@CacheConfig(cacheNames = ProductConstants.PRODUCT_KEY_PREFIX)
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService{

    @Override
    @Cacheable(key = ProductConstants.ALL_CATEGORY_KEY)
    public List<Category> queryAllCategoryList() {
        return lambdaQuery().orderByDesc(Category::getSeq).list();
    }

    @Override
    @Cacheable(key = ProductConstants.FIRST_CATEGORY_KEY)
    public List<Category> queryFirstCategoryList() {
        return lambdaQuery()
                .eq(Category::getParentId,0)
                .eq(Category::getStatus,1)
                .orderByDesc(Category::getSeq)
                .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(key = ProductConstants.ALL_CATEGORY_KEY),
            @CacheEvict(key = ProductConstants.FIRST_CATEGORY_KEY),
            @CacheEvict(key = ProductConstants.WX_FIRST_CATEGORY)
    })
    public void saveCategory(Category category) {
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        boolean success = save(category);
        if (!success) {
            throw new BusinessException(BusinessEnum.ADD_PRODUCT_CATEGORY_FAIL);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(key = ProductConstants.ALL_CATEGORY_KEY),
            @CacheEvict(key = ProductConstants.FIRST_CATEGORY_KEY),
            @CacheEvict(key = ProductConstants.WX_FIRST_CATEGORY)
    })
    public void modifyCategory(Category category) {
        // 修改后的pid
        Long parentId = category.getParentId();
        // 根据标识查询类目详情
        Category beforeCategory = getById(category.getCategoryId());
        if (null == beforeCategory) {
            throw new BusinessException(BusinessEnum.MODIFY_PRODUCT_CATEGORY_FAIL);
        }
        // 获取商品类目之前的级别,如果parentId为0即为1级类目，不为0即为2级类目
        Long beforeParentId = beforeCategory.getParentId();
        // 判断商品类目修改的详情
        // 1 -> 2 : 之前pid为0 且 修改后的pid不为0
        if (0 == beforeParentId && null != parentId && 0 != parentId) {
            // 查询当前类目是否包含子类目，如果包含子类目，则不允许修改
            // 根据当前类目标识查询子类目
            List<Category> childList = lambdaQuery()
                    .eq(Category::getParentId, category.getCategoryId()).list();
            // 判断是否有值
            if (ObjectUtil.isNotEmpty(childList)) {
                // 说明：当前类目包含子类目，不允许修改
                throw new BusinessException(BusinessEnum.MODIFY_PRODUCT_CATEGORY_BY_CHILD_FAIL);
            }
        }

        // 2 -> 1：之前pid不为0 且 当前pid为null
        if (0 != beforeParentId && null == parentId) {
            category.setParentId(0L);
        }
        boolean success = updateById(category);
        if (!success) {
            throw new BusinessException(BusinessEnum.MODIFY_PRODUCT_CATEGORY_FAIL);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(key = ProductConstants.ALL_CATEGORY_KEY),
            @CacheEvict(key = ProductConstants.FIRST_CATEGORY_KEY),
            @CacheEvict(key = ProductConstants.WX_FIRST_CATEGORY)
    })
    public void removeCategoryById(Long categoryId) {
        // 根据类目标识查询子类目集合
        List<Category> childCategoryList = lambdaQuery().eq(Category::getParentId, categoryId).list();
        if (ObjectUtil.isNotEmpty(childCategoryList)) {
            // 说明：当前类目包含子类目，不可删除
            throw new BusinessException(BusinessEnum.REMOVE_PRODUCT_CATEGORY_BY_CHILD_FAIL);
        }
        // 说明：当前类目不包含子类目
        boolean success = removeById(categoryId);
        if (!success) {
            throw new BusinessException(BusinessEnum.REMOVE_PRODUCT_CATEGORY_FAIL);
        }
    }

    @Override
    @Cacheable(key = ProductConstants.WX_FIRST_CATEGORY)
    public List<Category> queryWxCategoryListByPid(Long pid) {
        return lambdaQuery()
                .eq(Category::getStatus,1)
                .eq(Category::getParentId,pid)
                .orderByDesc(Category::getSeq)
                .list();
    }
}
