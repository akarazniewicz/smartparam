package org.smartparam.function.editor;

import org.smartparam.function.jdbc.core.FunctionParam;

import java.util.List;

public interface FunctionEditor {

    Long createFunction(String functionType, String name, List<FunctionParam> params, String body);

    Long updateFunction(String functionType, String name, List<FunctionParam> params, String body);

    void deleteFunction(String functionType, String name);
}
