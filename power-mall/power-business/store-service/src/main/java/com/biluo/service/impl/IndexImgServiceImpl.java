package com.biluo.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.biluo.constant.BusinessEnum;
import com.biluo.constant.StoreConstants;
import com.biluo.domain.IndexImg;
import com.biluo.domain.Prod;
import com.biluo.ex.handler.BusinessException;
import com.biluo.feign.StoreProdFeign;
import com.biluo.mapper.IndexImgMapper;
import com.biluo.model.Result;
import com.biluo.service.IndexImgService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = StoreConstants.STORE_KEY_PREFIX)
public class IndexImgServiceImpl extends ServiceImpl<IndexImgMapper, IndexImg> implements IndexImgService {
	private final StoreProdFeign storeProdFeign;

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(key = StoreConstants.WX_INDEX_IMG_KEY)
	public Boolean saveIndexImg(IndexImg indexImg) {
		indexImg.setShopId(1L);
		indexImg.setCreateTime(new Date());
		// 获取关联类型
		Integer type = indexImg.getType();
		// 判断关联类型
		if (-1 == type || (0 == type && ObjectUtil.isNull(indexImg.getProdId()))) {    // 轮播图未关联商品
			indexImg.setType(-1);
			indexImg.setProdId(null);
		}
		return save(indexImg);
	}

	@Override
	public IndexImg queryIndexImgInfoById(Long imgId) {
		// 根据标识查询轮播图信息
		IndexImg indexImg = getById(imgId);
		// 获取轮播图关联类型
		Integer type = indexImg.getType();
		// 获取关联商品的id
		Long prodId = indexImg.getProdId();
		// 判断关联商品
		if (0 == type && ObjectUtil.isNotNull(prodId)) {    // 如果当前轮播图已关联商品;
			// 远程调用：根据商品id查询商品图片和名称
			Result<List<Prod>> result = storeProdFeign
					.getProdListByIds(Collections.singletonList(prodId));
			// 判断是否正确
			if (BusinessEnum.OPERATION_FALL.getCode() == result.getCode()) {
				throw new BusinessException(BusinessEnum.QUERY_FEIGN_PRODUCT_FAIL);
			}
			// 获取数据
			List<Prod> prods = result.getData();
			// 判断集合是否有值
			if (ObjectUtil.isNotEmpty(prods)) {
				// 获取商品对象
				Prod prod = prods.get(0);
				indexImg.setPic(prod.getPic());
				indexImg.setProdName(prod.getProdName());
			}
		}

		return indexImg;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(key = StoreConstants.WX_INDEX_IMG_KEY)
	public Boolean modifyIndexImg(IndexImg indexImg) {
		Integer type = indexImg.getType();
		if (-1 == type || (0 == type && ObjectUtil.isNull(indexImg.getProdId()))) {
			indexImg.setType(-1);
			indexImg.setProdId(null);
		}
		return updateById(indexImg);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(key = StoreConstants.WX_INDEX_IMG_KEY)
	public Boolean removeIndexImgByIds(List<Long> imgIds) {
		return removeBatchByIds(imgIds);
	}

	@Override
	@Cacheable(key = StoreConstants.WX_INDEX_IMG_KEY)
	public List<IndexImg> queryWxIndexImgList() {
		return lambdaQuery()
				.eq(IndexImg::getStatus, 1)
				.orderByDesc(IndexImg::getSeq)
				.list();
	}
}
