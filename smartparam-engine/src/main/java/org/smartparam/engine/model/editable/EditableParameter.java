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
package org.smartparam.engine.model.editable;

import java.util.List;
import java.util.Set;
import org.smartparam.engine.model.Level;
import org.smartparam.engine.model.Parameter;
import org.smartparam.engine.model.ParameterEntry;

/**
 *
 * @author Adam Dubiel
 */
public interface EditableParameter extends Parameter, IdentifiableParameter {

    void setName(String name);

    void setLevels(List<Level> levels);

    void setInputLevels(int inputLevels);

    void setEntries(Set<ParameterEntry> entries);

    void setCacheable(boolean cacheable);

    void setNullable(boolean nullable);

    void setArraySeparator(char arraySeparator);

}
