package io.game.httpsrv.signtype;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * MD5加密算法
 *
 * @author Admin
 * @version $Id: Md5Utils.java 2014年9月3日 下午4:01:08 $
 */
@Slf4j
public class Md5 {
    private static final String SIGN_TYPE = "MD5";
    private static final String CHARSET_NAME = "UTF-8";
    private static final String salt = "lhMPbfaevwAEfQt9OuaOYmfM7RvOZi2p";

    /**
     * MD5加密
     *
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String encrypt(byte[] data) {
        try {
            MessageDigest md5 = MessageDigest.getInstance(SIGN_TYPE);
            md5.update(data);
            return byte2hex(md5.digest());
        } catch (NoSuchAlgorithmException e) {
            log.debug("md5 加密异常", e);
        }
        return "";
    }

    /**
     * MD5加密
     *
     * @param str
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String encrypt(String str) {
        try {
            MessageDigest md5 = MessageDigest.getInstance(SIGN_TYPE);
            md5.update((str + salt).getBytes(CHARSET_NAME));
            return byte2hex(md5.digest());
        } catch (Exception e) {
            log.debug("md5 加密异常", e);
        }
        return null;
    }

    /**
     * MD5加盐加密
     *
     * @param str
     * @param salt
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String encrypt(String str, String salt) {
        try {
            MessageDigest md5 = MessageDigest.getInstance(SIGN_TYPE);
            md5.update((str + salt).getBytes(CHARSET_NAME));
            return byte2hex(md5.digest());
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("md5 加密异常", e);
            }
        }
        return "";
    }

    public static String encrypt(String str, String salt, String charset) {
        try {
            MessageDigest md5 = MessageDigest.getInstance(SIGN_TYPE);
            md5.update((str + salt).getBytes(charset));
            return byte2hex(md5.digest());
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("md5 加密异常", e);
            }
        }
        return "";
    }

    public static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }

        return sign.toString();
    }

    public static byte[] hex2byte(String str) {
        if (str == null) {
            return null;
        }
        str = str.trim();
        int len = str.length();
        if (len <= 0 || len % 2 == 1) {
            return null;
        }
        byte[] b = new byte[len / 2];
        try {
            for (int i = 0; i < str.length(); i += 2) {
                b[(i / 2)] = (byte) Integer.decode("0x" + str.substring(i, i + 2)).intValue();
            }
            return b;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 给TOP请求做MD5签名。
     *
     * @param sortedParams 所有字符型的TOP请求参数
     * @param secret       签名密钥
     * @return 签名
     * @throws IOException
     */
    public static String signRequestNew(Map<String, String> sortedParams, String secret) throws IOException {
        // 第一步：把字典按Key的字母顺序排序
        List<String> keys = new ArrayList<String>(sortedParams.keySet());
        Collections.sort(keys);

        // 第二步：把所有参数名和参数值串在一起
        StringBuilder query = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = sortedParams.get(key);
            if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(value) && !"sign".equals(key)) {
                query.append(key).append("=").append(value);
            }
        }
        log.info("获取当APP请求参数，签名前值为：" + query.toString());
        return Md5.encrypt(query.toString(), secret);
    }

    public static String signRequest(Map<String, String> sortedParams, String secret) throws IOException {
        // 第一步：把字典按Key的字母顺序排序
        List<String> keys = new ArrayList<String>(sortedParams.keySet());
        Collections.sort(keys);

        // 第二步：把所有参数名和参数值串在一起
        StringBuilder query = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = sortedParams.get(key);
            if (!StringUtils.isEmpty(key) && null != value
                    && !"".equals(value) && !"sign".equals(key)) {
                query.append(key).append("=").append(value);
            }
        }
        log.info("获取当APP请求参数，签名前值为：" + query.toString());
        return Md5.encrypt(query.toString(), secret);
    }
}
