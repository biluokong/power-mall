package com.biluo.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.biluo.domain.Member;
import com.biluo.mapper.MemberMapper;
import com.biluo.service.MemberService;
import com.biluo.util.AuthUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService{


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean modifyMemberInfoByOpenId(Member member) {
        // 获取会员的openid
        String openId = AuthUtils.getMemberOpenId();
        // 更新会员的头像和昵称
        return lambdaUpdate().eq(Member::getOpenId,openId).update(member);
    }
}
