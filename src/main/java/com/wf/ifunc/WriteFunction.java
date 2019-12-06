package com.wf.ifunc;

import com.wf.model.Result;

@FunctionalInterface
public interface WriteFunction<WriteFunctionArguments> {

    Result apply(WriteFunctionArguments writeFunctionArguments);
}
