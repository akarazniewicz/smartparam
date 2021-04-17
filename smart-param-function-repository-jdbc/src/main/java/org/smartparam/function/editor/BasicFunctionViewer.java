package org.smartparam.function.editor;

import org.smartparam.engine.core.ParamEngine;
import org.smartparam.engine.core.function.Function;
import org.smartparam.function.editor.store.FunctionRepositoryStore;
import org.smartparam.function.jdbc.function.EditableFunctionRepository;

import java.util.List;

public class BasicFunctionViewer implements FunctionViewer {

    private final ParamEngine engine;
    private final FunctionRepositoryStore<EditableFunctionRepository> repositories;

    public BasicFunctionViewer(ParamEngine engine) {
        this.engine = engine;
        this.repositories = new FunctionRepositoryStore<>(
                engine.runtimeConfiguration().getFunctionRepositories(),
                EditableFunctionRepository.class
        );
    }

    @Override
    public Function getFunction(String functionType, String functionName) {
        EditableFunctionRepository repository = repositories.get(functionType);
        return repository.getFunction(functionName);
    }

    @Override
    public List<? extends Function> getFunctions(String functionType) {
        EditableFunctionRepository repository = repositories.get(functionType);
        return repository.getFunctions();
    }
}
