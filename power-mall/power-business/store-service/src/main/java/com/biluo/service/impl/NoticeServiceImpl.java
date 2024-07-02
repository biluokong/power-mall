package com.biluo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.biluo.constant.StoreConstants;
import com.biluo.domain.Notice;
import com.biluo.mapper.NoticeMapper;
import com.biluo.service.NoticeService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@CacheConfig(cacheNames = "com.biluo.service.impl.NoticeServiceImpl")
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {


	@Override
	@Transactional(rollbackFor = Exception.class)
	@Caching(evict = {
			@CacheEvict(key = StoreConstants.WX_TOP_NOTICE),
			@CacheEvict(key = StoreConstants.WX_ALL_NOTICE)
	})
	public Boolean saveNotice(Notice notice) {
		notice.setShopId(1L);
		notice.setCreateTime(new Date());
		notice.setUpdateTime(new Date());
		return save(notice);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@Caching(evict = {
			@CacheEvict(key = StoreConstants.WX_TOP_NOTICE),
			@CacheEvict(key = StoreConstants.WX_ALL_NOTICE)
	})
	public Boolean modifyNotice(Notice notice) {
		notice.setUpdateTime(new Date());
		return updateById(notice);
	}

	@Override
	@Cacheable(key = StoreConstants.WX_TOP_NOTICE)
	public List<Notice> queryWxTopNoticeList() {
		return lambdaQuery()
				.eq(Notice::getStatus,1)
				.eq(Notice::getIsTop,1)
				.orderByDesc(Notice::getCreateTime)
				.list();
	}

	@Override
	@Cacheable(key = StoreConstants.WX_ALL_NOTICE)
	public List<Notice> queryWxAllNoticeList() {
		return lambdaQuery()
				.eq(Notice::getStatus,1)
				.orderByDesc(Notice::getCreateTime)
				.list();
	}
}
