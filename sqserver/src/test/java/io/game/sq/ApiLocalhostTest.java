/**
 * Copyright (c) 2011-2018 All Rights Reserved.
 */
package io.game.sq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.game.sq.httpsrv.signtype.Md5;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.*;

/**
 * @author zengxx
 * @version $Id: PushSdkTest.java 2018年4月12日 下午4:31:31 $
 */
public class ApiLocalhostTest {
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36";

    private static String uri = "http://172.16.3.178:8080/gw/router";
    private static String key = "nviyWPlk78Qh8ALo4jQh6MK2NukUf2YU";
    private static String token = "pk4FSNfUVyVVdfsNxwdB6eQcoWicrLCG1tGv5HmtSiRO7AEYrN6SrfRYdISe55mosTwdWTl/185WahbvO3VhoMSwnBNJx4cw";
    private static String appid = "20202298";

    private static String uid = "8", target = "12";
    private static String cid = "zjqM4433432IlN56543654Ks12";

    private static String username = "15013893009";

    public static void main(String[] args) throws Exception {
//		user_regster();
        for (int i = 0; i < 1000000000; i++) {
            long start = System.currentTimeMillis();
            userLogin();
            System.out.println("耗时：" + (System.currentTimeMillis() - start));
        }
//		idcard();
//		heart();
    }

    protected static void userLogin() {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("method", "user.login");
            data.put("v", "1.0.0");
            data.put("username", username);
            data.put("password", "123456");
            String dataStr = createLinkString(filter(data));
            String sign = Md5.encrypt(dataStr, key, "UTF-8");
            data.put("sign", sign);

            String rsp = post(uri, data);
            System.out.println(rsp);
            if (!StringUtils.isEmpty(rsp)) {
                JSONObject obj = JSON.parseObject(rsp);
                if (obj.containsKey("data")) {
                    token = obj.getJSONObject("data").getString("token");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void user_regster() {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("method", "user.regster");
            data.put("v", "1.0.0");
            data.put("username", username);
            data.put("password", "123456");
            String dataStr = createLinkString(filter(data));
            String sign = Md5.encrypt(dataStr, key, "UTF-8");
            data.put("sign", sign);

            String rsp = post(uri, data);
            System.out.println(rsp);
            if (!StringUtils.isEmpty(rsp)) {
                JSONObject obj = JSON.parseObject(rsp);
                if (obj.containsKey("data")) {
                    token = obj.getJSONObject("data").getString("token");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void idcard() {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("method", "idcard.add");
            data.put("v", "1.0.0");
            data.put("username", username);
            data.put("name", "张三");
            data.put("idcard", "433126203011220019");

            String dataStr = createLinkString(filter(data));
            String sign = Md5.encrypt(dataStr, key, "UTF-8");
            data.put("sign", sign);

            String rsp = post(uri, data);
            System.out.println(rsp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void heart() {
        try {
            Map<String, String> data = new HashMap<>();
            data.put("method", "ping.req");
            data.put("v", "1.0.0");
            data.put("username", username);

            String dataStr = createLinkString(filter(data));
            String sign = Md5.encrypt(dataStr, key, "UTF-8");
            data.put("sign", sign);

            String rsp = post(uri, data);
            System.out.println(rsp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static String post(String url, Map<String, String> paramsMap) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        String responseText = "succeed";
        CloseableHttpResponse response = null;
        try {
            HttpPost method = new HttpPost(url);
            method.setHeader("User-Agent", USER_AGENT);
            if (paramsMap != null) {
                List<NameValuePair> paramList = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> param : paramsMap.entrySet()) {
                    NameValuePair pair = new BasicNameValuePair(param.getKey(), param.getValue());
                    paramList.add(pair);
                }
                method.setEntity(new UrlEncodedFormEntity(paramList, "UTF-8"));
            }
            method.setHeader("appid", appid);
            method.setHeader("token", token);
            method.setHeader("cid", cid);

            response = client.execute(method);
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                responseText = EntityUtils.toString(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                response.close();
            } catch (Exception e) {
                throw e;
            }
        }
        return responseText;
    }

    public static String createLinkString(Map<String, String> params) {
        // 第一步：把字典按Key的字母顺序排序,参数使用TreeMap已经完成排序
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        // 第二步：把所有参数名和参数值串在一起
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            String value = params.get(key);
            if (!StringUtils.isEmpty(value)) {
                sb.append(key).append("=").append(value);
            }
        }
        return sb.toString();
    }

    /**
     * 除去数组中的空值和签名参数
     * 为了兼容健康商城签名问题，过滤sign2参数
     *
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static Map<String, String> filter(Map<String, String> sArray) {
        Map<String, String> result = new HashMap<String, String>();
        if (sArray == null || sArray.size() <= 0) {
            return result;
        }

        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (StringUtils.isEmpty(value) || key.equalsIgnoreCase("sign")) {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }

}
