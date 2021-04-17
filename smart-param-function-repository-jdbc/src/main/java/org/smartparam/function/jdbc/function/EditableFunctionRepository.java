package org.smartparam.function.jdbc.function;

import org.smartparam.engine.core.function.Function;
import org.smartparam.function.jdbc.core.FunctionParam;

import java.util.List;

public interface EditableFunctionRepository {

    void createFunction(String name, List<FunctionParam> params, String body);

    Long updateFunction(String name, List<FunctionParam> params, String body);

    List<? extends Function> getFunctions();

    void deleteFunction(String name);
}
