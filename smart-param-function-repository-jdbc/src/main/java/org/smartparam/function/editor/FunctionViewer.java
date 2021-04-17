package org.smartparam.function.editor;

import org.smartparam.engine.core.function.Function;

import java.util.List;

public interface FunctionViewer {

    Function getFunction(String functionType, String functionName);

    List<? extends Function> getFunctions(String functionType);
}
