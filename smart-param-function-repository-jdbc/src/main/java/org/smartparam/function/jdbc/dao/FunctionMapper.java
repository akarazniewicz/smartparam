package org.smartparam.function.jdbc.dao;

import org.polyjdbc.core.query.mapper.ObjectMapper;
import org.smartparam.function.jdbc.core.FunctionParam;
import org.smartparam.function.jdbc.function.GroovyFunction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class FunctionMapper implements ObjectMapper<GroovyFunction> {

    public static class SignatureMapper {

        String serialize(List<FunctionParam> params) {
             return params.stream().map(p -> p.getName() + ":" + p.getType()).collect(joining(","));
        }

        List<FunctionParam> deserialize(String params) {
            String EMPTY_SIGNATURE = "";
            if (params.trim().equals(EMPTY_SIGNATURE))
                return emptyList();
            else
                return Arrays.stream(params.split(",")).map(p -> {
                    String[] paramAndType = p.split(":");
                    return new FunctionParam(paramAndType[0], paramAndType[1]);
                }).collect(toList());
        }
    }

    @Override
    public GroovyFunction createObject(ResultSet resultSet) throws SQLException {
        SignatureMapper signatureMapper = new SignatureMapper();
        return new GroovyFunction(
                resultSet.getString("name"),
                signatureMapper.deserialize(resultSet.getString("signature")), resultSet.getString("body")
        );
    }
}
