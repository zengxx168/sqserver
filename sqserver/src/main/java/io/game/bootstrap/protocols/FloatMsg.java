/*
 * Copyright (C) 2021 - 2023 . All Rights Reserved.
 */
package io.game.bootstrap.protocols;

import com.baidu.bjf.remoting.protobuf.FieldType;
import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import edu.emory.mathcs.backport.java.util.Arrays;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Accessors(chain = true)
@ProtobufClass
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FloatMsg {

    /**
     * 默认提示语 (也可以根据错误码，使用本地的)
     */
    @Protobuf(fieldType = FieldType.STRING, order = 1)
    private String text;

    /**
     * 返回错误中参数
     */
    @Protobuf(fieldType = FieldType.STRING, order = 2)
    private List<String> args;

    public FloatMsg args(String[] args) {
        if (null != args && args.length >= 1) {
            this.args = Arrays.asList(args);
        }
        return this;
    }

}
