package com.biluo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.biluo.domain.Prod;

import java.util.List;

/**
 *
 */
public interface SearchService {

    /**
     * 根据分组标签分页查询商品
     * @param current
     * @param size
     * @param tagId
     * @return
     */
    Page<Prod> queryWxProdPageByTagId(Long current, Long size, Long tagId);

    /**
     * 根据商品类目标识查询商品集合
     * @param categoryId
     * @return
     */
    List<Prod> queryWxProdListByCategoryId(Long categoryId);
}
