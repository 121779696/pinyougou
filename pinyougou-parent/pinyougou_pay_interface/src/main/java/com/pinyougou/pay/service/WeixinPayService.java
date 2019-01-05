package com.pinyougou.pay.service;

import java.util.Map;


public interface WeixinPayService {
   /**
    * @description: 统一下单
    * @param: out_trade_no
    * @param: total_fee
    * @return: java.util.Map
    * @createTime: 上午 11:35:20 2019年1月4日, 0004
    */
    public Map createNative(String out_trade_no,String total_fee);
    /**
     * @description: 查询支付状态
     * @param: out_trade_no
     * @return: java.util.Map
     * @createTime: 下午 14:41:44 2019年1月4日, 0004
     */
    public Map queryPayStatus(String out_trade_no);
}
