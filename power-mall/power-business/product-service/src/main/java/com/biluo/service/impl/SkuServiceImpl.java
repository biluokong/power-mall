package com.biluo.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.biluo.mapper.SkuMapper;
import com.biluo.domain.Sku;
import com.biluo.service.SkuService;
@Service
public class SkuServiceImpl extends ServiceImpl<SkuMapper, Sku> implements SkuService{

	@Override
	public Integer updateSkuStock(Long skuId, Integer count, Integer version) {
		return baseMapper.updateSkuStock(skuId,count,version);
	}
}
