package io.game.web.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ApiResponse implements Serializable {
    private static final long serialVersionUID = 5446816884635257158L;

    private String code = "0";
    private String message;
    private Object data;

    public ApiResponse(String code) {
        this.code = code;
    }

    public ApiResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        if (isSuccess() || data instanceof Map || data instanceof List) {
            return data;
        }
        return null;
    }

    public boolean isSuccess() {
        return "0".equals(code);
    }

}
