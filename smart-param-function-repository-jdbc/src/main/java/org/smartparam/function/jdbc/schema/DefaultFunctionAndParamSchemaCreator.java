package org.smartparam.function.jdbc.schema;

import org.polyjdbc.core.schema.SchemaInspector;
import org.polyjdbc.core.schema.SchemaManager;
import org.polyjdbc.core.schema.SchemaManagerFactory;
import org.polyjdbc.core.schema.model.Schema;
import org.polyjdbc.core.util.TheCloser;
import org.smartparam.function.jdbc.config.DefaultJdbcFunctionAndParamConfig;
import org.smartparam.repository.jdbc.schema.DefaultSchemaCreator;

public class DefaultFunctionAndParamSchemaCreator extends DefaultSchemaCreator {

    private final DefaultJdbcFunctionAndParamConfig config;
    private final SchemaManagerFactory schemaManagerFactory;

    public DefaultFunctionAndParamSchemaCreator(
            DefaultJdbcFunctionAndParamConfig configuration,
            SchemaManagerFactory schemaManagerFactory
    ) {
        super(configuration, schemaManagerFactory);
        this.config = configuration;
        this.schemaManagerFactory = schemaManagerFactory;
    }

    @Override
    public void createSchema() {
        super.createSchema();
        SchemaManager schemaManager = null;
        SchemaInspector schemaInspector = null;
        try {
            schemaManager = schemaManagerFactory.createManager();
            schemaInspector = schemaManagerFactory.createInspector();

            Schema schema = new Schema(config.dialect());
            createFunctionRelation(schema, schemaInspector);

            schemaManager.create(schema);
        } finally {
            TheCloser.close(schemaManager, schemaInspector);
        }
    }

    private void createFunctionRelation(Schema schema, SchemaInspector schemaInspector) {
        String relationName = config.functionEntityName();
        if (!schemaInspector.relationExists(relationName)) {
            schema.addRelation(relationName)
                    .withAttribute().longAttr("id").withAdditionalModifiers("AUTO_INCREMENT").notNull().and()
                    .withAttribute().string("name").withMaxLength(1024).notNull().unique().and()
                    .withAttribute().text("signature").notNull().and()
                    .withAttribute().text("body").notNull().and()
                    .primaryKey(primaryKey(relationName)).using("id").and()
                    .build();
            schema.addIndex(index(relationName) + "_id").indexing("id").on(relationName).build();
            schema.addIndex(index(relationName) + "_name").indexing("name").on(relationName).build();
            schema.addSequence(config.functionSequenceName()).build();
        }
    }

    @Override
    public void dropSchema() {
        super.dropSchema();
        SchemaManager schemaManager = null;
        try {
            schemaManager = schemaManagerFactory.createManager();

            Schema schema = new Schema(config.dialect());
            schema.addRelation(config.functionEntityName()).build();
            schema.addSequence(config.functionSequenceName()).build();

            schemaManager.drop(schema);
        } finally {
            TheCloser.close(schemaManager);
        }
    }
}
