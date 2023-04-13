package io.game.bootstrap;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

@Getter
@Setter
@Builder
public class ApiMethod {

    /**
     * 入参类型
     */
    private Class<?> cls = null;

    /**
     * 方法
     */
    private Method method;

    /**
     * 类
     */
    private Object target;

    /**
     * 指定返回指令码
     */
    private int ack;
}
