package com.biluo.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.biluo.constant.BusinessEnum;
import com.biluo.constant.ProductConstants;
import com.biluo.domain.ProdProp;
import com.biluo.domain.ProdPropValue;
import com.biluo.ex.handler.BusinessException;
import com.biluo.mapper.ProdPropMapper;
import com.biluo.mapper.ProdPropValueMapper;
import com.biluo.model.Result;
import com.biluo.service.ProdPropService;
import com.biluo.service.ProdPropValueService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = ProductConstants.PRODUCT_KEY_PREFIX)
public class ProdPropServiceImpl extends ServiceImpl<ProdPropMapper, ProdProp> implements ProdPropService {
	private final ProdPropValueMapper prodPropValueMapper;
	private final ProdPropValueService prodPropValueService;

	@Override
	public Page<ProdProp> queryProdSpecPage(Long current, Long size, String propName) {
		// 创建分页对象
		Page<ProdProp> page = new Page<>(current, size);
		// 多条件分页查询商品属性
		page = lambdaQuery()
				.like(StringUtils.hasText(propName), ProdProp::getPropName, propName)
				.page(page);
		// 从分页对象中获取属性记录
		List<ProdProp> prodPropList = page.getRecords();
		// 判断是否有值
		if (ObjectUtil.isEmpty(prodPropList)) {
			// 如果属性对象集合没有值，说明属性值也为空
			return page;
		}
		// 从属性对象集合中获取属性id集合
		List<Long> propIdList = prodPropList.stream().map(ProdProp::getPropId).collect(Collectors.toList());

		// 属性id集合查询属性值对象集合
		List<ProdPropValue> prodPropValueList = prodPropValueMapper.selectList(
				new LambdaQueryWrapper<ProdPropValue>().in(ProdPropValue::getPropId, propIdList));
		// 循环遍历属性对象集合
		prodPropList.forEach(prodProp -> {
			// 从属性值对象集合中过滤出与当前属性对象的属性id一致的属性对象集合
			List<ProdPropValue> propValues = prodPropValueList.stream()
					.filter(prodPropValue -> prodPropValue.getPropId().equals(prodProp.getPropId()))
					.collect(Collectors.toList());
			prodProp.setProdPropValues(propValues);
		});
		return page;
	}

	/**
	 * 新增商品规格
	 * 1.新增商品属性对象 -> 属性id
	 * 2.批量添加商品属性值对象
	 *
	 * @param prodProp
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(key = ProductConstants.ALL_SPEC_NAME_KEY)
	public void saveProdSpec(ProdProp prodProp) {
		// 新增商品属性对象
		prodProp.setShopId(1L);
		prodProp.setRule(2);
		boolean success = save(prodProp);
		if (!success) {
			throw new BusinessException(BusinessEnum.ADD_PRODUCT_SPEC_FAIL);
		}
		// 获取属性id
		Long propId = prodProp.getPropId();
		// 添加商品属性对象与属性值的记录
		// 获取商品属性值集合
		List<ProdPropValue> prodPropValues = prodProp.getProdPropValues();
		// 判断是否有值
		if (ObjectUtil.isNotEmpty(prodPropValues)) {
			// 循环遍历属性值对象集合
			prodPropValues.forEach(prodPropValue -> prodPropValue.setPropId(propId));
			// 批量添加属性值对象集合
			success = prodPropValueService.saveBatch(prodPropValues);
			if (!success) {
				throw new BusinessException(BusinessEnum.ADD_PRODUCT_SPEC_FAIL);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(key = ProductConstants.ALL_SPEC_NAME_KEY)
	public void modifyProdSpec(ProdProp prodProp) {
		Long propId = prodProp.getPropId();
		// 先删除属性
		prodPropValueService.lambdaUpdate()
				.eq(ProdPropValue::getPropId, propId).remove();

		// 保存新的属性值对象集合
		List<ProdPropValue> prodPropValues = prodProp.getProdPropValues();
		// 判断是否有值
		if (ObjectUtil.isNotEmpty(prodPropValues)) {
			// 循环遍历属性值对象集合
			prodPropValues.forEach(prodPropValue -> prodPropValue.setPropId(propId));
			// 批量添加属性值对象集合
			boolean success = prodPropValueService.saveBatch(prodPropValues);
			if (!success) {
				throw new BusinessException(BusinessEnum.MODIFY_PRODUCT_SPEC_FAIL);
			}
		}
		// 修改属性对象
		boolean success = updateById(prodProp);
		if (!success) {
			throw new BusinessException(BusinessEnum.MODIFY_PRODUCT_SPEC_FAIL);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(key = ProductConstants.ALL_SPEC_NAME_KEY)
	public void removeProdSpecByPropId(Long propId) {
		// 根据属性标识删除属性值
		prodPropValueMapper.delete(new LambdaQueryWrapper<ProdPropValue>()
				.eq(ProdPropValue::getPropId, propId)
		);
		// 删除属性对象
		boolean success = removeById(propId);
		if (!success) {
			throw new BusinessException(BusinessEnum.REMOVE_PRODUCT_SPEC_FAIL);
		}
	}

	@Override
	@Cacheable(key = ProductConstants.ALL_SPEC_NAME_KEY)
	public List<ProdProp> queryProdPropList() {
		return list();
	}
}
