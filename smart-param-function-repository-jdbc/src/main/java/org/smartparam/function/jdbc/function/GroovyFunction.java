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
    private final Script script;

    public GroovyFunction(String name, List<FunctionParam> signature, String body) {
        super(name, FUNCTION_TYPE);
        script = new GroovyShell().parse(body);
        this.signature = signature;
        this.body = body;
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
