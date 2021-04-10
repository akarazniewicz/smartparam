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
package org.smartparam.engine.matchers;

import org.smartparam.engine.annotated.annotations.ParamMatcher;
import org.smartparam.engine.annotated.annotations.ObjectInstance;
import org.smartparam.engine.core.index.Star;
import org.smartparam.engine.core.matcher.Matcher;
import org.smartparam.engine.core.type.ValueHolder;
import org.smartparam.engine.core.type.Type;
import org.smartparam.engine.util.EngineUtil;

/**
 * Range matcher, checks if value fits in range defined in pattern. Value type
 * and pattern type must also match. It is possible to define range inclusiveness
 * or exclusiveness, separately for each of range border value.
 *
 * Between matcher has a set of default separators, that will be used to
 * separate values for beginning and end of range. These separators are (order matters):
 * <pre>
 * ~ : - ,
 * </pre>
 *
 * @author Przemek Hertel
 * @since 0.9.0
 */
@ParamMatcher(value = "", instances = {
    @ObjectInstance(value = BetweenMatcher.BETWEEN_IE, constructorArgs = {"true", "false"}),
    @ObjectInstance(value = BetweenMatcher.BETWEEN_EI, constructorArgs = {"false", "true"}),
    @ObjectInstance(value = BetweenMatcher.BETWEEN_II, constructorArgs = {"true", "true"}),
    @ObjectInstance(value = BetweenMatcher.BETWEEN_EE, constructorArgs = {"false", "false"})
})
public class BetweenMatcher implements Matcher {

    private static final char[] DEFAULT_SEPARATORS = {'~', ':', '-', ','};

    public static final String BETWEEN_IE = "between/ie";

    public static final String BETWEEN_EI = "between/ei";

    public static final String BETWEEN_II = "between/ii";

    public static final String BETWEEN_EE = "between/ee";

    private boolean lowerInclusive = true;

    private boolean upperInclusive = false;

    private char[] separators = DEFAULT_SEPARATORS;

    public BetweenMatcher() {
    }

    public BetweenMatcher(String lowerInclusive, String upperInclusive) {
        this.lowerInclusive = Boolean.parseBoolean(lowerInclusive);
        this.upperInclusive = Boolean.parseBoolean(upperInclusive);
    }

    public BetweenMatcher(String lowerInclusive, String upperInclusive, String separators) {
        this(Boolean.parseBoolean(lowerInclusive), Boolean.parseBoolean(upperInclusive), separators);
    }

    /**
     * @param lowerInclusive range lower end should be inclusive?
     * @param upperInclusive range upper end should be inclusive?
     * @param separators     separators to use
     */
    public BetweenMatcher(boolean lowerInclusive, boolean upperInclusive, String separators) {
        this.lowerInclusive = lowerInclusive;
        this.upperInclusive = upperInclusive;
        if (separators != null) {
            this.separators = separators.toCharArray();
        }
    }

    @Override
    public <T extends ValueHolder> boolean matches(String value, String pattern, Type<T> type) {
        char separator = findSeparator(pattern);

        String[] tokens = EngineUtil.split2(pattern, separator);
        String lower = tokens[0].trim();
        String upper = tokens[1].trim();

        T v = type.decode(value);

        return lowerCondition(v, lower, type) && upperCondition(v, upper, type);
    }

    private char findSeparator(String pattern) {
        for (char ch : separators) {
            if (pattern.indexOf(ch) >= 0) {
                return ch;
            }
        }
        return DEFAULT_SEPARATORS[0];
    }

    private <T extends ValueHolder> boolean lowerCondition(T v, String lower, Type<T> type) {
        if (Star.SYMBOL.equals(lower) || "".equals(lower)) {
            return true;
        }

        T l = type.decode(lower);

        return lowerInclusive ? l.compareTo(v) <= 0 : l.compareTo(v) < 0;
    }

    private <T extends ValueHolder> boolean upperCondition(T v, String upper, Type<T> type) {
        if (Star.SYMBOL.equals(upper) || "".equals(upper)) {
            return true;
        }

        T u = type.decode(upper);

        return upperInclusive ? v.compareTo(u) <= 0 : v.compareTo(u) < 0;
    }

    public char[] separators() {
        return separators;
    }
}
