package org.smartparam.function.editor.store;

import org.smartparam.engine.core.function.FunctionRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FunctionRepositoryStore<T extends FunctionRepository> {

    private final Map<String, T> storedRepositories = new LinkedHashMap<>();

    public FunctionRepositoryStore(Map<String, FunctionRepository> allRepositories, Class<T> storedClass) {
        filterOutMatchingRepositories(allRepositories, storedClass);
    }

    @SuppressWarnings("unchecked")
    private void filterOutMatchingRepositories(Map<String, FunctionRepository> allRepositories, Class<T> storedClass) {
        FunctionRepository repository;
        for (Map.Entry<String, FunctionRepository> functionRepository : allRepositories.entrySet()) {
            repository = functionRepository.getValue();
            if (storedClass.isAssignableFrom(repository.getClass())) {
                storedRepositories.put(functionRepository.getKey(), (T) functionRepository.getValue());
            }
        }
    }

    public T get(String name) {
        T repository = storedRepositories.get(name);
        if (repository == null) {
            throw new InvalidFunctionRepositorySourceException(name);
        }
        return repository;
    }

    public List<? extends FunctionRepository> storedRepositories() {
        return Collections.unmodifiableList(new ArrayList<FunctionRepository>(storedRepositories.values()));
    }
}
