package com.biluo.feign.sentinel;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.biluo.domain.Category;
import com.biluo.domain.Prod;
import com.biluo.domain.ProdTagReference;
import com.biluo.feign.SearchProdFeign;
import com.biluo.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 */
@Component
@Slf4j
public class SearchProdFeignSentinel implements SearchProdFeign {
    @Override
    public Result<Page<ProdTagReference>> getProdTagReferencePageByTagId(Long current, Long size, Long tagId) {
        log.error("远程调用失败：根据分组标签id分页查询商品与分组标签关系");
        return null;
    }

    @Override
    public Result<List<Prod>> getProdListByIds(List<Long> prodIdList) {
        log.error("远程调用：根据商品id集合查询商品对象集合失败");
        return null;
    }

    @Override
    public Result<List<Category>> getCategoryListByParentId(Long parentId) {
        log.error("远程调用：根据商品一级类目id查询子类目集合失败");
        return null;
    }

    @Override
    public Result<List<Prod>> getProdListByCategoryIds(List<Long> categoryIds) {
        log.error("远程调用失败：根据商品类目id集合查询商品对象集合");
        return null;
    }
}
