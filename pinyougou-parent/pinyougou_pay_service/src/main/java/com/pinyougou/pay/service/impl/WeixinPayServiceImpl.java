package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;
import util.HttpClient;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @createTime: 2019-01-03 21:54
 */

@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner;

    @Value("${notifyurl}")
    private String notify_url;

    @Value("${partnerkey}")
    private String partnerkey;

    //生成微信支付二维码
    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        //创建参数
        Map<String,String> param = new HashMap<>();
        //公众号
        param.put("appid",appid);
        //商户号
        param.put("mch_id",partner);
        //随机字符串
        param.put("nonce_str", WXPayUtil.generateNonceStr());
        //商品描述
        param.put("body","品优购");
        //商户订单号
        param.put("out_trade_no",out_trade_no);
        //标价金额
        param.put("total_fee",total_fee);
        //终端IP
        param.put("spbill_create_ip","127.0.0.1");
        //通知地址
        param.put("notify_url",notify_url);
        //交易类型
        param.put("trade_type","NATIVE");

        try {
            //2.生成要发送的xml
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println("请求参数："+xmlParam);

            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            client.setHttps(true);//是否是https协议
            client.setXmlParam(xmlParam);//发送的xml数据
            client.post();//执行post请求

            //3.获取结果
            String result = client.getContent();

            Map<String, String> resultMap = WXPayUtil.xmlToMap(result);
            System.out.println("微信返回的结果："+resultMap);
            Map<String ,String> map = new HashMap();
            map.put("code_url",resultMap.get("code_url"));
            map.put("total_fee",total_fee);
            map.put("out_trade_no",out_trade_no);
            return map;

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }
    }

    @Override
    public Map queryPayStatus(String out_trade_no) {
        //封装参数
        Map param = new HashMap();
        param.put("appid",appid);//公众账号ID
        param.put("mch_id",partner);//商户号
        param.put("out_trade_no",out_trade_no);//订单号
        param.put("nonce_str",WXPayUtil.generateNonceStr());//随机字符串
        String url = "https://api.mch.weixin.qq.com/pay/orderquery";

        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            //发送请求
            HttpClient client = new HttpClient(url);
            client.setHttps(true);
            client.setXmlParam(xmlParam);
            client.post();

            //获取结果
            String xmlResult = client.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(xmlResult);
            System.out.println("调用查询API返回结果："+map);
            return map;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
