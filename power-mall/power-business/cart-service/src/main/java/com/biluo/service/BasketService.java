package com.biluo.service;

import com.biluo.domain.Basket;
import com.baomidou.mybatisplus.extension.service.IService;
import com.biluo.vo.CartTotalAmount;
import com.biluo.vo.CartVo;

import java.util.List;

public interface BasketService extends IService<Basket>{


    /**
     * 查询会员购物车中商品数量
     * @param openId
     * @return
     */
    Integer queryMemberBasketProdCount(String openId);

    /**
     * 查询会员购物车页面数据
     * @return
     */
    CartVo queryMemberCartVo();

    /**
     * 计算会员选中购物车中商品的金额
     * @param basketIds
     * @return
     */
    CartTotalAmount calculateMemberCheckedBasketTotalAmount(List<Long> basketIds);

    /**
     * 添加商品到购物车或修改商品在购物车中的数量
     * @param basket
     * @return
     */
    Boolean changeCartItem(Basket basket);
}
