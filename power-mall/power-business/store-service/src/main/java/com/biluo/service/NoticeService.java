package com.biluo.service;

import com.biluo.domain.Notice;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface NoticeService extends IService<Notice>{


    /**
     * 新增公告
     * @param notice
     * @return
     */
    Boolean saveNotice(Notice notice);

    /**
     * 修改公告内容
     * @param notice
     * @return
     */
    Boolean modifyNotice(Notice notice);

    /**
     * 查询小程序置顶公告列表
     * @return
     */
    List<Notice> queryWxTopNoticeList();

    /**
     * 查询小程序所有公告列表
     * @return
     */
    List<Notice> queryWxAllNoticeList();

}
