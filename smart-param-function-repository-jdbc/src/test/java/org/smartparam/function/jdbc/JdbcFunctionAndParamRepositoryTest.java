package org.smartparam.function.jdbc;

import org.smartparam.engine.core.function.Function;
import org.smartparam.engine.core.function.FunctionInvoker;
import org.smartparam.function.jdbc.core.FunctionParam;
import org.smartparam.function.jdbc.function.GroovyFunctionInvoker;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.testng.AssertJUnit.assertEquals;

@Test
class JdbcFunctionAndParamRepositoryTest extends DatabaseTest {

    @Test
    void shouldInvokeFunction() {
        JdbcFunctionAndParamRepository repository = get(JdbcFunctionAndParamRepository.class);
        FunctionInvoker invoker = new GroovyFunctionInvoker();

        repository.createFunction("testFunction",
                asList(new FunctionParam("firstName", "String"), new FunctionParam("lastName", "String")),
                "return 'Hi there ' + firstName + ' ' + lastName + '!'");

        Function greet = repository.loadFunction("testFunction");

        assertEquals("Hi there John Doe!", invoker.invoke(greet, "John", "Doe"));
    }

    @Test
    void shouldListAllFunctions() {
        JdbcFunctionAndParamRepository repository = get(JdbcFunctionAndParamRepository.class);
        FunctionInvoker invoker = new GroovyFunctionInvoker();

        repository.createFunction("testFunction1", Collections.emptyList(), "1");
        repository.createFunction("testFunction2", Collections.emptyList(), "2");

        List<? extends Function> functions = repository.getFunctions();

        assertEquals("testFunction1", functions.get(0).getName());
        assertEquals("testFunction2", functions.get(1).getName());
    }
}
