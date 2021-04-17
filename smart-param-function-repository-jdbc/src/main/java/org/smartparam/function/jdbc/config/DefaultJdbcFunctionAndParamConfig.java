package org.smartparam.function.jdbc.config;

import org.polyjdbc.core.dialect.Dialect;
import org.smartparam.repository.jdbc.config.DefaultJdbcConfig;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultJdbcFunctionAndParamConfig extends DefaultJdbcConfig implements JdbcFunctionConfig {

    private final String functionEntitySuffix = "function";
    private final String functionSequenceSuffix = "function";

    public DefaultJdbcFunctionAndParamConfig() {
        super();
    }

    public DefaultJdbcFunctionAndParamConfig(Dialect dialect) {
        super(dialect);
    }

    public DefaultJdbcFunctionAndParamConfig(DefaultJdbcConfig config) {
        super(config.dialect());
    }

    @Override
    public String functionEntityName() {
        return entityPrefix() + functionEntitySuffix;
    }

    @Override
    public String functionSequenceName() {
        return sequencePrefix() + functionSequenceSuffix;
    }

    @Override
    public String[] managedEntities() {
        return Stream.of(super.managedEntities(), new String[] {functionEntityName()})
                .flatMap(Stream::of)
                .collect(Collectors.toList())
                .toArray(new String[]{});
    }
}
