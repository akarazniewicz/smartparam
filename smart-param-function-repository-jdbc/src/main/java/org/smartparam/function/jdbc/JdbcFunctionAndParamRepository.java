package org.smartparam.function.jdbc;

import org.polyjdbc.core.query.QueryRunner;
import org.polyjdbc.core.query.TransactionRunner;
import org.polyjdbc.core.query.TransactionWrapper;
import org.polyjdbc.core.query.VoidTransactionWrapper;
import org.smartparam.engine.core.function.Function;
import org.smartparam.function.jdbc.core.FunctionParam;
import org.smartparam.function.jdbc.dao.FunctionDAO;
import org.smartparam.function.jdbc.function.EditableFunctionRepository;
import org.smartparam.function.jdbc.function.GroovyFunction;
import org.smartparam.repository.jdbc.JdbcParamRepository;
import org.smartparam.repository.jdbc.batch.JdbcParameterEntryBatchLoaderFactory;
import org.smartparam.repository.jdbc.dao.JdbcRepository;
import org.smartparam.repository.jdbc.schema.SchemaCreator;

import java.util.List;

public class JdbcFunctionAndParamRepository extends JdbcParamRepository implements EditableFunctionRepository {

    private final FunctionDAO functionDAO;

    public JdbcFunctionAndParamRepository(
            TransactionRunner operationRunner,
            JdbcParameterEntryBatchLoaderFactory batchLoaderFactory,
            FunctionDAO functionDAO,
            JdbcRepository paramDao,
            SchemaCreator schemaCreator) {
        super(operationRunner, batchLoaderFactory, paramDao, schemaCreator);
        this.functionDAO = functionDAO;
    }

    @Override
    public Long createFunction(String name, List<FunctionParam> params, String body) {
        return transactionRunner.run(queryRunner -> functionDAO.insert(queryRunner, name, params, body));
    }

    @Override
    public Long updateFunction(String functionName, List<FunctionParam> params, String body) {
        return transactionRunner.run(queryRunner -> functionDAO.update(queryRunner, functionName, params, body));
    }

    @Override
    public List<GroovyFunction> getFunctions() {
        return transactionRunner.run(functionDAO::getFunctions);
    }

    @Override
    public GroovyFunction getFunction(String name) {
        return (GroovyFunction) loadFunction(name);
    }

    @Override
    public void deleteFunction(String functionName) {
        transactionRunner.run(new VoidTransactionWrapper() {
            @Override
            public void performVoid(QueryRunner queryRunner) {
                functionDAO.delete(queryRunner, functionName);
            }
        });
    }

    @Override
    public Function loadFunction(String functionName) {
        return transactionRunner.run(
                (TransactionWrapper<Function>) queryRunner -> functionDAO.getFunction(queryRunner, functionName)
        );
    }
}
