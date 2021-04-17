package org.smartparam.function.jdbc.dao;

import org.polyjdbc.core.query.DeleteQuery;
import org.polyjdbc.core.query.InsertQuery;
import org.polyjdbc.core.query.QueryFactory;
import org.polyjdbc.core.query.QueryRunner;
import org.polyjdbc.core.query.SelectQuery;
import org.polyjdbc.core.query.UpdateQuery;
import org.smartparam.function.jdbc.core.FunctionParam;
import org.smartparam.function.jdbc.config.JdbcFunctionConfig;
import org.smartparam.function.jdbc.function.GroovyFunction;

import java.util.List;

public class FunctionDAO {

    private final JdbcFunctionConfig configuration;

    public FunctionDAO(JdbcFunctionConfig configuration) {
        this.configuration = configuration;
    }

    public GroovyFunction getFunction(QueryRunner queryRunner, String name) {
        SelectQuery query = QueryFactory.selectAll().from(configuration.functionEntityName()).where("name = :name")
                .withArgument("name", name);
        return queryRunner.queryUnique(query, new FunctionMapper(), false);
    }

    public List<GroovyFunction> getFunctions(QueryRunner queryRunner) {
        SelectQuery query = QueryFactory.selectAll().from(configuration.functionEntityName());
        return queryRunner.queryList(query, new FunctionMapper());
    }

    public long insert(QueryRunner queryRunner, String function, List<FunctionParam> signature, String body) {
        FunctionMapper.SignatureMapper mapper = new FunctionMapper.SignatureMapper();
        InsertQuery query = QueryFactory.insert().into(configuration.functionEntityName())
                .sequence("id", configuration.functionSequenceName())
                .value("name", function)
                .value("signature", mapper.serialize(signature))
                .value("body", body);
        return queryRunner.insert(query);
    }

    public void delete(QueryRunner queryRunner, String functionName) {
        DeleteQuery query = QueryFactory.delete().from(configuration.functionEntityName()).where("name = :name")
                .withArgument("name", functionName);
        queryRunner.delete(query);
    }

    public long update(QueryRunner queryRunner, String functionName, List<FunctionParam> signature, String body) {
        FunctionMapper.SignatureMapper mapper = new FunctionMapper.SignatureMapper();
        UpdateQuery query = QueryFactory.update(configuration.functionEntityName()).where("name = :name")
                .withArgument("name", functionName)
                .set("signature", mapper.serialize(signature))
                .set("body", body);

        return queryRunner.update(query);
    }
}
