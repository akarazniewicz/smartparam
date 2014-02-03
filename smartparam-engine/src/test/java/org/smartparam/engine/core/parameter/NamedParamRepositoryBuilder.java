/*
 * Copyright 2014 Adam Dubiel, Przemek Hertel.
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
package org.smartparam.engine.core.parameter;

import org.smartparam.engine.core.repository.RepositoryName;

/**
 *
 * @author Adam Dubiel
 */
public class NamedParamRepositoryBuilder {

    private final ParamRepository repository;

    private RepositoryName name = RepositoryName.from("default-test-name");

    private NamedParamRepositoryBuilder(ParamRepository repository) {
        this.repository = repository;
    }

    public static NamedParamRepositoryBuilder namedRepository(ParamRepository repository) {
        return new NamedParamRepositoryBuilder(repository);
    }

    public NamedParamRepository build() {
        return new NamedParamRepository(name, repository);
    }

    public NamedParamRepositoryBuilder named(String name) {
        this.name = RepositoryName.from(name);
        return this;
    }
}
