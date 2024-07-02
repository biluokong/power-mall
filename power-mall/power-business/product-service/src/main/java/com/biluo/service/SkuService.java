package com.biluo.service;

import com.biluo.domain.Sku;
import com.baomidou.mybatisplus.extension.service.IService;
public interface SkuService extends IService<Sku>{
	Integer updateSkuStock(Long skuId, Integer count, Integer version);
}
