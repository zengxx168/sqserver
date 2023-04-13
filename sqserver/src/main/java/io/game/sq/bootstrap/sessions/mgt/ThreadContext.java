/**
 * Copyright (c) 2011-2021 All Rights Reserved.
 */
package io.game.sq.bootstrap.sessions.mgt;

import io.game.sq.bootstrap.sessions.domain.Session;
import io.netty.channel.ChannelHandlerContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @version $Id: ThreadContext.java 2021年7月1日 下午6:02:58 $
 */
public class ThreadContext {
    public static final String SUBJECT_KEY = ThreadContext.class.getName() + "_SUBJECT_KEY";
    public static final String SUBJECT_CTX = ThreadContext.class.getName() + "_SUBJECT_CTX";
    private static final ThreadLocal<Map<Object, Object>> resources = new InheritableThreadLocalMap<Map<Object, Object>>();

    public static Map<Object, Object> getResources() {
        if (resources.get() == null) {
            return Collections.emptyMap();
        }
        return new HashMap<Object, Object>(resources.get());
    }

    public static void put(Object key, Object value) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        if (value == null) {
            remove(key);
            return;
        }
        ensureResourcesInitialized();
        resources.get().put(key, value);
    }

    public static Object get(Object key) {
        Map<Object, Object> perThreadResources = resources.get();
        return perThreadResources != null ? perThreadResources.get(key) : null;
    }

    private static void ensureResourcesInitialized() {
        if (resources.get() == null) {
            resources.set(new HashMap<Object, Object>());
        }
    }

    public static Object remove(Object key) {
        Map<Object, Object> perThreadResources = resources.get();
        return perThreadResources != null ? perThreadResources.remove(key) : null;
    }

    public static void remove() {
        resources.remove();
    }

    public static Session getSession() {
        return (Session) get(SUBJECT_KEY);
    }

    public static ChannelHandlerContext getCtx() {
        return (ChannelHandlerContext) get(SUBJECT_CTX);
    }

    public static void bind(Session session) {
        remove();
        put(SUBJECT_KEY, session);
    }

    public static void ctx(ChannelHandlerContext ctx) {
        put(SUBJECT_CTX, ctx);
    }

    private static final class InheritableThreadLocalMap<T extends Map<Object, Object>>
            extends InheritableThreadLocal<Map<Object, Object>> {
        @Override
        @SuppressWarnings({"unchecked"})
        protected Map<Object, Object> childValue(Map<Object, Object> parentValue) {
            if (parentValue != null) {
                return (Map<Object, Object>) ((HashMap<Object, Object>) parentValue).clone();
            }
            return null;
        }
    }
}
