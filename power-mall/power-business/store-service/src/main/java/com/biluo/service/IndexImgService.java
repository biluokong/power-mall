package com.biluo.service;

import com.biluo.domain.IndexImg;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IndexImgService extends IService<IndexImg>{


    /**
     * 新增轮播图
     * @param indexImg
     * @return
     */
    Boolean saveIndexImg(IndexImg indexImg);

    /**
     * 根据标识查询轮播图信息
     * @param imgId
     * @return
     */
    IndexImg queryIndexImgInfoById(Long imgId);

    /**
     * 修改轮播图内容
     * @param indexImg
     * @return
     */
    Boolean modifyIndexImg(IndexImg indexImg);

    /**
     * 批量删除轮播图
     * @param imgIds
     * @return
     */
    Boolean removeIndexImgByIds(List<Long> imgIds);

    /**
     * 查询小程序轮播图列表
     * @return
     */
    List<IndexImg> queryWxIndexImgList();

}
