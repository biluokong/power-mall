package com.biluo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.biluo.domain.Prod;
import com.biluo.model.ChangeStock;

public interface ProdService extends IService<Prod>{


    /**
     * 新增商品
     * @param prod
     * @return
     */
    void saveProd(Prod prod);

    /**
     * 根据标识查询商品详情
     * @param prodId
     * @return
     */
    Prod queryProdInfoById(Long prodId);

    /**
     * 修改商品信息
     * @param prod
     * @return
     */
    void modifyProdInfo(Prod prod);

    /**
     * 删除商品
     * @param prodId
     * @return
     */
    void removeProdById(Long prodId);

    /**
     * 小程序根据商品标识查询商品详情
     * @param prodId
     * @return
     */
    Prod queryWxProdInfoByProdId(Long prodId);


    /**
	 * 修改商品prod和sku库存数量
	 *
	 * @param changeStock
	 */
    void changeProdAndSkuChangeStock(ChangeStock changeStock);
}
