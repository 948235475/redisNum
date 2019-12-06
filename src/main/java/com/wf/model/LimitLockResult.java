package com.wf.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LimitLockResult {

    private int success;

    private int fail;

    private Object object;

    public LimitLockResult(int success, int fail) {
        this.success = success;
        this.fail = fail;
    }
}
