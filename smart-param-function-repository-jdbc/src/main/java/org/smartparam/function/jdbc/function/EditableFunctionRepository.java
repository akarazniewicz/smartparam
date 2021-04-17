package org.smartparam.function.jdbc.function;

import org.smartparam.engine.core.function.Function;
import org.smartparam.engine.core.function.FunctionRepository;
import org.smartparam.function.jdbc.core.FunctionParam;

import java.util.List;

public interface EditableFunctionRepository extends FunctionRepository {

    Long createFunction(String name, List<FunctionParam> params, String body);

    Long updateFunction(String name, List<FunctionParam> params, String body);

    List<? extends Function> getFunctions();

    Function getFunction(String name);

    void deleteFunction(String name);
}
