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
package org.smartparam.engine.core.parameter.entry;

/**
 * Klasa reprezentuje pojedynczy wiersz parametru {@link org.smartparam.engine.core.parameter.Parameter}.
 * Kazdy parametr moze zawierac dowolnie wiele takich wierszy.
 * <p>
 *
 * Kazdy wiersz parametru (ParameterEntry) zawiera:
 * <ul>
 * <li> <b>wzorzec dopasowania</b> (levels) - wartosci/wzorce dla poszczegolnych poziomow
 * <li> <b>wartosc wiersza</b> (value) - wartosc zwracana jako wartosc parametru, jesli ten wiersz zostanie wybrany
 * <li> <b>funkcja z repozytorium</b> (function) - funkcja, ktorej wynik jest zwracany, jesli value jest rowne null
 * </ul>
 *
 * Wzorzec dopasowania, czyli tablica String[] levels to dynamiczna tablica,
 * ktora jest niejawnie rozszerzana w setterach, jesli nastapi odwolanie do nieistniejacego indeksu.
 * <p>
 *
 * ParameterEntry moze przechowywac wartosci dla dowolnie wiellu poziomow,
 * ale persystentne jest tylko 8 pol: od getLevel1() do getLevel8().
 * Jesli poziomow jest wiecej niz 8, wartosc getLevel8 zawiera poziom osmy i kolejne
 * skonkatenowane znakiem srednika (";").
 *
 * @author Przemek Hertel
 * @author Adam Dubiel
 * @since 0.1.0
 */
public interface ParameterEntry {

    /**
     * Optional repository-scope unique identifier of this entry that might be
     * used in audits to pinpoint exact entry that was used to deliver value.
     */
    ParameterEntryKey getKey();

    /**
     * Get all level patterns for this row.
     * Both input and output levels.
     *
     * @return levels row values
     */
    String[] getLevels();
}
