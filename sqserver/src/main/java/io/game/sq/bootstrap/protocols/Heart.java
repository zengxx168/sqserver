package io.game.sq.bootstrap.protocols;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@ProtobufClass
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Heart {

    /**
     * 时间戳
     */
    @Protobuf(fieldType = FieldType.INT32, order = 1)
    private int t = 1;
}
