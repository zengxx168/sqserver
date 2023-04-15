package io.game.sq.httpsrv.filter;

import io.netty.handler.codec.http.HttpHeaders;

import java.util.Map;

public interface Interceptor {

    /**
     * 验证参数
     * @param params
     * @param headers
     * @return
     */
    public default boolean validate(Map<String, String> params, HttpHeaders headers) {
        return false;
    }
}
