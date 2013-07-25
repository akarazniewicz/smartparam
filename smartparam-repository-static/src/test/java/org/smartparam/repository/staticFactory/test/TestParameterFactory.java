/*
 * Copyright 2013 the original author or authors.
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
package org.smartparam.repository.staticFactory.test;

import java.util.Arrays;
import java.util.List;
import org.smartparam.engine.model.Parameter;
import org.smartparam.engine.model.editable.EditableParameter;
import org.smartparam.engine.model.editable.SimpleEditableParameter;
import org.smartparam.repository.staticFactory.StaticParameterFactory;
import org.smartparam.repository.staticFactory.ScannableStaticParameterFactory;

/**
 *
 * @author Adam Dubiel <dubiel.adam@gmail.com>
 */
@ScannableStaticParameterFactory
public class TestParameterFactory implements StaticParameterFactory {

    @Override
    public List<Parameter> createParameters() {
        EditableParameter parameter = new SimpleEditableParameter();
        parameter.setName("testParameter");

        return Arrays.asList((Parameter) parameter);
    }
}
