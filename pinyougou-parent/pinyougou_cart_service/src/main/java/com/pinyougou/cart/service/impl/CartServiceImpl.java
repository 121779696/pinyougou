package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @createTime: 2018-12-30 11:23
 */
@Service(timeout = 50000)
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.根据skuID查询商品明细SKU的对象
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        //健壮性判断
        if (item == null) {
            throw new RuntimeException("商品不存在。");
        }
        if (!item.getStatus().equals("1")){
            throw new RuntimeException("商品状态不合法");
        }
        //2.根据SKU对象得到商家ID
        String sellerId = item.getSellerId();
        //3.根据商家ID判断购物车列表中是否存在该商家的购物车	-> Cart
        Cart cart = searchCartBySellerId(cartList,sellerId);
        //4.如果购物车列表中不存在该商家的购物车 Cart = null
        if (cart == null) {
            //4.1 新建购物车对象  new Cart()
            cart = new Cart();
            cart.setSellerId(sellerId);//设置商家ID
            cart.setSellerName(item.getSeller());//设置商家名称
            //4.2 新建一个购物车明细对象设置商品id 数量  金额 tbOrderItem
            List<TbOrderItem> orderItemList = new ArrayList<TbOrderItem>();
            TbOrderItem orderItem = createOrderItem(item,num);
            //4.3 将新建的购物车对象添加到购物车列表	cartList.add()
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);
            cartList.add(cart);

        }else {  //5.如果购物车列表中存在该商家的购物车	Cart != null
            // 查询购物车明细列表中是否存在该商品
            TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(),itemId);
            if (orderItem == null) {
                //5.1. 如果没有，新增购物车明细
                orderItem = createOrderItem(item,num);
                cart.getOrderItemList().add(orderItem);
            }else {
                //5.2. 如果有，在原购物车明细上添加数量，更改金额
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));
                //如果数量操作后小于等于0，则移除
                if (orderItem.getNum() <= 0) {
                    //移除购物车明细
                    cart.getOrderItemList().remove(orderItem);
                }
                //如果移除后cart的明细数量为0，则将cart移除
                if (cart.getOrderItemList().size() == 0) {
                    cartList.remove(cart);
                }
            }
        }
        return cartList;
    }

    //根据skuID在购物车明细列表中查询购物车明细对象
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().longValue()==itemId.longValue()){
                return orderItem;
            }
        }
        return null;
    }

    //创建购物车明细对象
    private TbOrderItem createOrderItem(TbItem item,Integer num) {
        if(num<=0){
            throw new RuntimeException("数量非法");
        }
        //创建新的购物车明细对象
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setItemId(item.getId());
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setTitle(item.getTitle());
        orderItem.setPrice(item.getPrice());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
        return orderItem;
    }

    //根据商家ID在购物车列表中查询购物车对象
    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> findCartListFromRedis(String username) {
        System.out.println("从redis中提取购物车数据....."+username);
        List<Cart> cartList= (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if (cartList == null) {
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        System.out.println("向redis中存入购物车数据....." + username);
        redisTemplate.boundHashOps("cartList").put(username,cartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        System.out.println("合并购物车");
        for (Cart cart : cartList2) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                Long itemId = orderItem.getItemId();
                Integer num = orderItem.getNum();
                cartList1 = addGoodsToCartList(cartList1, itemId, num);
            }
        }
        return cartList1;
    }

}



