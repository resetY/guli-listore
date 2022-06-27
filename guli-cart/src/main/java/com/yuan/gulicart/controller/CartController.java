package com.yuan.gulicart.controller;

import com.yuan.gulicart.interceptor.CartInterceptor;
import com.yuan.gulicart.service.CartService;
import com.yuan.gulicart.vo.Cart;
import com.yuan.gulicart.vo.CartItem;
import com.yuan.gulicart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Controller
public class CartController {
    @Autowired
    CartService cartService;


/**
 * 京东做法：浏览器内一个cookie，里面有一个唯一用户身份表示的userkey，一个月后过期
 *      如果是第一次使用jd购物车功能，会给一个临时的用户身份userkey
 *      每次访问都带上这个userkey
 *
 *      登录状态：按照session
 *      不登陆：使用user-key，临时用户没有则需要创建
 * **/
    @GetMapping("/cart.html")
    public String cartListPage(HttpSession session,Model model) throws ExecutionException, InterruptedException { //跳转到列表页面的
//        Object a = session.getAttribute("loginUser");

//            UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
//            if(userInfoTo.getUserKey()==null){ //如果没有用户数据，则临时创建一个
//                String uuid = UUID.randomUUID().toString();
//                userInfoTo.setUserKey(uuid);
//                System.out.println("共享的用户信息："+userInfoTo);
//            }

       Cart cart =  cartService.getCart();
       model.addAttribute("cart",cart);//将购物车项传入到页面进行展示
            return "cartList";
    }



    /***
     * 添加商品到购物车的成功页面
     *
     *     /**
     *      *    redirectAttributes.addFlashAttribute():放在session中，只能取用一次
     *      *    redirectAttributes.addAttribute("skuId",skuId); //重定向携带数据
     *      * **/

    @RequestMapping("/addCart")
    public String addCart(@RequestParam("skuId") Long skuId,
                          @RequestParam("num") Integer num,
                          RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException { //添加的商品id，件数

        //添加成功页面需要展示添加商品的信息
        CartItem cartItem = cartService.addCart(skuId,num);
      //  System.out.println("购物项："+cartItem.toString());
       // model.addAttribute("cart",cartItem);
       redirectAttributes.addAttribute("skuId",skuId); //重定向携带数据
        return "redirect:http://cart.guli.com/addCart.html";
    }


    //用于解决重复提交添加到购物车，重定向到这个接口进行展示数据
    @RequestMapping("/addCart.html")
    public String addCartSuccess(@RequestParam("skuId") Long skuId,
                                 Model model){
        CartItem cartItem   =  cartService.getCartItem(skuId); //获取购物车中某个购物项
       model.addAttribute("cart",cartItem);
        return "success"; //通过重定向接口来跳转success
    }


    @RequestMapping("/checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId,
                            @RequestParam("check") Integer  check){
        cartService.checkItem(skuId,check);
        return "redirect:http://cart.guli.com/cart.html";
    }


    @RequestMapping("/changeCount")
    public String changeCount(@RequestParam("skuId") Long skuId,
                              @RequestParam("num") Integer num){
        cartService.changeCount(skuId,num);
        return "redirect:http://cart.guli.com/cart.html";
    }
    @RequestMapping("/delItem")
    public String delItem(@RequestParam("skuId") Long skuId){
        cartService.delItem(skuId);
        return "redirect:http://cart.guli.com/cart.html";
    }



}

