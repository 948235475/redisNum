package com.wf.model;

import com.wf.enums.ResultEnum;
import lombok.Data;

@Data
public class Result {

    private int code;

    private String message;

    private Object data;

    public Result() {
    }

    public Result(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Result(ResultEnum resultEnum) {
        this.code = resultEnum.getCode();
        this.message = resultEnum.getMsg();
    }

    public Result(int code) {
        this.code = code;
    }

    public Result(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result setEnum(ResultEnum resultEnum){
        this.code = resultEnum.getCode();
        this.message = resultEnum.getMsg();
        return this;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
