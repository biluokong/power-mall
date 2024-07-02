package com.biluo.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.biluo.constant.BusinessEnum;
import com.biluo.domain.Prod;
import com.biluo.domain.ProdTagReference;
import com.biluo.domain.Sku;
import com.biluo.ex.handler.BusinessException;
import com.biluo.mapper.ProdMapper;
import com.biluo.model.ChangeStock;
import com.biluo.model.ProdChange;
import com.biluo.model.SkuChange;
import com.biluo.service.ProdService;
import com.biluo.service.ProdTagReferenceService;
import com.biluo.service.SkuService;
import com.biluo.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProdServiceImpl extends ServiceImpl<ProdMapper, Prod> implements ProdService {
	private final ProdTagReferenceService prodTagReferenceService;
	private final SkuService skuService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveProd(Prod prod) {
		// 新增商品
		prod.setShopId(1L);
		prod.setSoldNum(0);
		prod.setCreateTime(new Date());
		prod.setUpdateTime(new Date());
		prod.setPutawayTime(new Date());
		prod.setVersion(0);
		Prod.DeliveryModeVo deliveryModeVo = prod.getDeliveryModeVo();
		prod.setDeliveryMode(JsonUtil.toJson(deliveryModeVo));
		boolean success = save(prod);
		if (!success) {
			throw new BusinessException(BusinessEnum.ADD_PRODUCT_FAIL);
		}

		Long prodId = prod.getProdId();
		// 处理商品与分组标签的关系
		// 获取商品分组标签
		List<Long> tagIdList = prod.getTagList();
		// 判断是否有值
		if (ObjectUtil.isNotEmpty(tagIdList)) {
			// 创建商品与分组标签关系集合
			List<ProdTagReference> prodTagReferenceList = new ArrayList<>();
			// 循环遍历分组标签id集合
			tagIdList.forEach(tagId -> {
				// 创建商品与分组标签的关系记录
				ProdTagReference prodTagReference = new ProdTagReference();
				prodTagReference.setProdId(prodId);
				prodTagReference.setTagId(tagId);
				prodTagReference.setCreateTime(new Date());
				prodTagReference.setShopId(1L);
				prodTagReference.setStatus(1);
				prodTagReferenceList.add(prodTagReference);
			});
			// 批量添加商品与分组标签的关系记录
			prodTagReferenceService.saveBatch(prodTagReferenceList);
		}

		// 处理商品与商品sku的关系
		// 获取商品sku对象集合
		List<Sku> skuList = prod.getSkuList();
		// 判断是否有值
		if (ObjectUtil.isNotEmpty(skuList)) {
			// 循环遍历商品sku对象集合
			skuList.forEach(sku -> {
				sku.setProdId(prodId);
				sku.setCreateTime(new Date());
				sku.setUpdateTime(new Date());
				sku.setVersion(0);
				sku.setActualStocks(sku.getStocks());
			});
			// 批量添加商品sku对象集合
			skuService.saveBatch(skuList);
		}
	}

	@Override
	public Prod queryProdInfoById(Long prodId) {
		// 根据标识查询商品详情
		Prod prod = getById(prodId);
		if (ObjectUtil.isNull(prod)) {
			return prod;
		}
		// 根据商品标识查询商品与分组标签的关系
		List<ProdTagReference> prodTagReferenceList = prodTagReferenceService.lambdaQuery()
				.eq(ProdTagReference::getProdId, prodId).list();
		// 判断是否有值
		if (ObjectUtil.isNotEmpty(prodTagReferenceList)) {
			// 从商品与分组标签的关系集合中获取分组标签id集合
			List<Long> tagIdList = prodTagReferenceList.stream().map(ProdTagReference::getTagId).collect(Collectors.toList());
			prod.setTagList(tagIdList);
		}
		// 根据商品id查询商品sku对象集合
		List<Sku> skus = skuService.lambdaQuery().eq(Sku::getProdId, prodId).list();
		prod.setSkuList(skus);
		return prod;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void modifyProdInfo(Prod prod) {
		// 获取商品标识
		Long prodId = prod.getProdId();
		// 删除商品原有的与分组标签的关系
		prodTagReferenceService.lambdaUpdate().eq(ProdTagReference::getProdId, prodId).remove();
		// 获取商品分组标签
		List<Long> tagIdList = prod.getTagList();
		// 判断是否有值
		if (CollectionUtil.isNotEmpty(tagIdList) && tagIdList.size() != 0) {
			// 创建商品与分组标签关系集合
			List<ProdTagReference> prodTagReferenceList = new ArrayList<>();
			// 循环遍历分组标签id集合
			tagIdList.forEach(tagId -> {
				// 创建商品与分组标签的关系记录
				ProdTagReference prodTagReference = new ProdTagReference();
				prodTagReference.setProdId(prodId);
				prodTagReference.setTagId(tagId);
				prodTagReference.setCreateTime(new Date());
				prodTagReference.setShopId(1L);
				prodTagReference.setStatus(1);
				prodTagReferenceList.add(prodTagReference);
			});
			// 批量添加商品与分组标签的关系记录
			prodTagReferenceService.saveBatch(prodTagReferenceList);
		}

		// 先删除原有的sku
		skuService.lambdaUpdate().eq(Sku::getProdId, prodId).remove();

		// 再保存现有的商品sku对象集合
		List<Sku> skuList = prod.getSkuList();
		// 判断是否有值
		if (ObjectUtil.isNotEmpty(skuList)) {
			// 循环遍历商品sku对象集合
			skuList.forEach(sku -> {
				sku.setProdId(prodId);
				sku.setCreateTime(new Date());
				sku.setUpdateTime(new Date());
				sku.setVersion(0);
				sku.setActualStocks(sku.getStocks());
			});
			// 批量添加商品sku对象集合
			skuService.saveBatch(skuList);
		}

		// 修改商品对象
		prod.setUpdateTime(new Date());
		boolean success = updateById(prod);
		if (!success) {
			throw new BusinessException(BusinessEnum.MODIFY_PRODUCT_FAIL);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void removeProdById(Long prodId) {
		// 删除商品与分组标签的关系
		prodTagReferenceService.lambdaUpdate().eq(ProdTagReference::getProdId, prodId).remove();
		// 根据商品id删除商品sku对象
		skuService.lambdaUpdate().eq(Sku::getProdId, prodId).remove();
		boolean success = removeById(prodId);
		if (!success) {
			throw new BusinessException(BusinessEnum.REMOVE_PRODUCT_FAIL);
		}
	}

	@Override
	public Prod queryWxProdInfoByProdId(Long prodId) {
		// 根据标识查询商品信息
		Prod prod = getById(prodId);
		// 根据商品标识查询商品sku对象
		List<Sku> skus = skuService.lambdaQuery().eq(Sku::getProdId, prodId).list();
		prod.setSkuList(skus);
		return prod;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void changeProdAndSkuChangeStock(ChangeStock changeStock) {
		// 获取商品sku购买数量对象
		List<SkuChange> skuChangeList = changeStock.getSkuChangeList();
		for (SkuChange skuChange : skuChangeList) {
			Long skuId = skuChange.getSkuId();
			Sku sku = skuService.getById(skuId);
			Integer count = skuService.updateSkuStock(skuId, skuChange.getCount(), sku.getVersion());
			if (count < 1) {
				throw new BusinessException(BusinessEnum.MODIFY_PROP_STOCK_FAIL);
			}
		}

		// 获取商品prod购买数量对象
		List<ProdChange> prodChangeList = changeStock.getProdChangeList();
		for (ProdChange prodChange : prodChangeList) {
			Long prodId = prodChange.getProdId();
			Prod prod = getById(prodId);
			Integer count = baseMapper.updateProdStock(prodId, prodChange.getCount(), prod.getVersion());
			if (count < 1) {
				throw new BusinessException(BusinessEnum.MODIFY_SKU_STOCK_FAIL);
			}
		}
	}
}
