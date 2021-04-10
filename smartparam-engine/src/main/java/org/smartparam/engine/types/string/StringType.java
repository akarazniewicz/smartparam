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
package org.smartparam.engine.types.string;

import org.smartparam.engine.annotated.annotations.ParamType;
import org.smartparam.engine.core.type.Type;

/**
 * Klasa definiuje typ stringowy, ktory moze zostac wlaczony
 * do systemu typow rozpoznawanych przez silnik.
 * <p>
 * Typ stringowy przechowuje wszystkie wartosci w obiekcie {@link StringHolder}.
 *
 * @author Przemek Hertel
 * @since 1.0.0
 */
@ParamType(StringType.TYPE_NAME)
public class StringType implements Type<StringHolder> {

    public static final String TYPE_NAME = "string";

    /**
     * Zamienia obiekt holdera na String.
     *
     * @param holder obiekt holdera
     * @return wartosc holdera podana wprost
     */
    @Override
    public String encode(StringHolder holder) {
        return holder.getValue();
    }

    /**
     * Zamienia string na obiekt holdera.
     *
     * @param text string
     * @return obiekt holdera
     */
    @Override
    public StringHolder decode(String text) {
        return new StringHolder(text);
    }

    /**
     * Konwertuje dowolny obiekt (np. zwrocony przez funkcje) na obiekt holdera.
     * Wartosc stringowa holdera bedzie rowna wynikowi metody toString obiektu.
     * Argument rowny null zostanie zamieniony na StringHolder reprezentujacy null.
     * <p>
     * Na przyklad:
     * <pre>
     *   convert( new Integer(17)  );        // StringHolder.getValue() : "17"
     *   convert( new Float(1.0/3) );        // StringHolder.getValue() : "0.33333334"
     *   convert( null );                    // StringHolder.getValue() : null
     *   convert( null );                    // StringHolder.isNull()   : true
     * </pre>
     *
     * @param obj dowolny obiekt java lub null
     * @return obiekt holdera
     */
    @Override
    public StringHolder convert(Object obj) {
        return new StringHolder(obj != null ? obj.toString() : null);
    }

    @Override
    public StringHolder[] newArray(int size) {
        return new StringHolder[size];
    }
}
