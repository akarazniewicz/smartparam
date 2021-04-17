package org.smartparam.function.jdbc.config;

import org.smartparam.repository.jdbc.config.JdbcConfig;

public interface JdbcFunctionConfig extends JdbcConfig {

    String functionEntityName();
    String functionSequenceName();
}
