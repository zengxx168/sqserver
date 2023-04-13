package io.game.bootstrap.serializer;

import com.google.protobuf.GeneratedMessageV3;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;

@Slf4j
@UtilityClass
public class DataCodecKit2 {

    /**
     * 字节码转对象
     *
     * @param <T>
     * @param data
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T decode(byte[] data, Class<T> cls) throws Exception {
        if (null == data || data.length < 0) {
            return null == cls ? null : cls.getDeclaredConstructor().newInstance();
        }
        if (cls.getSuperclass().equals(GeneratedMessageV3.class)) {
            try {
                Method method = cls.getMethod("parseFrom", byte[].class);
                return (T) method.invoke(cls, data);
            } catch (Exception e) {
                log.error("数据转换异常", e);
            }
        }
        return cls.getDeclaredConstructor().newInstance();
    }

    @SuppressWarnings("rawtypes")
    public static byte[] encode(Object data) {
        if (null != data && data instanceof GeneratedMessageV3.Builder) {
            try {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                GeneratedMessageV3.Builder builder = (GeneratedMessageV3.Builder) data;
                builder.build().writeTo(bout);
                return bout.toByteArray();
            } catch (Throwable e) {
                log.error("数据转换异常", e);
            }
        }
        return DataCodecKit.encode(data);
    }

}
