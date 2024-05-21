package org.smartparam.function.jdbc.function;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.smartparam.engine.core.function.Function;
import org.smartparam.function.jdbc.core.FunctionParam;

import java.util.List;

public class GroovyFunction extends Function {

    public final static String FUNCTION_TYPE = "groovy";
    private final List<FunctionParam> signature;
    private final String body;
    private Script script = null;

    public GroovyFunction(String name, List<FunctionParam> signature, String body) {
        super(name, FUNCTION_TYPE);
        this.signature = signature;
        this.body = body;
    }

    public void initialize() {
        script = new GroovyShell().parse(body);
    }

    public boolean isInitialized() {
        return script != null;
    }

    public Script getScript() {
        return script;
    }

    public List<FunctionParam> getSignature() {
        return signature;
    }

    public String getBody() {
        return body;
    }
}
