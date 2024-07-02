package com.biluo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.biluo.constant.BusinessEnum;
import com.biluo.constant.ProductConstants;
import com.biluo.domain.ProdTag;
import com.biluo.ex.handler.BusinessException;
import com.biluo.mapper.ProdTagMapper;
import com.biluo.service.ProdTagService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Service
@CacheConfig(cacheNames = ProductConstants.PRODUCT_KEY_PREFIX)
public class ProdTagServiceImpl extends ServiceImpl<ProdTagMapper, ProdTag> implements ProdTagService {


	@Override
	@Transactional(rollbackFor = Exception.class)
	@Caching(evict = {
			@CacheEvict(key = ProductConstants.ALL_NORMAL_TAG_KEY),
			@CacheEvict(key = ProductConstants.WX_PROD_TAG)
	})
	public void saveProdTag(ProdTag prodTag) {
		prodTag.setCreateTime(new Date());
		prodTag.setUpdateTime(new Date());
		boolean success = save(prodTag);
		if (!success) {
			throw new BusinessException(BusinessEnum.ADD_PRODUCT_TAG_FAIL);
		}
	}

	@Override
	@Caching(evict = {
			@CacheEvict(key = ProductConstants.ALL_NORMAL_TAG_KEY),
			@CacheEvict(key = ProductConstants.WX_PROD_TAG)
	})
	public void modifyProdTag(ProdTag prodTag) {
		prodTag.setUpdateTime(new Date());
		boolean success = updateById(prodTag);
		if (!success) {
			throw new BusinessException(BusinessEnum.MODIFY_PRODUCT_TAG_FAIL);
		}
	}

	@Override
	@Caching(evict = {
			@CacheEvict(key = ProductConstants.ALL_NORMAL_TAG_KEY),
			@CacheEvict(key = ProductConstants.WX_PROD_TAG)
	})
	public boolean removeById(Serializable id) {
		return super.removeById(id);
	}

	@Override
	@Cacheable(key = ProductConstants.ALL_NORMAL_TAG_KEY)
	public List<ProdTag> queryProdTagList() {
		return lambdaQuery()
				.eq(ProdTag::getStatus, 1)
				.orderByDesc(ProdTag::getSeq)
				.list();
	}

	@Override
	@Cacheable(key = ProductConstants.WX_PROD_TAG)
	public List<ProdTag> queryWxProdTagList() {
		return lambdaQuery()
				.eq(ProdTag::getStatus, 1)
				.orderByDesc(ProdTag::getSeq)
				.list();
	}
}
