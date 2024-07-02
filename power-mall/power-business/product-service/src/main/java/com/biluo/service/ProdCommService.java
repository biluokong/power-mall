package com.biluo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.biluo.domain.ProdComm;
import com.baomidou.mybatisplus.extension.service.IService;
import com.biluo.vo.ProdCommData;

public interface ProdCommService extends IService<ProdComm>{


    /**
     * 回复和审核商品评论
     * @param prodComm
     * @return
     */
    void replyAndExamineProdComm(ProdComm prodComm);

    /**
     * 小程序查询商品评论总览信息
     * @param prodId
     * @return
     */
    ProdCommData queryWxProdCommDataByProdId(Long prodId);

    /**
     * 小程序分页查询单个商品评论列表
     * @param current
     * @param size
     * @param prodId
     * @param evaluate
     * @return
     */
    Page<ProdComm> queryWxProdCommPageByProd(Long current, Long size, Long prodId, Long evaluate);
}
