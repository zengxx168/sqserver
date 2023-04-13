/*
 * Copyright (C) 2021 - 2023 . All Rights Reserved.
 */
package io.game.sq.bootstrap.protocols;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * 消息发送指令
 */
@Data
@ProtobufClass
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Command {

    /**
     * 请求ID
     */
    @Protobuf(fieldType = FieldType.INT32, order = 1)
    private int id = 1;

    /**
     * 请求指令
     */
    @Protobuf(fieldType = FieldType.INT32, order = 2)
    private int cmd;

    /**
     * 结果编码：0成功，非0失败
     */
    @Protobuf(fieldType = FieldType.INT32, order = 3)
    private int code;

    /**
     * 报文体
     */
    @Protobuf(fieldType = FieldType.BYTES, order = 4)
    private byte[] data;

    /**
     * 业务数据
     * @param data 业务数据
     */
    public void setData(byte[] data) {
        if (data != null) {
            this.data = data;
        }
    }
}
