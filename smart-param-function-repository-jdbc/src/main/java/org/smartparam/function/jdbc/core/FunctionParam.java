package org.smartparam.function.jdbc.core;

public class FunctionParam {
    private String name;
    private String type;

    public FunctionParam(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
