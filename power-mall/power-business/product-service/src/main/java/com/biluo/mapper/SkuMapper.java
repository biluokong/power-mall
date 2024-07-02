package com.biluo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.biluo.domain.Sku;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface SkuMapper extends BaseMapper<Sku> {

    @Update("update sku set stocks = stocks + #{count}, " +
            "actual_stocks = actual_stocks + #{count}," +
            "version = version + 1 " +
            "where sku_id = #{skuId} " +
            "and version = #{version} " +
            "and  (actual_stocks + #{count}) >= 0")
    Integer updateSkuStock(@Param("skuId") Long skuId, @Param("count") Integer count, @Param("version") Integer version);
}
