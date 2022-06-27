package com.yuan.guli.gulimember.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.to.SocailUser;
import com.yuan.common.utils.PageUtils;
import com.yuan.guli.gulimember.entity.MemberEntity;
import com.yuan.guli.gulimember.vo.LoginVo;
import com.yuan.guli.gulimember.vo.RegistVo;

import java.util.Map;

/**
 * 会员
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-08 10:30:09
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void regist(RegistVo registVo);


    /**
     * 校验方法
     * */
    void checkEmailUnique (String email);
    void checkUsernameUnique (String username);
    void checkPhoneUnique(String phone);

    MemberEntity login(LoginVo loginVo);


    //登录重载方法：社交登录
    MemberEntity login(SocailUser socailUser) throws Exception;
}

