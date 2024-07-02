package com.biluo.feign.sentinel;

import com.biluo.domain.Sku;
import com.biluo.feign.OrderProdFeign;
import com.biluo.model.ChangeStock;
import com.biluo.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 */
@Component
@Slf4j
public class OrderProdFeignSentinel implements OrderProdFeign {
    @Override
    public Result<List<Sku>> getSkuListBySkuIds(List<Long> skuIds) {
        log.error("远程接口调用：根据商品skuId集合查询商品sku对象集合 失败");
        return null;
    }

    @Override
    public Result<Boolean> changeProdAndSkuStock(ChangeStock changeStock) {
        log.error("远程接口调用失败：修改商品prod和sku库存数量");
        return null;
    }
}
