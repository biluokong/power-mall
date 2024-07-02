package com.biluo.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.biluo.constant.BusinessEnum;
import com.biluo.domain.Member;
import com.biluo.domain.ProdComm;
import com.biluo.ex.handler.BusinessException;
import com.biluo.feign.ProdMemberFeign;
import com.biluo.mapper.ProdCommMapper;
import com.biluo.model.Result;
import com.biluo.service.ProdCommService;
import com.biluo.vo.ProdCommData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProdCommServiceImpl extends ServiceImpl<ProdCommMapper, ProdComm> implements ProdCommService {
	private final ProdMemberFeign prodMemberFeign;

	@Override
	public void replyAndExamineProdComm(ProdComm prodComm) {
		// 获取商品评论内容
		String replyContent = prodComm.getReplyContent();
		// 判断评论内容是否有值
		if (StringUtils.hasText(replyContent)) {
			prodComm.setReplyTime(new Date());
			prodComm.setReplySts(1);
		}
		boolean success = updateById(prodComm);
		if (!success) {
			throw new BusinessException(BusinessEnum.MODIFY_PRODUCT_COMMENT_FAIL);
		}
	}

	@Override
	public ProdCommData queryWxProdCommDataByProdId(Long prodId) {
		// 根据商品id查询商品评论总数量
		Long allCount = lambdaQuery()
				.eq(ProdComm::getProdId, prodId)
				.eq(ProdComm::getStatus, 1)
				.count();
		// 根据商品id查询商品好评数量
		Long goodCount = lambdaQuery()
				.eq(ProdComm::getProdId, prodId)
				.eq(ProdComm::getStatus, 1)
				.eq(ProdComm::getEvaluate, 0)
				.count();
		// 根据商品id查询商品中评数量
		Long secodeCount = lambdaQuery()
				.eq(ProdComm::getProdId, prodId)
				.eq(ProdComm::getStatus, 1)
				.eq(ProdComm::getEvaluate, 1)
				.count();
		// 根据商品id查询商品差评数量
		Long badCount = lambdaQuery()
				.eq(ProdComm::getProdId, prodId)
				.eq(ProdComm::getStatus, 1)
				.eq(ProdComm::getEvaluate, 2)
				.count();
		// 根据商品id查询商品有图评论数量
		Long picCount = lambdaQuery()
				.eq(ProdComm::getProdId, prodId)
				.eq(ProdComm::getStatus, 1)
				.isNotNull(ProdComm::getPics)
				.count();
		// 好评率 = 好评数量 / 评论总数量
		BigDecimal goodLv = BigDecimal.ZERO;
		if (0 != allCount) {
			goodLv = new BigDecimal(goodCount)
					.divide(new BigDecimal(allCount), 3, RoundingMode.HALF_DOWN)
					.multiply(new BigDecimal(100));
		}

		return ProdCommData.builder()
				.allCount(allCount).goodCount(goodCount).secondCount(secodeCount)
				.badCount(badCount).picCount(picCount).goodLv(goodLv)
				.build();
	}

	@Override
	public Page<ProdComm> queryWxProdCommPageByProd(Long current, Long size, Long prodId, Long evaluate) {
		// 创建评论分页对象
		Page<ProdComm> page = new Page<>(current, size);
		// 根据商品id分页查询单个商品的评论
		page = lambdaQuery()
				.eq(ProdComm::getProdId, prodId)
				.eq(ProdComm::getStatus, 1)
				.eq(0 == evaluate || 1 == evaluate || 2 == evaluate, ProdComm::getEvaluate, evaluate)
				.isNotNull(3 == evaluate, ProdComm::getPics)
				.orderByDesc(ProdComm::getCreateTime)
				.page(page);
		// 从分页对象中获取评论记录
		List<ProdComm> prodCommList = page.getRecords();
		// 判断是否有值
		if (ObjectUtil.isEmpty(prodCommList)) {
			return page;
		}
		// 从商品评论集合中获取会员openId集合
		List<String> openIdList = prodCommList.stream().map(ProdComm::getOpenId).collect(Collectors.toList());
		// 远程调用：根据会员openId集合查询会员对象集合
		Result<List<Member>> result = prodMemberFeign.getMemberListByOpenIds(openIdList);
		// 判断操作结果
		if (result.getCode() == BusinessEnum.OPERATION_FALL.getCode()) {
			log.error("远程调用：根据会员openId集合查询会员对象集合失败");
			throw new BusinessException(BusinessEnum.QUERY_FEIGN_MEMBER_LIST_FAIL);
		}
		// 获取数据
		List<Member> memberList = result.getData();
		// 循环遍历评论集合
		prodCommList.forEach(prodComm -> {
			// 从会员对象集合中过滤出与当前会员对象的openId一致的会员对象
			Member member = memberList.stream()
					.filter(m -> m.getOpenId().equals(prodComm.getOpenId()))
					.collect(Collectors.toList()).get(0);
			// 将会员昵称进行脱敏操作
			StringBuilder stringBuilder = new StringBuilder(member.getNickName());
			StringBuilder replaceNickName = stringBuilder.replace(1, stringBuilder.length() - 1, "***");
			prodComm.setNickName(replaceNickName.toString());
			prodComm.setPic(member.getPic());
		});
		return page;
	}
}
