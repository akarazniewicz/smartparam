package org.smartparam.engine.core.repository;

import java.util.Map;
import org.smartparam.engine.core.type.Type;

/**
 *
 * @author Przemek Hertel
 * @author Adam Dubiel
 * @since 0.1.0
 */
public interface TypeRepository {

    void registerType(String code, Type<?> type);

    Map<String, Type<?>> registeredTypes();

    Type<?> getType(String code);

    void setTypes(Map<String, Type<?>> types);
}
