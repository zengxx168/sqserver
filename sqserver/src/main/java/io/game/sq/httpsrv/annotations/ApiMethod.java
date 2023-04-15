package io.game.sq.httpsrv.annotations;

import java.lang.annotation.*;

/**
 * 使用该注解对服务方法进行标注。
 *
 * @author Admin
 * @version $Id: ApiMethod.java 2015年4月5日 下午5:31:31 $
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiMethod {

    /**
     * 服务方法需要需求会话检查，默认要检查
     *
     * @return
     */
    TokenType value() default TokenType.DEFAULT;

}
