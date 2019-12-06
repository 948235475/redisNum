package com.wf.ifunc;

@FunctionalInterface
public interface LimitFunction<LimitFunctionArguments> {

    int apply(LimitFunctionArguments limitFunctionArguments);
}
