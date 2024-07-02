package com.biluo.feign.sentinel;

import com.biluo.domain.Sku;
import com.biluo.feign.BasketProdFeign;
import com.biluo.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 */
@Component
@Slf4j
public class BasketProdFeignSentinel implements BasketProdFeign {
    @Override
    public Result<List<Sku>> getSkuListBySkuIds(List<Long> skuIds) {
        log.error("远程调用：根据商品skuId集合查询商品sku对象集合失败");
        return null;
    }
}
