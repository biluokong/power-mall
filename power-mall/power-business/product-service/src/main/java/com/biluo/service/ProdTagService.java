package com.biluo.service;

import com.biluo.domain.ProdTag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ProdTagService extends IService<ProdTag>{


    /**
     * 新增商品分组标签
     * @param prodTag
     * @return
     */
    void saveProdTag(ProdTag prodTag);

    /**
     * 修改商品分组标签信息
     * @param prodTag
     * @return
     */
    void modifyProdTag(ProdTag prodTag);

    /**
     * 查询状态正常的商品分组标签集合
     * @return
     */
    List<ProdTag> queryProdTagList();

    /**
     * 查询小程序商品分组标签
     * @return
     */
    List<ProdTag> queryWxProdTagList();
}
