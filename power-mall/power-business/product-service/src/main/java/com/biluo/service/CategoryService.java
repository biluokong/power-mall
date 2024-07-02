package com.biluo.service;

import com.biluo.domain.Category;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CategoryService extends IService<Category>{


    /**
     * 查询系统所有商品类目
     * @return
     */
    List<Category> queryAllCategoryList();

    /**
     * 查询系统商品一级类目
     * @return
     */
    List<Category> queryFirstCategoryList();

    /**
     * 新增商品类目
     * @param category
     * @return
     */
    void saveCategory(Category category);

    /**
     * 修改商品类目信息
     * @param category
     * @return
     */
    void modifyCategory(Category category);

    /**
     * 删除商品类目
     * @param categoryId
     * @return
     */
    void removeCategoryById(Long categoryId);

    /**
     * 查询小程序中商品的一级类目集合
     * @param pid
     * @return
     */
    List<Category> queryWxCategoryListByPid(Long pid);
}
