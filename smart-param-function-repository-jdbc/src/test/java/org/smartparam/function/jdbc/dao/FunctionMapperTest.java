package org.smartparam.function.jdbc.dao;
import org.smartparam.function.jdbc.function.GroovyFunction;
import org.testng.annotations.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Test
class FunctionMapperTest {

    @Test
    public void  testFunctionDeserialization() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);

        when(resultSet.getString("name"))
                .thenReturn("testFunction");
        when(resultSet.getString("body"))
                .thenReturn("return a + b");
        when(resultSet.getString("signature"))
                .thenReturn("a:String,b:String");

        GroovyFunction function = new FunctionMapper().createObject(resultSet);

        assertEquals("testFunction", function.getName());
        assertNotNull(function.getScript());
        assertEquals("a", function.getSignature().get(0).getName());
        assertEquals("String", function.getSignature().get(0).getType());
        assertEquals("b", function.getSignature().get(1).getName());
        assertEquals("String", function.getSignature().get(1).getType());
    }
}