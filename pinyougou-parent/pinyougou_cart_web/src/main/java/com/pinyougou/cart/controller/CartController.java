package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @description:
 * @createTime: 2018-12-30 14:46
 */

@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    //从cookie中取出购物车
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){

        //得到登陆人账号,判断当前是否有人登陆
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        String cartListString = CookieUtil.getCookieValue(request, "cartList", "utf-8");
        if (cartListString==null||cartListString.equals("")) {
            cartListString = "[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);
        //如果未登录,读取本地购物车
        if (username.equals("anonymousUser")) {
            return cartList_cookie;
        //如果已登录,从redis中提取
        }else {
            List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
            if (cartList_cookie.size() > 0) {  //如果本地存在购物车
                //合并购物车
                cartList_redis = cartService.mergeCartList(cartList_redis, cartList_cookie);
                //清除本地cookie的数据
                CookieUtil.deleteCookie(request,response,"cartList");
                //将合并后的数据存入redis
                cartService.saveCartListToRedis(username,cartList_redis);
                System.out.println("合并了购物车。。。。。");
            }
            return cartList_redis;
        }

    }

    //添加商品到购物车
    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId,Integer num){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录用户:"+username);

        try {
            //获取购物车列表
            List<Cart> cartList = findCartList();
            cartList= cartService.addGoodsToCartList(cartList, itemId, num);
            if (username.equals("anonymousUser")){
                //如果是未登录，保存到cookie
                String s = JSON.toJSONString(cartList);
                CookieUtil.setCookie(request,response,"cartList",s,3600*24,"utf-8");
                System.out.println("向cookie存入数据");
             //如果是已登录，保存到redis
            }else {
                cartService.saveCartListToRedis(username,cartList);
            }

            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }



}
