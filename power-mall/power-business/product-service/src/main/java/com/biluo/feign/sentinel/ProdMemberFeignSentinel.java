package com.biluo.feign.sentinel;

import com.biluo.domain.Member;
import com.biluo.feign.ProdMemberFeign;
import com.biluo.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 *
 */
@Component
@Slf4j
public class ProdMemberFeignSentinel implements ProdMemberFeign {
    @GetMapping("p/user/getMemberListByOpenIds")
    @Override
    public Result<List<Member>> getMemberListByOpenIds(List<String> openIds) {
        log.error("远程调用：根据会员openId集合查询会员对象集合失败");
        return null;
    }
}
