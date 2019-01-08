package com.pinyougou.seckill.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @createTime: 2019-01-04 09:33
 */

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WeixinPayService weixinPayService;

    @Reference
    private SeckillOrderService seckillOrderService;

    @RequestMapping("/createNative")
    public Map createNative(){
        //获取当前用户
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        //提前订单
        TbSeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedisByUserId(userId);
        //判断支付日志存在
        if (seckillOrder != null) {
            return weixinPayService.createNative(seckillOrder.getId()+"",(long)(seckillOrder.getMoney().doubleValue()*100)+"");
        }else {
            return new HashMap();
        }
    }


    //查询支付状态
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Result result=null;
        int x = 0;
        while(true){
            //调用查询接口
            Map<String,String> map = weixinPayService.queryPayStatus(out_trade_no);
            if(map==null){//出错
                result=new  Result(false, "支付出错");
                break;
            }
            if(map.get("trade_state").equals("SUCCESS")){//如果成功
                result=new  Result(true, "支付成功");
                //修改订单状态
               // orderService.updateOrderStatus(out_trade_no,map.get("transaction_id"));
                seckillOrderService.saveOrderFromRedisToDb(userId,Long.valueOf(out_trade_no), map.get("transaction_id"));
                break;
            }
            try {
                Thread.sleep(3000);//间隔三秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            x++;
            if (x >= 5) {
                result = new Result(false,"二维码超时");
                break;
            }

        }
        return result;
    }

}