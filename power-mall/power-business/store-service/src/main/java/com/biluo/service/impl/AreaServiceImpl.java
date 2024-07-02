package com.biluo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.biluo.constant.StoreConstants;
import com.biluo.domain.Area;
import com.biluo.mapper.AreaMapper;
import com.biluo.service.AreaService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = StoreConstants.STORE_KEY_PREFIX)
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements AreaService{


    @Override
    @Cacheable(key = StoreConstants.ALL_AREA_KEY)
    public List<Area> queryAllAreaList() {
        return list();
    }
}
