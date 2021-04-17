package org.smartparam.function.jdbc;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.smartparam.engine.config.pico.PicoContainerUtil;
import org.smartparam.function.jdbc.config.JdbcFunctionConfig;

import javax.sql.DataSource;

public class JdbcFunctionAndParamRepositoryFactory {

    public static JdbcFunctionAndParamRepository jdbcRepository(DataSource dataSource, JdbcFunctionConfig config) {
        return new JdbcFunctionAndParamRepositoryFactory().createRepository(dataSource, config);
    }

    public JdbcFunctionAndParamRepository createRepository(DataSource dataSource, JdbcFunctionConfig config) {
        return createRepository(new JdbcFunctionAndParamRepositoryConfig(dataSource, config));
    }

    public JdbcFunctionAndParamRepository createRepository(JdbcFunctionAndParamRepositoryConfig config) {
        PicoContainer container = createContainer(config);
        return container.getComponent(JdbcFunctionAndParamRepository.class);
    }

    PicoContainer createContainer(JdbcFunctionAndParamRepositoryConfig config) {
        MutablePicoContainer container = PicoContainerUtil.createContainer();
        PicoContainerUtil.injectImplementations(container, JdbcFunctionAndParamRepository.class,
                config.getConfiguration(), config.getConfiguration().dialect(), config.getDataSource());
        PicoContainerUtil.injectImplementations(container, config.getComponents());

        return container;
    }
}
