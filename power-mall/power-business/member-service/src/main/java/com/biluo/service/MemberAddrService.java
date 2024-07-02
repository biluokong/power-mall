package com.biluo.service;

import com.biluo.domain.MemberAddr;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface MemberAddrService extends IService<MemberAddr>{


    /**
     * 查询会员所有收货地址
     * @param openId
     * @return
     */
    List<MemberAddr> queryMemberAddrListByOpenId(String openId);

    /**
     * 新增会员收货地址
     * @param memberAddr
     * @param openId
     * @return
     */
    Boolean saveMemberAddr(MemberAddr memberAddr,String openId);

    /**
     * 修改会员收货地址信息
     * @param memberAddr
     * @param openId
     * @return
     */
    Boolean modifyMemberAddrInfo(MemberAddr memberAddr, String openId);

    /**
     * 删除会员收货地址
     * @param addrId
     * @param openId
     * @return
     */
    Boolean removeMemberAddrById(Long addrId, String openId);

    /**
     * 会员设置默认收货地址
     * @param openId
     * @param newAddrId
     * @return
     */
    Boolean modifyMemberDefaultAddr(String openId, Long newAddrId);
}
