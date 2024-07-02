package com.biluo.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.biluo.constant.BusinessEnum;
import com.biluo.domain.MemberCollection;
import com.biluo.domain.Prod;
import com.biluo.ex.handler.BusinessException;
import com.biluo.feign.MemberProdFeign;
import com.biluo.mapper.MemberCollectionMapper;
import com.biluo.model.Result;
import com.biluo.service.MemberCollectionService;
import com.biluo.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberCollectionServiceImpl extends ServiceImpl<MemberCollectionMapper, MemberCollection> implements MemberCollectionService {
	private final MemberProdFeign memberProdFeign;

	@Override
	public Long queryMemberCollectionProdCount() {
		// 获取会员openid
		String openId = AuthUtils.getMemberOpenId();
		// 根据会员openid查询会员收藏商品的数量
		return lambdaQuery().eq(MemberCollection::getOpenId, openId).count();
	}

	@Override
	public Page<Prod> queryMemberCollectionProdPageByOpenId(String openId, Long current, Long size) {
		// 创建商品分页对象
		Page<Prod> prodPage = new Page<>(current,size);
		// 创建会员与商品收藏关系分页对象
		Page<MemberCollection> memberCollectionPage = new Page<>(current,size);
		// 根据会员openId分页查询会员与商品的收藏关系记录
		memberCollectionPage = lambdaQuery()
				.eq(MemberCollection::getOpenId, openId)
				.orderByDesc(MemberCollection::getCreateTime)
				.page(memberCollectionPage);
		// 从会员与商品收藏关系分页对象中获取收藏记录
		List<MemberCollection> memberCollectionList = memberCollectionPage.getRecords();
		// 判断是否有值
		if (ObjectUtil.isEmpty(memberCollectionList)) {
			return prodPage;
		}
		// 从会员与商品收藏关系集合中获取收藏商品的id集合
		List<Long> prodIdList = memberCollectionList.stream().map(MemberCollection::getProdId).collect(Collectors.toList());
		// 远程调用：根据商品id集合查询商品对象集合
		Result<List<Prod>> result = memberProdFeign.getProdListByIds(prodIdList);
		if (BusinessEnum.OPERATION_FALL.getCode() == result.getCode()) {
			throw new BusinessException(BusinessEnum.QUERY_FEIGN_PROD_LIST_FAIL);
		}
		// 获取数据
		List<Prod> prodList = result.getData();
		// 将商品对象集合赋值给商品分页对象
		prodPage.setRecords(prodList);
		prodPage.setTotal(memberCollectionPage.getTotal());
		prodPage.setPages(memberCollectionPage.getPages());
		return prodPage;
	}

	@Override
	public Boolean addOrCancelMemberCollection(String openId, Long prodId) {
		// 根据会员openId和商品id查询收藏记录
		MemberCollection memberCollection = lambdaQuery()
				.eq(MemberCollection::getOpenId, openId)
				.eq(MemberCollection::getProdId, prodId)
				.one();
		// 判断收藏记录是否存在
		if (ObjectUtil.isNull(memberCollection)) {
			// 为空，说明当前商品没有被收藏，则将当前商品添加到收藏记录
			memberCollection = new MemberCollection();
			memberCollection.setCreateTime(new Date());
			memberCollection.setOpenId(openId);
			memberCollection.setProdId(prodId);
			return save(memberCollection);
		}

		// 不为空，说明当前商品已被会员收藏 -> 将当前商品取消收藏记录
		return removeById(memberCollection.getId());
	}
}
