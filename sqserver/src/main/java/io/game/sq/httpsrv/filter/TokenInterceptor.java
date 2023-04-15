package io.game.sq.httpsrv.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

@Order(1)
@Component("tokenInterceptor")
public class TokenInterceptor implements Interceptor {

    @Override
    public boolean validate(Map<String, String> params, Map<String, String> headers) {



        return true;
    }
}
