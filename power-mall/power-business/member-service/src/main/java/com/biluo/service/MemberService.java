package com.biluo.service;

import com.biluo.domain.Member;
import com.baomidou.mybatisplus.extension.service.IService;
public interface MemberService extends IService<Member>{


    /**
     * 更新会员的头像和昵称
     * @param member
     * @return
     */
    Boolean modifyMemberInfoByOpenId(Member member);
}
