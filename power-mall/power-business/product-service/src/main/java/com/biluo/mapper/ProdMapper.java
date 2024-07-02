package com.biluo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.biluo.domain.Prod;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface ProdMapper extends BaseMapper<Prod> {

	@Update("update prod set sold_num = sold_num - #{count}," +
			"total_stocks = total_stocks + #{count}," +
			"version = version +1 " +
			"where prod_id = #{prodId} " +
			"and version = #{version} " +
			"and (total_stocks + #{count}) >= 0")
	Integer updateProdStock(@Param("prodId") Long prodId, @Param("count") Integer count, @Param("version") Integer version);
}
