/*
 * Copyright 2013 Adam Dubiel, Przemek Hertel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartparam.engine.config;

import org.smartparam.engine.config.initialization.ComponentInitializer;
import org.smartparam.engine.config.initialization.ComponentInitializerRunner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartparam.engine.annotated.RepositoryObjectKey;
import org.smartparam.engine.config.pico.ComponentConfig;
import org.smartparam.engine.core.cache.MapFunctionCache;
import org.smartparam.engine.core.cache.MapParamCache;
import org.smartparam.engine.core.prepared.BasicLevelPreparer;
import org.smartparam.engine.core.prepared.BasicParamPreparer;
import org.smartparam.engine.core.matcher.Matcher;
import org.smartparam.engine.core.function.FunctionInvoker;
import org.smartparam.engine.core.function.FunctionRepository;
import org.smartparam.engine.core.parameter.ParamRepository;
import org.smartparam.engine.annotated.repository.ScanningInvokerRepository;
import org.smartparam.engine.annotated.repository.ScanningMatcherRepository;
import org.smartparam.engine.annotated.repository.ScanningTypeRepository;
import org.smartparam.engine.core.function.BasicFunctionManager;
import org.smartparam.engine.annotated.repository.ScanningFunctionProvider;
import org.smartparam.engine.core.parameter.BasicParameterProvider;
import org.smartparam.engine.core.type.Type;

/**
 *
 * @author Adam Dubiel
 */
public class ParamEngineConfig extends ComponentConfig {

    private List<ParamRepository> parameterRepositories = new ArrayList<ParamRepository>();

    private Map<RepositoryObjectKey, FunctionRepository> functionRepositories = new HashMap<RepositoryObjectKey, FunctionRepository>();

    private Map<String, FunctionInvoker> functionInvokers = new HashMap<String, FunctionInvoker>();

    private Map<String, Type<?>> types = new HashMap<String, Type<?>>();

    private Map<String, Matcher> matchers = new HashMap<String, Matcher>();

    private ComponentInitializerRunner initializationRunner;

    private List<ComponentInitializer> componentInitializers = new ArrayList<ComponentInitializer>();

    @Override
    protected void injectDefaults(List<Object> components) {
        components.add(BasicParamPreparer.class);
        components.add(BasicLevelPreparer.class);
        components.add(MapParamCache.class);
        components.add(BasicFunctionManager.class);
        components.add(ScanningFunctionProvider.class);
        components.add(MapFunctionCache.class);
        components.add(ScanningInvokerRepository.class);
        components.add(BasicParameterProvider.class);
        components.add(ScanningTypeRepository.class);
        components.add(ScanningMatcherRepository.class);
    }

    public List<ParamRepository> getParameterRepositories() {
        return parameterRepositories;
    }

    public void setParameterRepositories(List<ParamRepository> parameterRepositories) {
        this.parameterRepositories = parameterRepositories;
    }

    public Map<RepositoryObjectKey, FunctionRepository> getFunctionRepositories() {
        return functionRepositories;
    }

    public void setFunctionRepositories(Map<RepositoryObjectKey, FunctionRepository> functionRepositories) {
        this.functionRepositories = functionRepositories;
    }

    public Map<String, FunctionInvoker> getFunctionInvokers() {
        return functionInvokers;
    }

    public void setFunctionInvokers(Map<String, FunctionInvoker> functionInvokers) {
        this.functionInvokers = functionInvokers;
    }

    public Map<String, Type<?>> getTypes() {
        return types;
    }

    public void setTypes(Map<String, Type<?>> types) {
        this.types = types;
    }

    public Map<String, Matcher> getMatchers() {
        return matchers;
    }

    public void setMatchers(Map<String, Matcher> matchers) {
        this.matchers = matchers;
    }

    public List<ComponentInitializer> getComponentInitializers() {
        return componentInitializers;
    }

    public void setComponentInitializers(List<ComponentInitializer> componentInitializers) {
        this.componentInitializers = componentInitializers;
    }

    public ComponentInitializerRunner getInitializationRunner() {
        return initializationRunner;
    }

    public void setInitializationRunner(ComponentInitializerRunner initializationRunner) {
        this.initializationRunner = initializationRunner;
    }
}
