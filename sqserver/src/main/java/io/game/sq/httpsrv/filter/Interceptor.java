package io.game.sq.httpsrv.filter;

import java.util.Map;

public interface Interceptor {

    /**
     * 验证参数
     * @param params
     * @return
     */
    public default boolean validate(Map<String, String> params) {
        return this.validate(params, null);
    }

    /**
     * 验证参数
     * @param params
     * @param headers
     * @return
     */
    public default boolean validate(Map<String, String> params, Map<String, String> headers) {
        return false;
    }
}
