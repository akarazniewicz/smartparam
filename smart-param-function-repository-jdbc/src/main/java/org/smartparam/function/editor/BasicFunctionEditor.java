package org.smartparam.function.editor;

import org.smartparam.engine.core.ParamEngine;
import org.smartparam.function.editor.store.FunctionRepositoryStore;
import org.smartparam.function.jdbc.core.FunctionParam;
import org.smartparam.function.jdbc.function.EditableFunctionRepository;

import java.util.List;

public class BasicFunctionEditor implements FunctionEditor {

    private final ParamEngine engine;
    private final FunctionRepositoryStore<EditableFunctionRepository> repositories;

    public BasicFunctionEditor(ParamEngine engine) {
        this.engine = engine;
        this.repositories = new FunctionRepositoryStore<>(
                engine.runtimeConfiguration().getFunctionRepositories(),
                EditableFunctionRepository.class
        );
    }

    @Override
    public Long createFunction(String functionType, String name, List<FunctionParam> params, String body) {
        EditableFunctionRepository repository = repositories.get(functionType);
        return repository.createFunction(name, params, body);
    }

    @Override
    public Long updateFunction(String functionType, String name, List<FunctionParam> params, String body) {
        EditableFunctionRepository repository = repositories.get(functionType);
        return repository.updateFunction(name, params, body);
    }

    @Override
    public void deleteFunction(String functionType, String name) {
        EditableFunctionRepository repository = repositories.get(functionType);
        repository.deleteFunction(name);
    }
}
