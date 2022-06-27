package com.yuan.guli.gulimember.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.yuan.common.exception.CodeEnume;
import com.yuan.common.exception.RRException;
import com.yuan.common.to.SocailUser;
import com.yuan.guli.gulimember.error.PasswordNotFoundException;
import com.yuan.guli.gulimember.error.PhoneExistException;
import com.yuan.guli.gulimember.error.UserNotFoundException;
import com.yuan.guli.gulimember.error.UsernameExistException;
import com.yuan.guli.gulimember.feign.CouponFeignService;
import com.yuan.guli.gulimember.vo.LoginVo;
import com.yuan.guli.gulimember.vo.RegistVo;
import org.apache.commons.codec.language.bm.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yuan.guli.gulimember.entity.MemberEntity;
import com.yuan.guli.gulimember.service.MemberService;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.R;



/**
 * 会员
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-08 10:30:09
 */
@RestController
@RequestMapping("gulimember/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

   @Autowired
   CouponFeignService couponFeignService;

    //测试：远程调用优惠券功能测试，在会员功能调用优惠券
    @RequestMapping("/coupons")
    public R getcounponTest(){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("郑凯婷");
        R membercoupon = couponFeignService.membercoupon();
        return R.ok().put("member",memberEntity).put("coupons",membercoupon.get("coupons"));  //返回优惠券
    }




    /**
     * 列表
     */
    @RequestMapping("/list")
 //   @RequiresPermissions("gulimember:member:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("gulimember:member:info")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);
        return R.ok().put("member", member);
    }




    @RequestMapping("/regist")
    public R regist(@RequestBody RegistVo registVo){
      try {
          memberService.regist(registVo);
      }catch (PhoneExistException pe){
          return  R.error(CodeEnume.PHONE_EXCEPTION.getCode(),CodeEnume.PHONE_EXCEPTION.getMessage());
      }catch (UsernameExistException ue){
          return  R.error(CodeEnume.USER_EXCEPTION.getCode(),CodeEnume.USER_EXCEPTION.getMessage());
      }
        return R.ok();
    }

    @RequestMapping("/login")
    public R login(@RequestBody LoginVo loginVo){
        MemberEntity user = new MemberEntity();
        try {
          user = memberService.login(loginVo);
        }catch (UserNotFoundException uf){
            return R.error(CodeEnume.USER_LOGIN_EXCEPTION.getCode(),CodeEnume.USER_LOGIN_EXCEPTION.getMessage()).put("msg",CodeEnume.USER_LOGIN_EXCEPTION.getMessage());
        }catch (PasswordNotFoundException pw){
            return R.error(CodeEnume.PWS_LOGIN_EXCEPTION.getCode(),CodeEnume.PWS_LOGIN_EXCEPTION.getMessage()).put("msg",CodeEnume.PWS_LOGIN_EXCEPTION.getMessage());
        }
//        return  R.ok().put("user",user.getUsername()); //保存用户名数据
        return  R.ok().setData(user);
    }
    /**
     *
     * 社交登录接口
     * **/
    @RequestMapping("/oauth2/login")
    public R oauthLogin(@RequestBody SocailUser socailUser) throws Exception {
        MemberEntity user = new MemberEntity();
     MemberEntity memberEntity =  memberService.login(socailUser);
     if(memberEntity!=null){
         return R.ok().setData(memberEntity); //返回结果并且保存数据给调用方
     }else{
         return R.error(CodeEnume.USER_LOGIN_EXCEPTION.getCode(),CodeEnume.USER_LOGIN_EXCEPTION.getMessage());
     }
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("gulimember:member:save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("gulimember:member:update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("gulimember:member:delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
