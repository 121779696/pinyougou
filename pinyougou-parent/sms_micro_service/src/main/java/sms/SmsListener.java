package sms;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @description:
 * @createTime: 2018-12-28 20:48
 */

@Component
public class SmsListener {
    @Autowired
    private SmsUtil smsUtil;

    @JmsListener(destination = "sms")
    public void sendSms(Map<String,String> map){
        try {
            SendSmsResponse response = smsUtil.sendSms(map.get("mobile"),
                    map.get("signName"),
                    map.get("templateCode"),
                    map.get("param"));
            System.out.println("code:"+response.getCode());
            System.out.println("message:"+response.getMessage());
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
