package org.smartparam.function.jdbc.function;

import groovy.lang.MissingPropertyException;
import org.smartparam.function.jdbc.dao.FunctionMapper;
import org.testng.annotations.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Test
class GroovyFunctionInvokerTest {

    @Test
    void shouldCallFunction() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);

        when(resultSet.getString("name"))
                .thenReturn("testFunction");
        when(resultSet.getString("body"))
                .thenReturn("return a + ' '+ b");
        when(resultSet.getString("signature"))
                .thenReturn("a:String,b:String");

        GroovyFunction function = new FunctionMapper().createObject(resultSet);
        GroovyFunctionInvoker invoker = new GroovyFunctionInvoker();
        String result = (String)invoker.invoke(function, "Hello", "World", "Termo");
        assertEquals("Hello World", result);
    }

    @Test
    void emptyListOfArgumentsShouldThrowException() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);

        when(resultSet.getString("name"))
                .thenReturn("testFunction");
        when(resultSet.getString("body"))
                .thenReturn("return a + ' '+ b");
        when(resultSet.getString("signature"))
                .thenReturn("a:String,b:String");

        GroovyFunction function = new FunctionMapper().createObject(resultSet);
        GroovyFunctionInvoker invoker = new GroovyFunctionInvoker();

        assertThrows(MissingPropertyException.class, () -> invoker.invoke(function));
    }
}
