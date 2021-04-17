package org.smartparam.function.jdbc;

import org.picocontainer.PicoContainer;
import org.polyjdbc.core.infrastructure.PolyDatabaseTest;
import org.smartparam.function.jdbc.config.DefaultJdbcFunctionAndParamConfig;
import org.smartparam.function.jdbc.schema.DefaultFunctionAndParamSchemaCreator;
import org.smartparam.repository.jdbc.config.DefaultJdbcConfig;
import org.smartparam.repository.jdbc.config.JdbcConfig;
import org.smartparam.repository.jdbc.config.JdbcConfigBuilder;
import org.smartparam.repository.jdbc.schema.SchemaCreator;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.smartparam.repository.jdbc.config.JdbcConfigBuilder.jdbcConfig;

public class DatabaseTest extends PolyDatabaseTest {

    private SchemaCreator schemaCreator;

    private PicoContainer container;

    protected <T> T get(Class<T> objectClass) {
        return container.getComponent(objectClass);
    }

    @BeforeClass(alwaysRun = true)
    public void setUpDatabase() throws Exception {
        DataSource dataSource = createDatabase("H2", "jdbc:h2:mem:test", "smartparam", "smartparam");

        JdbcConfigBuilder configurationBuilder = jdbcConfig().withDialect(dialect())
                .withParameterSufix("parameter").withLevelSufix("level")
                .withParameterEntrySufix("entry");
        customizeConfiguraion(configurationBuilder);
        DefaultJdbcFunctionAndParamConfig configuration = new DefaultJdbcFunctionAndParamConfig(configurationBuilder.build());

        this.schemaCreator = new DefaultFunctionAndParamSchemaCreator(configuration, schemaManagerFactory());
        schemaCreator.createSchema();

        JdbcFunctionAndParamRepositoryFactory factory = new JdbcFunctionAndParamRepositoryFactory();
        this.container = factory.createContainer(new JdbcFunctionAndParamRepositoryConfig(dataSource, configuration));
    }

    protected void customizeConfiguraion(JdbcConfigBuilder builder) {
    }

    @BeforeMethod(alwaysRun = true)
    public void cleanDatabase() {
        JdbcConfig config = get(JdbcConfig.class);
        List<String> relationsToDelete = Arrays.asList(config.managedEntities());
        Collections.reverse(relationsToDelete);
        super.deleteFromRelations(relationsToDelete);
    }

    @AfterClass(alwaysRun = true)
    public void tearDownDatabase() throws Exception {
        dropDatabase();
        this.container = null;
    }

    @Override
    protected void dropSchema() {
        schemaCreator.dropSchema();
    }
}
