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
package org.smartparam.engine.types.bool;

import org.smartparam.engine.annotated.annotations.ParamType;
import org.smartparam.engine.core.type.Type;
import org.smartparam.engine.util.EngineUtil;

/**
 * Klasa definiuje typ logiczny, ktory moze zostac wlaczony
 * do systemu typow rozpoznawanych przez silnik.
 * <p>
 * Typ ten przechowuje wartosci logiczne w obiekcie {@link BooleanHolder},
 * ktory moze reprezentowac:
 * <ul>
 * <li>wartosc logiczna false
 * <li>wartosc logiczna true
 * <li>wartosc null
 * </ul>
 *
 * @author Przemek Hertel
 * @since 0.2.0
 */
@ParamType(BooleanType.TYPE_NAME)
public class BooleanType implements Type<BooleanHolder> {

    public static final String TYPE_NAME = "boolean";

    public String encode(BooleanHolder holder) {
        return String.valueOf(holder.getBoolean());
    }

    public BooleanHolder decode(String text) {
        Boolean value = EngineUtil.hasText(text) ? parse(text) : null;
        return new BooleanHolder(value);
    }

    public BooleanHolder convert(Object obj) {

        if (obj instanceof Boolean) {
            return new BooleanHolder((Boolean) obj);
        }

        if (obj instanceof String) {
            return decode((String) obj);
        }

        if (obj == null) {
            return new BooleanHolder(null);
        }

        throw new IllegalArgumentException("conversion not supported for: " + obj.getClass());
    }

    public BooleanHolder[] newArray(int size) {
        return new BooleanHolder[size];
    }

    private Boolean parse(String text) {
        return Boolean.valueOf(text);
    }

    //TODO #ph: finish boolean: 1) convert 2) parse 3) attributes

    /*
     * attr             default
     * formatTrue       "true"  - literaly uzywane przez encode
     * formatFalse      "false"
     *
     * parseTrue        true, t, yes, y, 1
     * parseFalse       false, f, no, n, 0
     * ignoreCase       true
     * parseUnknownAsEx true
     * parseUnknownAs   false (:boolean)
     */

}
