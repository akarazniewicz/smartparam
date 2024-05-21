package org.smartparam.function.jdbc.function;

import groovy.lang.Binding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartparam.engine.core.function.Function;
import org.smartparam.engine.core.function.FunctionInvoker;
import org.smartparam.function.jdbc.core.FunctionParam;

import java.util.List;

import static java.lang.Math.min;
import static java.util.Arrays.asList;
import static java.util.stream.IntStream.range;

public class GroovyFunctionInvoker implements FunctionInvoker {

    private final Logger logger = LoggerFactory.getLogger(GroovyFunctionInvoker.class);

    @Override
    public Object invoke(Function function, Object... args) {
        GroovyFunction groovyFunction = (GroovyFunction) function;
        if (!groovyFunction.isInitialized())
            groovyFunction.initialize();
        Binding binding = groovyFunction.getScript().getBinding();
        List<FunctionParam> signature = groovyFunction.getSignature();
        int suppliedArguments = min(args.length, signature.size());
        if (args.length > groovyFunction.getSignature().size())
            logger.warn("Function: {} has been defined with {} arguments, while function invocation " +
                            "supplied {} actual arguments ({}). Following arguments will be ignored: {}",
                    function.getName(),
                    groovyFunction.getSignature().size(),
                    args.length,
                    args,
                    asList(args).subList(groovyFunction.getSignature().size(), args.length));

        range(0, suppliedArguments)
                .forEach(i -> binding.setProperty(signature.get(i).getName(), args[i]));
        return groovyFunction.getScript().run();
    }
}
