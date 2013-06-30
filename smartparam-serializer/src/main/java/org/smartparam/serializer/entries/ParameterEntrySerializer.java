package org.smartparam.serializer.entries;

import java.io.Writer;
import org.smartparam.serializer.SerializationConfig;
import org.smartparam.serializer.exception.SmartParamSerializationException;

/**
 *
 * @author Adam Dubiel <dubiel.adam@gmail.com>
 */
public interface ParameterEntrySerializer {

    void serialize(SerializationConfig config, Writer writer, ParameterEntrySupplier supplier) throws SmartParamSerializationException;
}