package com.biluo.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.biluo.domain.Prod;
import com.biluo.domain.Sku;
import com.biluo.model.ChangeStock;
import com.biluo.model.Result;
import com.biluo.service.ProdService;
import com.biluo.service.SkuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品管理控制层
 */
@Api(tags = "商品接口管理")
@RequestMapping("prod/prod")
@RestController
@RequiredArgsConstructor
public class ProdController {
	private final ProdService prodService;
	private final SkuService skuService;

	/**
	 * 多条件分页查询商品
	 *
	 * @param current  页码
	 * @param size     每页显示条数
	 * @param prodName 商品名称
	 * @param status   商品状态
	 */
	@ApiOperation("多条件分页查询商品")
	@GetMapping("page")
	@PreAuthorize("hasAuthority('prod:prod:page')")
	public Result<Page<Prod>> loadProdPage(Long current, Long size,
										   @RequestParam(required = false) String prodName,
										   @RequestParam(required = false) Long status) {
		// 创建商品分页对象
		Page<Prod> page = new Page<>(current, size);
		// 多条件分页查询商品
		page = prodService.page(page, new LambdaQueryWrapper<Prod>()
				.eq(ObjectUtil.isNotNull(status), Prod::getStatus, status)
				.like(StringUtils.hasText(prodName), Prod::getProdName, prodName)
				.orderByDesc(Prod::getCreateTime)
		);
		return Result.success(page);
	}

	/**
	 * 新增商品
	 *
	 * @param prod 商品对象
	 */
	@ApiOperation("新增商品")
	@PostMapping
	@PreAuthorize("hasAuthority('prod:prod:save')")
	public Result saveProd(@RequestBody Prod prod) {
		prodService.saveProd(prod);
		return Result.success();
	}

	/**
	 * 根据标识查询商品详情
	 *
	 * @param prodId 商品id
	 */
	@ApiOperation("根据标识查询商品详情")
	@GetMapping("info/{prodId}")
	@PreAuthorize("hasAuthority('prod:prod:info')")
	public Result<Prod> loadProdInfo(@PathVariable Long prodId) {
		Prod prod = prodService.queryProdInfoById(prodId);
		return Result.success(prod);
	}

	/**
	 * 修改商品信息
	 *
	 * @param prod 商品对象
	 * @return
	 */
	@ApiOperation("修改商品信息")
	@PutMapping
	@PreAuthorize("hasAuthority('prod:prod:update')")
	public Result modifyProdInfo(@RequestBody Prod prod) {
		prodService.modifyProdInfo(prod);
		return Result.success();
	}

	/**
	 * 删除商品
	 *
	 * @param prodId 商品id
	 * @return
	 */
	@ApiOperation("删除商品")
	@DeleteMapping("{prodId}")
	@PreAuthorize("hasAuthority('prod:prod:delete')")
	public Result removeProd(@PathVariable Long prodId) {
		prodService.removeProdById(prodId);
		return Result.success();
	}

	///////////////////////////////////////// feign接口 /////////////////////////////////////
	@GetMapping("getProdListByIds")
	public Result<List<Prod>> getProdListByIds(@RequestParam List<Long> prodIdList) {
		List<Prod> prods = prodService.listByIds(prodIdList);
		return Result.success(prods);
	}

	@GetMapping("getProdListByCategoryIds")
	public Result<List<Prod>> getProdListByCategoryIds(@RequestParam List<Long> categoryIds) {
		List<Prod> list = prodService.lambdaQuery().in(Prod::getCategoryId, categoryIds).list();
		return Result.success(list);
	}

	@GetMapping("getSkuListBySkuIds")
	public Result<List<Sku>> getSkuListBySkuIds(@RequestParam List<Long> skuIds) {
		List<Sku> skus = skuService.listByIds(skuIds);
		return Result.success(skus);
	}

	@PostMapping("changeProdAndSkuStock")
	public Result changeProdAndSkuStock(@RequestBody ChangeStock changeStock) {
		prodService.changeProdAndSkuChangeStock(changeStock);
		return Result.success();
	}


	////////////////////////////// 微信小程序数据接口 /////////////////////////

	/**
	 * 小程序根据商品标识查询商品详情
	 *
	 * @param prodId 商品标识
	 * @return
	 */
	@ApiOperation("小程序根据商品标识查询商品详情")
	@GetMapping("prod/prodInfo")
	public Result<Prod> loadWxProdInfo(@RequestParam Long prodId) {
		// 根据商品标识查询商品详情
		Prod prod = prodService.queryWxProdInfoByProdId(prodId);
		return Result.success(prod);
	}
}
