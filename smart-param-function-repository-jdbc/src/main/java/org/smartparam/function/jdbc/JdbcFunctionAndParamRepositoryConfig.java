package org.smartparam.function.jdbc;

import org.polyjdbc.core.query.QueryRunnerFactory;
import org.polyjdbc.core.query.SimpleQueryRunner;
import org.polyjdbc.core.query.TransactionRunner;
import org.polyjdbc.core.schema.SchemaManagerFactory;
import org.polyjdbc.core.transaction.DataSourceTransactionManager;
import org.polyjdbc.core.transaction.TransactionManager;
import org.smartparam.engine.config.pico.ComponentDefinition;
import org.smartparam.function.jdbc.dao.FunctionDAO;
import org.smartparam.function.jdbc.config.JdbcFunctionConfig;
import org.smartparam.function.jdbc.schema.DefaultFunctionAndParamSchemaCreator;
import org.smartparam.repository.jdbc.JdbcParamRepositoryConfig;
import org.smartparam.repository.jdbc.batch.JdbcParameterEntryBatchLoaderFactory;
import org.smartparam.repository.jdbc.dao.JdbcRepository;
import org.smartparam.repository.jdbc.dao.LevelDAO;
import org.smartparam.repository.jdbc.dao.ParameterDAO;
import org.smartparam.repository.jdbc.dao.ParameterEntryDAO;
import org.smartparam.repository.jdbc.dao.SimpleJdbcRepository;
import org.smartparam.repository.jdbc.schema.DefaultSchemaCreator;

import javax.sql.DataSource;
import java.util.Set;

import static org.smartparam.engine.config.pico.ComponentDefinition.component;

public class JdbcFunctionAndParamRepositoryConfig extends JdbcParamRepositoryConfig  {

    public JdbcFunctionAndParamRepositoryConfig(DataSource dataSource, JdbcFunctionConfig configuration) {
        super(dataSource, configuration);
    }

    @Override
    protected void injectDefaults(Set<ComponentDefinition> components) {
        components.add(component(JdbcRepository.class, SimpleJdbcRepository.class));
        components.add(component(TransactionManager.class, DataSourceTransactionManager.class));
        components.add(component(QueryRunnerFactory.class, QueryRunnerFactory.class));
        components.add(component(SchemaManagerFactory.class, SchemaManagerFactory.class));
        components.add(component(FunctionDAO.class, FunctionDAO.class));
        components.add(component(ParameterDAO.class, ParameterDAO.class));
        components.add(component(LevelDAO.class, LevelDAO.class));
        components.add(component(ParameterEntryDAO.class, ParameterEntryDAO.class));
        components.add(component(SimpleQueryRunner.class, SimpleQueryRunner.class));
        components.add(component(DefaultSchemaCreator.class, DefaultFunctionAndParamSchemaCreator.class));
        components.add(component(TransactionRunner.class, TransactionRunner.class));
        components.add(component(JdbcParameterEntryBatchLoaderFactory.class, JdbcParameterEntryBatchLoaderFactory.class));
    }
}
