package com.biluo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.biluo.domain.ProdProp;
import com.biluo.domain.ProdPropValue;
import com.biluo.model.Result;
import com.biluo.service.ProdPropService;
import com.biluo.service.ProdPropValueService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品规格管理控制层
 */
@Api(tags = "商品规格接口管理")
@RequestMapping("prod/spec")
@RestController
@RequiredArgsConstructor
public class ProSpecController {
	private final ProdPropService prodPropService;
	private final ProdPropValueService prodPropValueService;

	/**
	 * 多条件分页查询商品规格
	 * @param current  页码
	 * @param size     每页显示条数
	 * @param propName 属性名称
	 */
	@ApiOperation("多条件分页查询商品规格")
	@GetMapping("page")
	@PreAuthorize("hasAuthority('prod:spec:page')")
	public Result<Page<ProdProp>> loadProdSpecPage(Long current, Long size,
												   @RequestParam(required = false) String propName) {
		// 多条件分页查询商品规格
		Page<ProdProp> page = prodPropService.queryProdSpecPage(current, size, propName);
		return Result.success(page);
	}

	/**
	 * 新增商品规格
	 * @param prodProp 商品属性对象
	 */
	@ApiOperation("新增商品规格")
	@PostMapping
	@PreAuthorize("hasAuthority('prod:spec:save')")
	public Result saveProdSpec(@RequestBody ProdProp prodProp) {
		prodPropService.saveProdSpec(prodProp);
		return Result.success();
	}

	/**
	 * 修改商品规格信息
	 * @param prodProp 商品属性对象
	 */
	@ApiOperation("修改商品规格信息")
	@PutMapping
	@PreAuthorize("hasAuthority('prod:spec:update')")
	public Result modifyProdSpec(@RequestBody ProdProp prodProp) {
		prodPropService.modifyProdSpec(prodProp);
		return Result.success();
	}

	/**
	 * 删除商品规格
	 * @param propId 属性标识
	 */
	@ApiOperation("删除商品规格")
	@DeleteMapping("{propId}")
	@PreAuthorize("hasAuthority('prod:spec:delete')")
	public Result removeProdSpec(@PathVariable Long propId) {
		prodPropService.removeProdSpecByPropId(propId);
		return Result.success();
	}

	/**
	 * 查询系统商品属性集合
	 */
	@ApiOperation("查询系统商品属性集合")
	@GetMapping("list")
	@PreAuthorize("hasAuthority('prod:spec:page')")
	public Result<List<ProdProp>> loadProdPropList() {
		List<ProdProp> prodProps = prodPropService.queryProdPropList();
		return Result.success(prodProps);
	}

	/**
	 * 根据商品属性id查询属性值集合
	 * @param propId 商品属性id
	 */
	@ApiOperation("根据商品属性id查询属性值集合")
	@GetMapping("listSpecValue/{propId}")
	@PreAuthorize("hasAuthority('prod:spec:page')")
	public Result<List<ProdPropValue>> loadProdPropValues(@PathVariable Long propId) {
		List<ProdPropValue> prodPropValues = prodPropValueService.list(new LambdaQueryWrapper<ProdPropValue>()
				.eq(ProdPropValue::getPropId,propId)
		);
		return Result.success(prodPropValues);
	}
}
