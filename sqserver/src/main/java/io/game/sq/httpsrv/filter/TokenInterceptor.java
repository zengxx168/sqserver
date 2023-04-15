package io.game.sq.httpsrv.filter;

import io.game.sq.commons.Constants;
import io.game.sq.httpsrv.service.ValueoptService;
import io.game.sq.httpsrv.signtype.DES;
import io.netty.handler.codec.http.HttpHeaders;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Order(1)
@Component("tokenInterceptor")
public class TokenInterceptor implements Interceptor {
    private final static String TOKEN = "token";
    private final static String CID = "cid";
    private final static String USER_NAME = "username";

    @Resource
    private ValueoptService valueoptService;

    @Override
    public boolean validate(Map<String, String> params, HttpHeaders headers) {
        String token = headers.get(TOKEN);
        String username = params.get(USER_NAME);
        if (StringUtils.isEmpty(token) || StringUtils.isEmpty(username)) {
            return false;
        }
        String cid = headers.get(CID);
        if (!this.validate(username, cid, token)) {
            return false;
        }
        String tk = valueoptService.get(String.format(Constants.TOKEN, username));
        log.info("获取token值: 登录账号{}, 上传的token: {}, 缓存: {}", username, token, tk);
        if (!StringUtils.equals(token, tk) && StringUtils.indexOf(tk, token) == -1) {
            return false;
        }
        return true;
    }

    /**
     * 校验token
     * @param uid
     * @param token
     * @return
     */
    public boolean validate(String uid, String cid, String token) {
        String id = Tokenor.getUserId(cid, token);
        if (StringUtils.equals(id, uid)) {
            return true;
        }
        return false;
    }

    /**
     * 创建token
     * @param clientId
     * @param uid
     * @return
     */
    public String generateToken(String clientId, String uid) {
        return Tokenor.getToken(clientId, uid);
    }
}

@Slf4j
class Tokenor {
    private static String KEY = "PyLAmF0yJAW2BEWK";
    private static long expiredTime = 7 * 24 * 60 *60 *1000;

    public static void setKey(String key) {
        if (!StringUtils.isEmpty(key)) {
            KEY = key;
        }
    }

    public static void setExpiredTime(long expiredTime) {
        Tokenor.expiredTime = expiredTime;
    }

    public static String getUserId(String cid, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        try {
            String signKey = DES.decryptDES(token);
            if (signKey.indexOf(cid) == -1) {
                return null;
            }

            if (signKey.startsWith(KEY + "|")) {
                signKey = signKey.substring(KEY.length() + 1);
                long timestamp = Long.parseLong(signKey.substring(0, signKey.indexOf('|')));
                if (System.currentTimeMillis() - timestamp > expiredTime) {
                    return null;
                }
                return signKey.substring(signKey.lastIndexOf('|') + 1);
            }
        } catch (Exception e) {
            log.error("解密出错", e);
        }
        return null;
    }

    public static String getToken(String clientId, String uid) {
        String signKey = KEY + "|" + (System.currentTimeMillis()) + "|" + clientId + "|" + uid;
        try {
            return DES.encryptDES(signKey);
        } catch (Exception e) {
            log.error("解密出错", e);
        }
        return null;
    }
}
