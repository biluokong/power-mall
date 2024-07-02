package com.biluo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.biluo.domain.MemberCollection;
import com.baomidou.mybatisplus.extension.service.IService;
import com.biluo.domain.Prod;

public interface MemberCollectionService extends IService<MemberCollection>{


    /**
     * 查询会员收藏商品的数量
     * @return
     */
    Long queryMemberCollectionProdCount();

    /**
     * 分页查询会员收藏商品列表
     * @param openId
     * @param current
     * @param size
     * @return
     */
    Page<Prod> queryMemberCollectionProdPageByOpenId(String openId, Long current, Long size);

    /**
     * 添加或取消收藏商品
     * @param openId
     * @param prodId
     * @return
     */
    Boolean addOrCancelMemberCollection(String openId, Long prodId);
}
