package com.wf.enums;


public enum ResultEnum {

    FAILUER(1,"失败"),

    SUCCESS(2,""),

    RESOURCES_LIMIT(5,"超过上限"),

    SCREEN_LIMIT(500,"大屏数量超过上限"),

    /**
     * 没有写权限
     */
    HAS_NO_WRITE_REGHT(6,"系统错误"),
    /**
     * 没有用户写权限
     */
    HAS_NO_USER_WRITE_RIGHT(600,"系统错误"),
    /**
     * 没有读权限
     */
    HAS_NO_READ_REGHT(7,"系统错误"),
    /**
     * 没有用户读权限
     */
    HAS_NO_USER_READ_RIGHT(700,"系统错误"),
    /**
     * 位置错误
     */
    UNKNOW_ERROR(8,"未知错误"),
    /**
     * 发生异常
     */
    CATCHED_EXCEPTION(9,"系统错误"),
    /**
     * 限制数查询条件异常
     */
    EXCEPTION_ON_LIMIT(900,"系统错误"),
    /**
     * 计数查询条件异常
     */
    EXCEPTION_ON_COUNT(901,"系统错误"),
    /**
     * 通用所尚未初始化
     */
    IS_NOT_INITIAL(10,"系统错误");

    private Integer code;

    private String msg;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
