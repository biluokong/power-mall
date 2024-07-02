package com.biluo.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.biluo.constant.MemberConstants;
import com.biluo.domain.MemberAddr;
import com.biluo.mapper.MemberAddrMapper;
import com.biluo.service.MemberAddrService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@CacheConfig(cacheNames = MemberConstants.MEMBER_ADDR_PREFIX)
public class MemberAddrServiceImpl extends ServiceImpl<MemberAddrMapper, MemberAddr> implements MemberAddrService {

	@Override
	@Cacheable(key = "#openId")
	public List<MemberAddr> queryMemberAddrListByOpenId(String openId) {
		return lambdaQuery()
				.eq(MemberAddr::getOpenId, openId)
				.eq(MemberAddr::getStatus, 1)
				.orderByDesc(MemberAddr::getCommonAddr, MemberAddr::getCreateTime)
				.list();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(key = "#openId")
	public Boolean saveMemberAddr(MemberAddr memberAddr, String openId) {
		// 补充收货地址信息
		memberAddr.setCommonAddr(0);
		memberAddr.setStatus(1);
		memberAddr.setCreateTime(new Date());
		memberAddr.setUpdateTime(new Date());
		memberAddr.setOpenId(openId);
		// 根据会员openId查询会员收货地址数量
		Long count = lambdaQuery().eq(MemberAddr::getOpenId, openId).count();
		// 判断会员是否有收货地址
		if (0 == count) {    // 如果当前会员新增的收货地址为第1个，则设置为默认收货地址
			memberAddr.setCommonAddr(1);
		}
		return save(memberAddr);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(key = "#openId")
	public Boolean modifyMemberAddrInfo(MemberAddr memberAddr, String openId) {
		memberAddr.setUpdateTime(new Date());
		return updateById(memberAddr);
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(key = "#openId")
	public Boolean removeMemberAddrById(Long addrId, String openId) {
		// 根据收货地址id查询收货地址对象
		MemberAddr memberAddr = getById(addrId);
		// 判断是否为默认收货地址
		if (memberAddr.getCommonAddr().equals(1)) {
			// 说明：当前删除的收货地址是会员默认收货地址，重新获取一个新的地址（最近刚刚新增的）作为默认收货地址
			// 根据会员openId查询会员非默认收货地址
			List<MemberAddr> memberAddrs = lambdaQuery()
					.eq(MemberAddr::getOpenId, openId)
					.eq(MemberAddr::getCommonAddr, 0)
					.orderByDesc(MemberAddr::getCreateTime)
					.list();
			// 判断非默认收货地址是否有值
			if (ObjectUtil.isNotEmpty(memberAddrs)) {
				// 如果删除地址后会员没有了默认收货地址，则把第1个地址并设置为新的默认收货地址
				MemberAddr newDefaultMemberAddr = memberAddrs.get(0);
				newDefaultMemberAddr.setCommonAddr(1);
				newDefaultMemberAddr.setUpdateTime(new Date());
				updateById(newDefaultMemberAddr);
			}
		}
		// 说明：当前删除的收货地址非默认收货地址
		return removeById(addrId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(key = "#openId")
	public Boolean modifyMemberDefaultAddr(String openId, Long newAddrId) {
		// 根据收货地址标识查询收货地址对象
		MemberAddr newDefaultMemberAddr = getById(newAddrId);
		// 判断新的默认收货地址是否为原有的默认收货地址
		if (newDefaultMemberAddr.getCommonAddr().equals(1)) {
			return true;
		}
		// 不是，则将之前的默认收货地址修改为非默认，并更新当前新的默认收货地址
		// 将会员原有的默认收货地址设置为非默认
		MemberAddr oldDefaultMemberAddr = new MemberAddr();
		oldDefaultMemberAddr.setCommonAddr(0);
		oldDefaultMemberAddr.setUpdateTime(new Date());
		lambdaUpdate().eq(MemberAddr::getOpenId, openId).update(oldDefaultMemberAddr);

		// 将当前收货地址设置的新的默认收货地址
		newDefaultMemberAddr.setCommonAddr(1);
		newDefaultMemberAddr.setUpdateTime(new Date());

		return updateById(newDefaultMemberAddr);
	}
}
