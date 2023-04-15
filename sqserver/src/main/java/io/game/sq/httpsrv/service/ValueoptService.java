package io.game.sq.httpsrv.service;

import io.game.sq.commons.TtyException;
import lombok.extern.slf4j.Slf4j;
import org.jctools.maps.NonBlockingHashMap;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 缓存操作服务
 *
 * @author Admin
 * @version $Id: IValueOperationService.java 2015年8月21日 下午8:44:29 $
 */
@Slf4j
@Component("valueoptService")
public class ValueoptService {

    private Map<String, Object> valueops = new NonBlockingHashMap<>();

    public <T> void set(String key, T t) throws TtyException {
        set(key, t, 30, TimeUnit.MINUTES);
    }

    public <T> void set(String key, T t, long timeout, TimeUnit unit) throws TtyException {
        try {
            valueops.put(key, t);
            log.debug("add：key:{}", key);
        } catch (Exception e) {
            log.error("添加值失败", e);
        }
    }

    public <T> T get(String key) throws TtyException {
        try {
            return (T) valueops.get(key);
        } catch (Exception e) {
            log.error("获取值失败", e);
        }
        return null;
    }

    @Async
    public void delete(String key) throws TtyException {
        try {
            valueops.remove(key);
        } catch (Exception e) {
            log.error("删除值失败", e);
        }
    }
}
