package io.game.httpsrv.filter;

import io.game.httpsrv.signtype.Md5;
import io.game.web.domain.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * 签名验证拦截器O
 *
 * @author Admin
 * @version $Id: AuthchkInterceptor.java 2015年8月16日 下午10:20:27 $
 */
@Slf4j
@Component
public class AuthInterceptor {
    public static String key = "nviyWPlk78Qh8ALo4jQh6MK2NukUf2YU";

    public boolean validate(Map<String, String> params) {
        // 签名不能为空
        ApiResponse rsp = new ApiResponse("500");
        String s = String.valueOf(params.get("sign"));
        if (!params.containsKey("sign") || StringUtils.isEmpty(s)) {
            return false;
        }

        /**
         * 签名验证
         */
        try {
            String sign = Md5.signRequestNew(params, key);
            log.info("服务器签名值: {}, 拦截器客户端: {}", sign, params.get("sign"));
            if (StringUtils.equals(s, sign)) {
                return true;
            }
        } catch (IOException e) {
            log.error("签名验证失败", e);
        }
        return false;
    }
}
