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
package org.smartparam.engine.core;

import org.smartparam.engine.core.output.ParamValue;
import org.smartparam.engine.config.ParamEngineConfig;
import org.smartparam.engine.config.ParamEngineConfigBuilder;
import org.smartparam.engine.config.ParamEngineFactory;
import org.smartparam.engine.core.context.DefaultContext;
import org.smartparam.engine.core.context.LevelValues;
import org.smartparam.engine.core.function.FunctionInvoker;
import org.smartparam.engine.core.function.FunctionRepository;
import org.smartparam.engine.core.parameter.ParamRepository;
import org.smartparam.engine.matchers.BetweenMatcher;
import org.smartparam.engine.core.parameter.level.Level;
import org.smartparam.engine.core.parameter.Parameter;
import org.smartparam.engine.core.parameter.entry.ParameterEntry;
import org.smartparam.engine.core.function.Function;
import org.smartparam.engine.types.date.DateType;
import org.smartparam.engine.types.integer.IntegerType;
import org.smartparam.engine.types.string.StringType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import org.assertj.core.api.Assertions;
import org.smartparam.engine.core.output.DetailedParamValue;

import org.smartparam.engine.core.output.GettingKeyNotIdentifiableParameterException;
import org.smartparam.engine.core.output.GettingWrongTypeException;
import org.smartparam.engine.core.output.entry.MapEntry;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.mockito.Mockito.*;
import static org.smartparam.engine.core.parameter.ParameterTestBuilder.parameter;
import static org.smartparam.engine.test.ParamEngineAssertions.assertThat;
import static org.smartparam.engine.functions.java.JavaFunctionTestBuilder.javaFunction;
import static org.smartparam.engine.core.parameter.entry.ParameterEntryTestBuilder.parameterEntry;
import static org.smartparam.engine.core.parameter.level.LevelTestBuilder.level;

/**
 * @author Przemek Hertel
 */
public class ParamEngineIntegrationTest {

    private SmartParamEngine engine;

    private ParamRepository paramRepository;

    private FunctionRepository functionRepository;

    private FunctionInvoker functionInvoker;

    @BeforeMethod
    public void initialize() {
        paramRepository = mock(ParamRepository.class);
        functionRepository = mock(FunctionRepository.class);
        functionInvoker = mock(FunctionInvoker.class);

        ParamEngineConfig config;
        config = ParamEngineConfigBuilder.paramEngineConfig()
                .withType("string", new StringType())
                .withType("integer", new IntegerType())
                .withType("date", new DateType())
                .withParameterRepository("testRepository", paramRepository)
                .withFunctionRepository("java", 1, functionRepository)
                .withFunctionInvoker("java", functionInvoker)
                .withMatcher("between", new BetweenMatcher())
                .withAnnotationScanDisabled()
                .build();

        engine = (SmartParamEngine) ParamEngineFactory.paramEngine(config);
    }

    @Test
    public void shouldReturnValueOfParameterWithLevelValuesPassedExplicitly() {
        // given
        Level[] levels = new Level[]{
                level().withType("string").build(),
                level().withType("string").build(),
                level().withType("integer").build()
        };
        ParameterEntry[] entries = new ParameterEntry[]{
                parameterEntry().withLevels("A", "F", "11").build()
        };
        Parameter parameter = parameter().withLevels(levels).withEntries(entries).withInputLevels(2).build();
        when(paramRepository.load("parameter")).thenReturn(parameter);

        // when
        ParamValue value = engine.get("parameter", "A", "F");

        // then
        assertThat(value).hasValue(11l);
        assertThat(value.sourceRepository().value()).isEqualTo("testRepository");
    }

    @Test
    public void shouldThrowExceptionWhenTryingToGetParameterValueByContextWithoutLevelCreators() {
        // given
        Level[] levels = new Level[]{
                level().withType("string").build(),
                level().withType("integer").build()
        };
        ParameterEntry[] entries = new ParameterEntry[]{
                parameterEntry().withLevels("A", "42").build()
        };
        Parameter parameter = parameter().withLevels(levels).withEntries(entries).withInputLevels(2).build();
        when(paramRepository.load("parameter")).thenReturn(parameter);

        // when
        catchException(engine).get("parameter", new DefaultContext());

        // then
        assertThat((Exception) caughtException()).isInstanceOf(UndefinedLevelCreatorException.class);
    }

    @Test
    public void shouldThrowExceptionWhenNoValueFoundForNotNullableParameter() {
        // given
        Level[] levels = new Level[]{
                level().withType("string").build(),
                level().withType("integer").build()
        };
        ParameterEntry[] entries = new ParameterEntry[]{
                parameterEntry().withLevels("A", "42").build()
        };
        Parameter parameter = parameter().withLevels(levels).withEntries(entries).withInputLevels(1).build();
        when(paramRepository.load("parameter")).thenReturn(parameter);

        // when
        catchException(engine).get("parameter", "B");

        // then
        assertThat((Exception) caughtException()).isInstanceOf(ParameterValueNotFoundException.class);
    }

    @Test
    public void shouldReturnEmptyParamValueWhenNothingFoundAndParameterIsNullable() {
        // given
        Level[] levels = new Level[]{
                level().withType("string").build(),
                level().withType("string").build(),
                level().withType("integer").build()
        };
        ParameterEntry[] entries = new ParameterEntry[]{
                parameterEntry().withLevels("A", "F", "11").build(),
                parameterEntry().withLevels("B", "F", "21").build()
        };
        Parameter parameter = parameter().nullable().withLevels(levels).withEntries(entries).withInputLevels(2).build();
        when(paramRepository.load("parameter")).thenReturn(parameter);

        // when
        ParamValue value = engine.get("parameter", "A", "C");

        // then
        assertThat(value.isEmpty()).isTrue();
    }

    @Test
    public void shouldReturnDefaultValueWhenNoneOtherFound() {
        // given
        Level[] levels = new Level[]{
                level().withType("string").build(),
                level().withType("string").build(),
                level().withType("integer").build()
        };
        ParameterEntry[] entries = new ParameterEntry[]{
                parameterEntry().withLevels("A", "F", "11").build(),
                parameterEntry().withLevels("A", "*", "42").build()
        };
        Parameter parameter = parameter().withLevels(levels).withEntries(entries).withInputLevels(2).build();
        when(paramRepository.load("parameter")).thenReturn(parameter);

        // when
        ParamValue value = engine.get("parameter", "A", "C");

        // then
        assertThat(value).hasValue(42L);
    }

    @Test
    public void shouldPreferConcreteValueToDefaultValueWhenBothPossible() {
        // given
        Level[] levels = new Level[]{
                level().withType("string").build(),
                level().withType("string").build(),
                level().withType("integer").build()
        };
        ParameterEntry[] entries = new ParameterEntry[]{
                parameterEntry().withLevels("A", "F", "42").build(),
                parameterEntry().withLevels("A", "*", "11").build()
        };
        Parameter parameter = parameter().withLevels(levels).withEntries(entries).withInputLevels(2).build();
        when(paramRepository.load("parameter")).thenReturn(parameter);

        // when
        ParamValue value = engine.get("parameter", "A", "F");

        // then
        assertThat(value).hasValue(42L);
    }

    @Test
    public void shouldGetBackToTheRootIfStuckInDeadEndWhenSearchingForValue() {
        // given
        Level[] levels = new Level[]{
                level().withType("integer").withMatcher("between").build(),
                level().withType("string").build(),
                level().withType("string").build(),
                level().withType("integer").build()
        };
        ParameterEntry[] entries = new ParameterEntry[]{
                parameterEntry().withLevels("1-10", "B", "C", "11").build(),
                parameterEntry().withLevels("1-10", "B", "D", "12").build(),
                parameterEntry().withLevels("1-10", "B", "D", "13").build(),
                parameterEntry().withLevels("3-20", "B", "E", "42").build(),
                parameterEntry().withLevels("4-25", "B", "F", "14").build()
        };
        Parameter parameter = parameter().withLevels(levels).withEntries(entries).withInputLevels(3).build();
        when(paramRepository.load("parameter")).thenReturn(parameter);

        // when
        ParamValue value = engine.get("parameter", "6", "B", "E");

        // then
        assertThat(value).hasValue(42L);
    }

    @Test
    public void shouldReturnDetailedValuesThatContainWholeParamEntries() {
        // given
        Level[] levels = new Level[]{
                level().withName("level1").withType("string").build(),
                level().withName("level2").withType("string").build(),
                level().withName("level3").withType("string").build(),
                level().withName("output").withType("integer").build()
        };
        ParameterEntry[] entries = new ParameterEntry[]{
                parameterEntry().withLevels("A", "B", "C", "11").build(),
                parameterEntry().withLevels("C", "B", "D", "12").build(),};
        Parameter parameter = parameter().withLevels(levels).withEntries(entries).withInputLevels(3).build();
        when(paramRepository.load("parameter")).thenReturn(parameter);

        // when
        DetailedParamValue value = engine.getDetailed("parameter", LevelValues.from("A", "B", "C"));

        // then
        MapEntry details = value.detailedRow().entry();
        assertThat((String) details.get("level1")).isEqualTo("A");
        assertThat((String) details.get("level2")).isEqualTo("B");
        assertThat((String) details.get("level3")).isEqualTo("C");
        assertThat((long) details.get("output")).isEqualTo(11L);
    }

    @Test
    public void shouldMatchNullLevelValues() {
        // given
        Level[] levels = new Level[]{
                level().withType("string").build(),
                level().withType("string").build(),
                level().withType("integer").build()
        };
        ParameterEntry[] entries = new ParameterEntry[]{
                parameterEntry().withLevels("A", null, "11").build(),
                parameterEntry().withLevels("B", "F", "21").build()
        };
        Parameter parameter = parameter().withLevels(levels).withEntries(entries).withInputLevels(2).build();
        when(paramRepository.load("parameter")).thenReturn(parameter);

        // when
        ParamValue value = engine.get("parameter", "A", null);

        // then
        assertThat(value).hasValue(11L);
    }

    @Test
    public void shouldReturnMultipleValuesInOneRow() {
        // given
        Level[] levels = new Level[]{
                level().withType("string").build(),
                level().withType("integer").build(),
                level().withType("integer").build()
        };
        ParameterEntry[] entries = new ParameterEntry[]{
                parameterEntry().withLevels("A", "1", "11").build()
        };
        Parameter parameter = parameter().withLevels(levels).withEntries(entries).withInputLevels(1).build();
        when(paramRepository.load("parameter")).thenReturn(parameter);

        // when
        ParamValue value = engine.get("parameter", "A");

        // then
        assertThat(value).hasSingleRow(1L, 11L);
    }

    @Test
    public void shouldReturnValueForParameterWithNoInputLevels() {
        // given
        Level[] levels = new Level[]{
                level().withType("integer").build()
        };
        ParameterEntry[] entries = new ParameterEntry[]{
                parameterEntry().withLevels("42").build()
        };
        Parameter parameter = parameter().withLevels(levels).withEntries(entries).withInputLevels(0).build();
        when(paramRepository.load("parameter")).thenReturn(parameter);

        // when
        ParamValue value = engine.get("parameter");

        // then
        assertThat(value).hasValue(42l);
    }

    @Test
    public void shouldThrowExceptionWhenProvidingMoreQueryValuesThanDeclaredInputLevels() {
        // given
        Level[] levels = new Level[]{
                level().withType("string").build(),
                level().withType("integer").build()
        };
        ParameterEntry[] entries = new ParameterEntry[]{
                parameterEntry().withLevels("A", "42").build()
        };
        Parameter parameter = parameter().withLevels(levels).withEntries(entries).withInputLevels(1).build();
        when(paramRepository.load("parameter")).thenReturn(parameter);

        // when
        catchException(engine).get("parameter", "A", "B");

        // then
        assertThat((Exception) caughtException()).isInstanceOf(InvalidLevelValuesQuery.class);
    }

    @Test
    public void shouldThrowExceptionWhenProvidingLessQueryValuesThanDeclaredInputLevels() {
        // given
        Level[] levels = new Level[]{
                level().withType("string").build(),
                level().withType("string").build(),
                level().withType("integer").build()
        };
        ParameterEntry[] entries = new ParameterEntry[]{
                parameterEntry().withLevels("A", "B", "42").build()
        };
        Parameter parameter = parameter().withLevels(levels).withEntries(entries).withInputLevels(2).build();
        when(paramRepository.load("parameter")).thenReturn(parameter);

        // when
        catchException(engine).get("parameter", "A");

        // then
        assertThat((Exception) caughtException()).isInstanceOf(InvalidLevelValuesQuery.class);
    }

    @Test
    public void shouldFindValueViaRepositoryWhenEvaluatingNoncacheableParameter() {
        // given
        Level[] levels = new Level[]{
                level().withType("string").build(),
                level().withType("string").build(),
                level().withType("integer").build()
        };
        ParameterEntry[] entries = new ParameterEntry[]{
                parameterEntry().withLevels("A", "B", "42").build()
        };
        Parameter parameter = parameter().withName("parameter").nullable().noncacheable().withLevels(levels).withEntries(entries).withInputLevels(2).build();
        when(paramRepository.load("parameter")).thenReturn(parameter);
        when(paramRepository.findEntries("parameter", new String[]{"A", "B"})).thenReturn(new HashSet<ParameterEntry>(Arrays.asList(entries)));

        // when
        ParamValue value = engine.get("parameter", "A", "B");

        // then
        assertThat(value).hasValue(42l);
    }

    @Test
    public void shouldReturnArrayFromCellContentWhenArrayFlagIsSetForLevel() {
        // given
        Level[] levels = new Level[]{
                level().withType("string").build(),
                level().withType("string").array().build()
        };
        ParameterEntry[] entries = new ParameterEntry[]{
                parameterEntry().withLevels("A", "B,C").build()
        };
        Parameter parameter = parameter().withArraySeparator(',').withLevels(levels).withEntries(entries).withInputLevels(1).build();
        when(paramRepository.load("parameter")).thenReturn(parameter);

        // when
        ParamValue value = engine.get("parameter", "A");

        // then
        assertThat(value).hasArray(0, "B", "C");
    }

    @Test
    public void shouldReturnKeysForResultingParameterEntriesWhenIdentifiableParameterFlagIsSet() {
        // given
        Level[] levels = new Level[]{
                level().withType("string").build(),
                level().withType("string").build()
        };
        ParameterEntry[] entries = new ParameterEntry[]{
                parameterEntry().withLevels("A", "B").withKey("entry-key").build()
        };
        Parameter parameter = parameter().identifyEntries().withArraySeparator(',').withLevels(levels).withEntries(entries).withInputLevels(1).build();
        when(paramRepository.load("parameter")).thenReturn(parameter);

        // when
        ParamValue value = engine.get("parameter", "A");

        // then
        assertThat(value.key().value()).isEqualTo("entry-key");
    }

    @Test
    public void shouldThrowExceptionWhenTryingToRetrieveEntryKeyWhenIdentifiableParameterFlagIsNotSet() {
        // given
        Level[] levels = new Level[]{
                level().withType("string").build(),
                level().withType("string").build()
        };
        ParameterEntry[] entries = new ParameterEntry[]{
                parameterEntry().withLevels("A", "B").build()
        };
        Parameter parameter = parameter().withArraySeparator(',').withLevels(levels).withEntries(entries).withInputLevels(1).build();
        when(paramRepository.load("parameter")).thenReturn(parameter);

        // when
        ParamValue value = engine.get("parameter", "A");
        try {
            value.key();
            Assertions.fail("Expected GettingKeyNotIdentifiableParameterException.");
        } catch (GettingKeyNotIdentifiableParameterException exception) {
            // then
        }
    }

    @Test
    public void shouldReturnMultipleMatchingRows() {
        // given
        Level[] levels = new Level[]{
                level().withType("string").build(),
                level().withType("string").build(),
                level().withType("integer").build()
        };
        ParameterEntry[] entries = new ParameterEntry[]{
                parameterEntry().withLevels("A", "B", "42", "43").build(),
                parameterEntry().withLevels("A", "B", "43", "44").build()
        };
        Parameter parameter = parameter().withLevels(levels).withEntries(entries).withInputLevels(2).build();
        when(paramRepository.load("parameter")).thenReturn(parameter);

        // when
        ParamValue value = engine.get("parameter", "A", "B");

        // then
        assertThat(value).hasRows(2).hasRowWithValues(42l, 43l).hasRowWithValues(43l, 44l);
    }

    @Test
    public void shouldReturnValueOfParameterWithLevelValuesPassedInAnotherFormat() {

        // given
        Level[] levels = new Level[]{
                level().withType("date").build(), // input
                level().withType("integer").build() // output
        };
        ParameterEntry[] entries = new ParameterEntry[]{
                parameterEntry().withLevels("2013-01-27", "5").build(),
                parameterEntry().withLevels("*", "9").build()
        };
        Parameter parameter = parameter().withLevels(levels).withEntries(entries).withInputLevels(1).build();
        when(paramRepository.load("parameter")).thenReturn(parameter);

        // when - then
        assertThat(engine.get("parameter", "2013-01-27")).hasIntValue(5);
        assertThat(engine.get("parameter", "2013.01.27")).hasIntValue(5);
        assertThat(engine.get("parameter", "27/01/2013")).hasIntValue(5);
        assertThat(engine.get("parameter", "27/01/1999")).hasIntValue(9);
    }

    @Test
    public void shouldAsteriskDoNotPreventLevelNormalization() {
        // given
        Level[] levels = new Level[]{
                level().withType("date").build(), // input
                level().withType("integer").build() // output
        };
        ParameterEntry[] entries = new ParameterEntry[]{
                parameterEntry().withLevels("*", "9").build()
        };
        Parameter parameter = parameter().withLevels(levels).withEntries(entries).withInputLevels(1).build();
        when(paramRepository.load("parameter")).thenReturn(parameter);

        // when
        ParamValue value = engine.get("parameter", "2013-01-01");

        // then
        assertThat(value).hasIntValue(9);
    }

    @Test
    public void shouldReturnValueOfParameterWithLevelValuePassedAsObject() throws ParseException {

        // given
        Level[] levels = new Level[]{
                level().withType("date").build(), // input
                level().withType("integer").build() // output
        };
        ParameterEntry[] entries = new ParameterEntry[]{
                parameterEntry().withLevels("27/01/2013", "5").build(),
                parameterEntry().withLevels("*", "9").build()
        };
        Parameter parameter = parameter().withLevels(levels).withEntries(entries).withInputLevels(1).build();
        when(paramRepository.load("parameter")).thenReturn(parameter);

        Date date = parseDate("2013-01-27");

        // when
        ParamValue value = engine.get("parameter", date);

        // then
        assertThat(value).hasIntValue(5);
    }

    @Test
    public void shouldReturnDefaultValueIfObjectLevelDoesNotMatchAnyEntry() throws ParseException {

        // given
        Level[] levels = new Level[]{
                level().withType("date").build(), // input
                level().withType("integer").build() // output
        };
        ParameterEntry[] entries = new ParameterEntry[]{
                parameterEntry().withLevels("27/01/2013", "5").build(),
                parameterEntry().withLevels("*", "9").build()
        };
        Parameter parameter = parameter().withLevels(levels).withEntries(entries).withInputLevels(1).build();
        when(paramRepository.load("parameter")).thenReturn(parameter);

        Date date = parseDate("1999-09-09");

        // when
        ParamValue value = engine.get("parameter", date);

        // then
        assertThat(value).hasIntValue(9);
    }

    @Test
    public void shouldReturnValueWhenMatcherIsUsedOnObjectLevelValue() throws ParseException {

        // given
        Level[] levels = new Level[]{
                level().withType("date").withMatcher("between").build(), // input
                level().withType("integer").build() // output
        };
        ParameterEntry[] entries = new ParameterEntry[]{
                parameterEntry().withLevels("01/01/2013 - 05/01/2013", "1").build(),
                parameterEntry().withLevels("*", "2").build()
        };
        Parameter parameter = parameter().withLevels(levels).withEntries(entries).withInputLevels(1).build();
        when(paramRepository.load("parameter")).thenReturn(parameter);

        Date date1 = parseDate("2013-01-04");
        Date date2 = parseDate("2013-01-06");

        // when - then
        assertThat(engine.get("parameter", date1)).hasIntValue(1);
        assertThat(engine.get("parameter", date2)).hasIntValue(2);
    }

    @Test
    public void shouldReturnValueOfParameterWhenLevelValueIsCorrupted() {

        // given
        Level[] levels = new Level[]{
                level().withType("date").build(), // input
                level().withType("integer").build() // output
        };
        ParameterEntry[] entries = new ParameterEntry[]{
                parameterEntry().withLevels("2013-01-27", "5").build(),
                parameterEntry().withLevels("*", "9").build()
        };
        Parameter parameter = parameter().withLevels(levels).withEntries(entries).withInputLevels(1).build();
        when(paramRepository.load("parameter")).thenReturn(parameter);

        // when
        ParamValue value = engine.get("parameter", "2013--0127");

        // then
        assertThat(value).hasIntValue(9);
    }

    @Test
    public void shouldReturnValueOfParameterWhenLevelValueIsNull() {

        // given
        Level[] levels = new Level[]{
                level().withType("date").build(), // input
                level().withType("integer").build() // output
        };
        ParameterEntry[] entries = new ParameterEntry[]{
                parameterEntry().withLevels(null, "5").build(),
                parameterEntry().withLevels("*", "9").build()
        };
        Parameter parameter = parameter().withLevels(levels).withEntries(entries).withInputLevels(1).build();
        when(paramRepository.load("parameter")).thenReturn(parameter);

        // when
        ParamValue value = engine.get("parameter", (Object) null);

        // then
        assertThat(value).hasIntValue(5);
    }

    private Date parseDate(String text) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(text);
    }

    @Test
    public void shouldThrowExceptionIfParamterNotFoundInRepositories() {
        // given
        // when
        catchException(engine).get("unknown");

        // then
        assertThat((Exception) caughtException()).isInstanceOf(UnknownParameterException.class);
    }

    @Test
    public void shouldThrowExceptionWhenTryingToReadArrayFromlevelThatWasNotMarkedAsArray() {
        // given
        Level[] levels = new Level[]{
                level().withType("string").build(),
                level().withType("string").build(),
                level().withType("string").build()
        };
        ParameterEntry[] entries = new ParameterEntry[]{
                parameterEntry().withLevels("A", "B", "42,43").build(),};
        Parameter parameter = parameter().withArraySeparator(',').withLevels(levels).withEntries(entries).withInputLevels(2).build();
        when(paramRepository.load("parameter")).thenReturn(parameter);
        ParamValue value = engine.get("parameter", "A", "B");

        // when
        catchException(value.row()).getArray(0);

        // then
        assertThat((Exception) caughtException()).isInstanceOf(GettingWrongTypeException.class);
    }

    @Test
    public void shouldCallFunctionReturnedByParameter() {
        // given
        Level[] levels = new Level[]{
                level().withType("string").build(),
                level().withType("string").build(),
                level().withType("string").build()
        };
        ParameterEntry[] entries = new ParameterEntry[]{
                parameterEntry().withLevels("A", "B", "function").build(),};
        Parameter parameter = parameter().withLevels(levels).withEntries(entries).withInputLevels(2).build();
        Function function = javaFunction().build();
        when(paramRepository.load("parameter")).thenReturn(parameter);
        when(functionRepository.loadFunction("function")).thenReturn(function);

        // when
        engine.callEvaluatedFunction("parameter", new LevelValues("A", "B"), "argument");

        // then
        verify(functionInvoker, times(1)).invoke(function, "argument");
    }

    @Test
    public void shouldThrowExceptionIfTryingToCallFunctionFromNonStringType() {
        // given
        Level[] levels = new Level[]{
                level().withType("string").build(),
                level().withType("integer").build()
        };
        ParameterEntry[] entries = new ParameterEntry[]{
                parameterEntry().withLevels("A", "42").build()};
        Parameter parameter = parameter().withLevels(levels).withEntries(entries).withInputLevels(1).build();
        when(paramRepository.load("parameter")).thenReturn(parameter);

        // when
        catchException(engine).callEvaluatedFunction("parameter", new LevelValues("A"), "argument");

        // then
        assertThat((Exception) caughtException()).isInstanceOf(InvalidFunctionToCallException.class);
    }
}
