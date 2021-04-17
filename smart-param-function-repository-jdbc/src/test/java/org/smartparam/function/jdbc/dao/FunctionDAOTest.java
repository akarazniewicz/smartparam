package org.smartparam.function.jdbc.dao;

import org.polyjdbc.core.query.QueryRunner;
import org.smartparam.engine.core.function.Function;
import org.smartparam.function.jdbc.DatabaseTest;
import org.smartparam.function.jdbc.core.FunctionParam;
import org.smartparam.function.jdbc.function.GroovyFunctionInvoker;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;

import static java.util.Collections.emptyList;
import static org.testng.Assert.assertNull;
import static org.testng.AssertJUnit.assertEquals;

@Test
class FunctionDAOTest extends DatabaseTest {

    @Test
    void shouldCreateFunction() {
        QueryRunner queryRunner = queryRunner();

        FunctionDAO functionDAO = get(FunctionDAO.class);

        long id = functionDAO.insert(queryRunner,
                "testFunction",
                Collections.singletonList(new FunctionParam("premiums", "java.lang.Object")),
                "premiums.inject(0, { sum, value -> sum + value})");
        Function function = functionDAO.getFunction(queryRunner, "testFunction");
        queryRunner.close();

        assertEquals(10, new GroovyFunctionInvoker().invoke(function, Arrays.asList(1, 2, 3, 4)));
    }

    @Test
    void shouldUpdateFunction() {
        QueryRunner queryRunner = queryRunner();

        FunctionDAO functionDAO = get(FunctionDAO.class);
        GroovyFunctionInvoker groovyFunctionInvoker = new GroovyFunctionInvoker();

        functionDAO.insert(queryRunner,
                "testFunction2",
                Collections.singletonList(new FunctionParam("premiums", "java.lang.Object")),
                "premiums.inject(0, { sum, value -> sum + value})");

        Function function = functionDAO.getFunction(queryRunner, "testFunction2");
        Object result1 = groovyFunctionInvoker.invoke(function, Arrays.asList(1, 2, 3, 4));

        functionDAO.update(queryRunner,
                "testFunction2",
                Collections.singletonList(new FunctionParam("premiums", "java.lang.Object")),
                "premiums.max { it }");

        function = functionDAO.getFunction(queryRunner, "testFunction2");
        Object result2 = groovyFunctionInvoker.invoke(function, Arrays.asList(1, 2, 3, 4));
        queryRunner.close();

        assertEquals(10, result1);
        assertEquals(4, result2);
    }

    @Test
    void shouldDeleteFunction() {
        QueryRunner queryRunner = queryRunner();

        FunctionDAO functionDAO = get(FunctionDAO.class);
        GroovyFunctionInvoker groovyFunctionInvoker = new GroovyFunctionInvoker();

        functionDAO.insert(queryRunner,
                "testFunction3",
                emptyList(),
                "return 42");

        Function function = functionDAO.getFunction(queryRunner, "testFunction3");
        Object result1 = groovyFunctionInvoker.invoke(function);

        functionDAO.delete(queryRunner, "testFunction3");
        function = functionDAO.getFunction(queryRunner, "testFunction3");
        queryRunner.close();

        assertEquals(42, result1);
        assertNull(function);
    }

}
