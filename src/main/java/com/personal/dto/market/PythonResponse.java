package com.personal.dto.market;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Python服务响应包装类
 * Python服务返回格式: {"code":200,"message":"success","data":[...]}
 */
@Data
public class PythonResponse<T> {
    private Integer code;

    private String message;

    @JsonProperty("data")
    private T data;
}
