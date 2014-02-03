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

package org.smartparam.engine.core.context;

import org.assertj.core.api.AbstractAssert;
import org.smartparam.engine.test.ParamEngineAssertions;

/**
 *
 * @author Adam Dubiel
 */
public class BaseParamContextAssert extends AbstractAssert<BaseParamContextAssert, BaseParamContext> {

    private BaseParamContextAssert(BaseParamContext actual) {
        super(actual, BaseParamContextAssert.class);
    }

    public static BaseParamContextAssert assertThat(BaseParamContext actual) {
        return new BaseParamContextAssert(actual);
    }

    public BaseParamContextAssert hasLevelValues(Object... levelValues) {
        ParamEngineAssertions.assertThat(actual.getLevelValues()).containsExactly(levelValues);
        return this;
    }
}