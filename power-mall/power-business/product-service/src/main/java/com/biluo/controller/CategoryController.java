package com.biluo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.biluo.domain.Category;
import com.biluo.model.Result;
import com.biluo.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品类目控制层
 */
@Api(tags = "商品类目接口管理")
@RequestMapping("prod/category")
@RestController
@RequiredArgsConstructor
public class CategoryController {
	private final CategoryService categoryService;

	/**
	 * 查询系统所有商品类目
	 */
	@ApiOperation("查询系统所有商品类目")
	@GetMapping("table")
	@PreAuthorize("hasAuthority('prod:category:page')")
	public Result<List<Category>> loadAllCategoryList() {
		List<Category> list = categoryService.queryAllCategoryList();
		return Result.success(list);
	}

	/**
	 * 查询系统商品一级类目
	 */
	@ApiOperation("查询系统商品一级类目")
	@GetMapping("listCategory")
	@PreAuthorize("hasAuthority('prod:category:page')")
	public Result<List<Category>> loadFirstCategoryList() {
		List<Category> list = categoryService.queryFirstCategoryList();
		return Result.success(list);
	}

	/**
	 * 新增商品类目
	 *
	 * @param category 商品类目对象
	 */
	@ApiOperation("新增商品类目")
	@PostMapping
	@PreAuthorize("hasAuthority('prod:category:save')")
	public Result<String> saveCategory(@RequestBody Category category) {
		categoryService.saveCategory(category);
		return Result.success();
	}

	/**
	 * 根据标识查询商品类目详情
	 *
	 * @param categoryId 商品类目标识
	 */
	@ApiOperation("根据标识查询商品类目详情")
	@GetMapping("info/{categoryId}")
	@PreAuthorize("hasAuthority('prod:category:info')")
	public Result<Category> loadCategoryInfo(@PathVariable Long categoryId) {
		Category category = categoryService.getById(categoryId);
		return Result.success(category);
	}

	/**
	 * 修改商品类目信息
	 *
	 * @param category 商品类目对象
	 */
	@ApiOperation("修改商品类目信息")
	@PutMapping
	@PreAuthorize("hasAuthority('prod:category:update')")
	public Result modifyCategory(@RequestBody Category category) {
		categoryService.modifyCategory(category);
		return Result.success();
	}

	/**
	 * 删除商品类目
	 *
	 * @param categoryId 商品类目标识
	 */
	@ApiOperation("删除商品类目")
	@DeleteMapping("{categoryId}")
	@PreAuthorize("hasAuthority('prod:category:delete')")
	public Result<String> removeCategory(@PathVariable Long categoryId) {
		categoryService.removeCategoryById(categoryId);
		return Result.success();
	}

	////////////////// 微信小程序数据接口 //////////////////////////

	/**
	 * 查询小程序中商品的一级类目集合
	 *
	 * @param parentId 父节点id
	 * @return
	 */
	@ApiOperation("查询小程序中商品的一级类目集合")
	@GetMapping("category/list")
	public Result<List<Category>> loadWxCategoryList(@RequestParam Long parentId) {
		List<Category> list = categoryService.queryWxCategoryListByPid(parentId);
		return Result.success(list);
	}

	///////////////////////// feign 接口 ///////////////////////////////
	@GetMapping("getCategoryListByParentId")
	public Result<List<Category>> getCategoryListByParentId(@RequestParam Long parentId) {
		// 查询商品类目的子类目集合
		List<Category> list = categoryService.lambdaQuery().eq(Category::getParentId, parentId).list();
		return Result.success(list);
	}
}
