package com.biluo.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.biluo.constant.BusinessEnum;
import com.biluo.domain.Category;
import com.biluo.domain.Prod;
import com.biluo.domain.ProdTagReference;
import com.biluo.ex.handler.BusinessException;
import com.biluo.feign.SearchProdFeign;
import com.biluo.model.Result;
import com.biluo.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
	private final SearchProdFeign searchProdFeign;

	@Override
	public Page<Prod> queryWxProdPageByTagId(Long current, Long size, Long tagId) {
		// 创建商品分页对象
		Page<Prod> prodPage = new Page<>(current, size);
		// 远程接口调用：根据分组标签分页查询商品与分组标签的关系
		Result<Page<ProdTagReference>> result = searchProdFeign.getProdTagReferencePageByTagId(current, size, tagId);
		// 判断是否操作成功
		if (result.getCode() == BusinessEnum.OPERATION_FALL.getCode()) {
			log.error("远程接口调用：根据分组标签分页查询商品与分组标签的关系失败");
			throw new BusinessException(BusinessEnum.QUERY_FEIGN_PROD_TAG_LIST_FAIL);
		}
		// 获取商品与分组标签的分页对象
		Page<ProdTagReference> prodTagReferencePage = result.getData();
		// 从商品与分组标签分页对象中获取商品与分组标签关系记录
		List<ProdTagReference> prodTagReferenceList = prodTagReferencePage.getRecords();
		// 判断商品与分组标签关系集合是否有值
		if (ObjectUtil.isEmpty(prodTagReferenceList)) {
			return prodPage;
		}
		// 从商品与分组标签关系集合中获取商品id集合
		List<Long> prodIdList = prodTagReferenceList.stream()
				.map(ProdTagReference::getProdId).collect(Collectors.toList());
		// 远程调用：根据商品id集合查询商品对象集合
		Result<List<Prod>> prodResult = searchProdFeign.getProdListByIds(prodIdList);
		// 判断是否操作成功
		if (prodResult.getCode() == BusinessEnum.OPERATION_FALL.getCode()) {
			log.error("远程调用：根据商品id集合查询商品对象集合失败");
			throw new BusinessException(BusinessEnum.QUERY_FEIGN_PROD_LIST_FAIL);
		}
		// 获取商品对象集合
		List<Prod> prods = prodResult.getData();

		// 组装商品分页对象
		prodPage.setRecords(prods);
		prodPage.setTotal(prodTagReferencePage.getTotal());
		prodPage.setPages(prodTagReferencePage.getPages());

		return prodPage;
	}

	/**
	 * 根据商品类目标识查询商品集合<br>
	 * 1.当前类目标识只有商品一级类目<br>
	 * 2.查询的商品应该包含商品一级类目下的子类目的商品
	 *
	 * @param categoryId
	 * @return
	 */
	@Override
	public List<Prod> queryWxProdListByCategoryId(Long categoryId) {
		// 创建所有类目id集合
		List<Long> allCategoryIds = new ArrayList<>();
		allCategoryIds.add(categoryId);
		// 远程调用：根据商品一级类目id查询子类目集合
		Result<List<Category>> categoryResult = searchProdFeign.getCategoryListByParentId(categoryId);
		// 判断操作成功
		if (categoryResult.getCode() == BusinessEnum.OPERATION_FALL.getCode()) {
			log.error("远程调用失败：根据商品一级类目id查询子类目集合");
			throw new BusinessException(BusinessEnum.QUERY_FEIGN_CATEGORY_LIST_FAIL);
		}
		// 获取数据
		List<Category> categoryList = categoryResult.getData();
		// 判断子类目集合是否有值
		if (ObjectUtil.isNotEmpty(categoryList)) {
			// 从子类目集合中获取类目id集合
			List<Long> categoryIdList = categoryList.stream().map(Category::getCategoryId).collect(Collectors.toList());
			allCategoryIds.addAll(categoryIdList);
		}
		// 远程调用：根据商品类目id集合查询商品对象集合
		Result<List<Prod>> prodResult = searchProdFeign.getProdListByCategoryIds(allCategoryIds);
		// 判断查询结果
		if (prodResult.getCode()==BusinessEnum.OPERATION_FALL.getCode()) {
			log.error("远程调用失败：根据商品类目id集合查询商品对象集合");
			throw new BusinessException(BusinessEnum.QUERY_FEIGN_PROD_LIST_FAIL);
		}
		// 获取数据

		return prodResult.getData();
	}
}
