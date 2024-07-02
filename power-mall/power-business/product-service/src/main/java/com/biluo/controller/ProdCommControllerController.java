package com.biluo.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.biluo.domain.ProdComm;
import com.biluo.model.Result;
import com.biluo.service.ProdCommService;
import com.biluo.vo.ProdCommData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 商品评论管理控制层
 */
@Api(tags = "商品评论接口管理")
@RequestMapping("prod/prodComm")
@RestController
@RequiredArgsConstructor
public class ProdCommControllerController {
	private final ProdCommService prodCommService;

	/**
	 * 多条件分页查询商品评论
	 *
	 * @param current  页码
	 * @param size     每页显示条数
	 * @param prodName 商品名称
	 * @param status   评论状态
	 */
	@ApiOperation("多条件分页查询商品评论")
	@GetMapping("page")
	@PreAuthorize("hasAuthority('prod:prodComm:page')")
	public Result<Page<ProdComm>> loadProdCommPage(Long current, Long size,
												   @RequestParam(required = false) String prodName,
												   @RequestParam(required = false) Integer status) {
		// 创建分页对象
		Page<ProdComm> prodCommPage = new Page<>(current, size);
		// 多条件分页查询商品评论
		prodCommPage = prodCommService.page(prodCommPage, new LambdaQueryWrapper<ProdComm>()
				.eq(ObjectUtil.isNotNull(status), ProdComm::getStatus, status)
				.like(StringUtils.hasText(prodName), ProdComm::getProdName, prodName)
				.orderByDesc(ProdComm::getCreateTime)
		);
		return Result.success(prodCommPage);
	}

	/**
	 * 根据标识查询商品评论详情
	 *
	 * @param id 商品评论标识
	 */
	@ApiOperation("根据标识查询商品评论详情")
	@GetMapping("{id}")
	@PreAuthorize("hasAuthority('prod:prodComm:info')")
	public Result<ProdComm> loadProdCommInfo(@PathVariable Long id) {
		ProdComm prodComm = prodCommService.getById(id);
		return Result.success(prodComm);
	}

	/**
	 * 回复和审核商品评论
	 *
	 * @param prodComm 商品评论对象
	 */
	@ApiOperation("回复和审核商品评论")
	@PutMapping
	@PreAuthorize("hasAuthority('prod:prodComm:update')")
	public Result replyAndExamineProdComm(@RequestBody ProdComm prodComm) {
		prodCommService.replyAndExamineProdComm(prodComm);
		return Result.success();
	}

	////////////////////////// 微信小程序数据接口 ////////////////////////////

	/**
	 * 小程序查询商品评论总览信息
	 *
	 * @param prodId 商品id
	 * @return
	 */
	@ApiOperation("小程序查询商品评论总览信息")
	@GetMapping("prodComm/prodCommData")
	public Result<ProdCommData> loadWxProdCommData(@RequestParam Long prodId) {
		ProdCommData prodCommData = prodCommService.queryWxProdCommDataByProdId(prodId);
		return Result.success(prodCommData);
	}

	/**
	 * 小程序分页查询单个商品评论列表
	 *
	 * @param current  页码
	 * @param size     每页显示条数
	 * @param prodId   商品id
	 * @param evaluate 评论级别
	 * @return
	 */
	@ApiOperation("小程序分页查询单个商品评论列表")
	@GetMapping("prodComm/prodCommPageByProd")
	public Result<Page<ProdComm>> loadWxProdCommPageByProd(Long current, Long size,
														   Long prodId, Long evaluate) {
		// 根据商品分页查询商品评论
		Page<ProdComm> page = prodCommService.queryWxProdCommPageByProd(current, size, prodId, evaluate);
		return Result.success(page);
	}
}
