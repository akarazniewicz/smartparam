package org.smartparam.function.editor.store;

import org.smartparam.engine.core.exception.SmartParamException;

public class InvalidFunctionRepositorySourceException extends SmartParamException {
    public InvalidFunctionRepositorySourceException(String name) {
        super("Invalid function repository: " + name);
    }
}
