package com.yuan.guli.gulimember.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yuan.common.to.SocailUser;
import com.yuan.common.utils.HttpUtils;
import com.yuan.common.utils.R;
import com.yuan.guli.gulimember.dao.MemberLevelDao;
import com.yuan.guli.gulimember.entity.MemberLevelEntity;
import com.yuan.guli.gulimember.error.PasswordNotFoundException;
import com.yuan.guli.gulimember.error.PhoneExistException;
import com.yuan.guli.gulimember.error.UserNotFoundException;
import com.yuan.guli.gulimember.error.UsernameExistException;
import com.yuan.guli.gulimember.vo.LoginVo;
import com.yuan.guli.gulimember.vo.RegistVo;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.Query;

import com.yuan.guli.gulimember.dao.MemberDao;
import com.yuan.guli.gulimember.entity.MemberEntity;
import com.yuan.guli.gulimember.service.MemberService;
import org.springframework.transaction.annotation.Transactional;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 注册  member=ums表
     */
    @Autowired
    MemberLevelDao memberLevelDao;

    /**
     * 注册方法
     * **/
    @Override
    public void regist(RegistVo registVo) {
        MemberEntity memberEntity = new MemberEntity();

        //需要检查手机、用户名(邮箱：以后需要)是否唯一：
        checkPhoneUnique(registVo.getPhone());
        checkUsernameUnique(registVo.getUserName());

        memberEntity.setUsername(registVo.getUserName());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String md5_password = passwordEncoder.encode(registVo.getPassWord());
      //  boolean p = bCryptPasswordEncoder.matches("123456", "$2a$10$hH4gCT4o4ULrvEmgIDE6nu2PFXBkuPsRDCGdCAjSQ8PuI9CoyPDJS");
        memberEntity.setPassword(md5_password); //数据库存储加密后的密码
        memberEntity.setMobile(registVo.getPhone());
        memberEntity.setCreateTime(new Date());
        memberEntity.setNickname(registVo.getUserName()); //昵称默认保存为用户名


        //查询默认等级id
        MemberLevelEntity memberLevelEntity = memberLevelDao.selectOne(new QueryWrapper<MemberLevelEntity>().eq("default_status", 1));

        memberEntity.setLevelId(memberLevelEntity.getId());

      baseMapper.insert(memberEntity);
    }


    /**
     * 使用异常机制来进行判断的报错，不需要使用布尔类型
     * **/
    @Override
    public void checkEmailUnique(String email) {

        //或者可以使用count的方式来进行判断是否有这个数据
        Long count= baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("email", email));
        if(count>0){
            throw new UsernameExistException();
        }
    }

    @Override
    public void checkUsernameUnique(String username) {
        //MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username", username));
        Long count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));

        if(count!=0){
            throw new UsernameExistException();
        }

    }

    @Override
    public void checkPhoneUnique(String phone) {
        Long count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if(count!=0){
            throw new PhoneExistException();
        }
    }
    /** 登录方法    **/
    @Transactional
    @Override
    public MemberEntity login(LoginVo loginVo) {
        //1.判断该用户是否存在
        MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().
                        eq("username", loginVo.getLoginName()).
                        or().eq("mobile", loginVo.getLoginName()).
                        or().eq("email", loginVo.getLoginName()));

        if(memberEntity == null){
            System.out.println("---------------------------");
            throw new UserNotFoundException();
         }
        //2.判断密码是否正确
        //  boolean p = bCryptPasswordEncoder.matches("123456", "$2a$10$hH4gCT4o4ULrvEmgIDE6nu2PFXBkuPsRDCGdCAjSQ8PuI9CoyPDJS");
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean p = passwordEncoder.matches(loginVo.getPassWord(),memberEntity.getPassword());
        System.out.println("密码验证结果:"+p+"-----------------------------------------------------------");
        if(p){ //如果匹配
            return  memberEntity;
        }
        throw new PasswordNotFoundException(); //抛出错误
    }


    /***
     * 进行社交登录和注册
     * */
    @Transactional
    @Override
    public MemberEntity login(SocailUser socailUser) throws Exception {
        //登录和注册合并逻辑的社交登录

        MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", socailUser.getUid()));
        if(memberEntity!=null){ //该用户存在，直接更换令牌
            memberEntity.setAccessToken(socailUser.getAccess_token());
            memberEntity.setExpiresIn(socailUser.getExpires_in());
            memberEntity.setSocialUid(socailUser.getUid());
        baseMapper.updateById(memberEntity);
        return  memberEntity;
        }else{
            //2.查询不到则进行注册
            MemberEntity new_memberEntity = new MemberEntity();
  try{
      //3.查询当前社交用户的社交账号信息（昵称、性别等）
      Map<String,String> query = new HashMap<>();
      query.put("access_token",socailUser.getAccess_token());
      query.put("uid",socailUser.getUid());
      HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get",
              new HashMap<String, String>(), query);
      if(response.getStatusLine().getStatusCode() == 200){ //查询成功
          String json = EntityUtils.toString(response.getEntity());//转化为json
          JSONObject jsonObject = JSON.parseObject(json);

          String name = jsonObject.getString("name");//微博昵称
          String gender = jsonObject.getString("gender");//m 或者 f
          String image = jsonObject.getString("profile_image_url"); //头像地址
          //导入到数据库进行注册默认信息
          new_memberEntity.setNickname(name);
          new_memberEntity.setGender("m".equals(gender)?1:0);
          new_memberEntity.setHeader(image);
      }
  }catch (Exception e){
  }
            //保存最为重要的三个信息：这几个放在外面，基本状态不为200也要保存
            new_memberEntity.setSocialUid(socailUser.getUid());
            new_memberEntity.setAccessToken(socailUser.getAccess_token());
            new_memberEntity.setExpiresIn(socailUser.getExpires_in());
            //4.插入数据库
            baseMapper.insert(new_memberEntity);
            return  new_memberEntity;
        }
    }

}