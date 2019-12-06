package com.wf.ifunc;

@FunctionalInterface
public interface CountFunction<CountFunctionArguments> {

    int apply(CountFunctionArguments countFunctionArguments);
}
