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
package org.smartparam.engine.test.builder;

import org.smartparam.engine.model.editable.EditableLevel;
import org.smartparam.engine.model.editable.SimpleEditableLevel;

/**
 *
 * @author Adam Dubiel
 */
public class LevelTestBuilder extends AbstractLevelTestBuilder<EditableLevel, LevelTestBuilder> {

    private LevelTestBuilder() {
        super(new SimpleEditableLevel());
    }

    public static LevelTestBuilder level() {
        return new LevelTestBuilder();
    }

    @Override
    protected LevelTestBuilder self() {
        return this;
    }
}