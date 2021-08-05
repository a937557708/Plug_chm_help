package com.tjr.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
 
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
/**
 * @author msh
 * @date2020/7/16
 */
public class EchartsUtil {
    /**
     * 临时文件夹路径
     */
    public static final String TEMP_FILE_PATH = "D:/tempFile/";
    private static final String SUCCESS_CODE = "1";
    private static final Logger logger = LoggerFactory.getLogger(EchartsUtil.class);
 
    public static String generateEchartsBase64(String option) throws ClientProtocolException, IOException {
        String base64 = "";
        if (option == null) {
            return base64;
        }
        option = option.replaceAll("\\s+", "").replaceAll("\"", "'");
 
        // 将option字符串作为参数发送给echartsConvert服务器
        Map<String, String> params = new HashMap<>();
        params.put("opt", option);
        String response = HttpUtil.post("http://localhost:6666", params, "utf-8");
 
        // 解析echartsConvert响应
        JSONObject responseJson = JSON.parseObject(response);
        String code = responseJson.getString("code");
 
        // 如果echartsConvert正常返回
        if (SUCCESS_CODE.equals(code)) {
            base64 = responseJson.getString("data");
        }
        // 未正常返回
        else {
            String string = responseJson.getString("msg");
            throw new RuntimeException(string);
        }
        BASE64Decoder decoder = new BASE64Decoder();
        try (OutputStream out = new FileOutputStream("F:\\test.jpg")){
            // 解密
            byte[] b = decoder.decodeBuffer(base64);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            out.write(b);
            out.flush();
        }
        return base64;
    }

    public static void main(String[] args) {
        String option="{'title':{'text':'电流图','subtext':'电流图','x':'left'},'toolbox':{'feature':{'saveAsImage':{'show':true,'title':'保存为图片','type':'png','lang':['点击保存']}},'show':true},'tooltip':{'trigger':'axis'},'legend':{'data':['邮件营销','联盟广告','视频广告']},'xAxis':[{'type':'category','boundaryGap':false,'data':['周一','周二','周三','周四','周五','周六','周日']}],'yAxis':[{'type':'value'}],'series':[{'name':'邮件营销','type':'line','stack':'总量','data':[120,132,101,134,90,230,210]},{'name':'联盟广告','type':'line','stack':'总量','data':[220,182,191,234,290,330,310]},{'name':'视频广告','type':'line','stack':'总量','data':[150,232,201,154,190,330,410]}]}";
        try {
            EchartsUtil.generateEchartsBase64(option);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}