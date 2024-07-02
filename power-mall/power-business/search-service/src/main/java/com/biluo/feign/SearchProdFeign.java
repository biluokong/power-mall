package com.biluo.feign;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.biluo.domain.Category;
import com.biluo.domain.Prod;
import com.biluo.domain.ProdTagReference;
import com.biluo.feign.sentinel.SearchProdFeignSentinel;
import com.biluo.model.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 搜索业务模块调用产品业务模块: feign接口
 */
@FeignClient(value = "product-service",fallback = SearchProdFeignSentinel.class)
public interface SearchProdFeign {

    @GetMapping("prod/prodTag/getProdTagReferencePageByTagId")
    Result<Page<ProdTagReference>> getProdTagReferencePageByTagId(@RequestParam Long current,
                                                                  @RequestParam Long size,
                                                                  @RequestParam Long tagId);

    @GetMapping("prod/prod/getProdListByIds")
    Result<List<Prod>> getProdListByIds(@RequestParam List<Long> prodIdList);

    @GetMapping("prod/category/getCategoryListByParentId")
    Result<List<Category>> getCategoryListByParentId(@RequestParam Long parentId);

    @GetMapping("prod/prod/getProdListByCategoryIds")
    Result<List<Prod>> getProdListByCategoryIds(@RequestParam List<Long> categoryIds);
}
