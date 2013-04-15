package org.smartparam.spring.function;

import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartparam.engine.core.context.ParamContext;
import org.smartparam.engine.core.exception.SmartParamException;
import org.smartparam.engine.core.exception.SmartParamErrorCode;
import org.smartparam.engine.core.function.AbstractJavaFunctionRepository;
import org.smartparam.engine.core.function.OldFunctionInvoker;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * Klasa odpowiedzialna za wykonywania funkcji (z repozytorium) typu {@link SpringFunction}.
 * Klasa zapewnia:
 * <ul>
 * <li>odnajdowanie metod pasujacych do wywolania (sprawdzanie kompatybilnosci parametrow),
 * <li>cache dla refleksji przyspieszajacy znajdowanie metody okolo 5 razy,
 * </ul>
 *
 * SpringFunctionInvoker pobiera bean springowy o nazwie zdefiniowanej w funkcji SpringFunction
 * a nastepnie wykonuje odpowiednia metode beana.
 *
 * @author Przemek Hertel
 * @since 1.0.0
 */
public class SpringFunctionInvoker extends AbstractJavaFunctionRepository implements OldFunctionInvoker<SpringFunction>, ApplicationContextAware {

    /**
     * Logger.
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Kontekst aplikacji springa.
     */
    private ApplicationContext appContext = null;

    /**
     * {@inheritDoc}
     *
     * @throws ParamException jesli wywolanie zostanie przerwane przez jakikolwiek wyjatek, ({@link ErrorCode#FUNCTION_INVOKE_ERROR})
     */
    @Override
    public Object invoke(SpringFunction function, ParamContext ctx) {
        return call(function, ctx);
    }

    /**
     * {@inheritDoc}
     *
     * @throws ParamException jesli wywolanie zostanie przerwane przez jakikolwiek wyjatek, ({@link ErrorCode#FUNCTION_INVOKE_ERROR})
     */
    @Override
    public Object invoke(SpringFunction function, Object... args) {
        return call(function, args);
    }

    /**
     * Setter dla appContext. Wykorzystywany przez springa podczas podnoszenia kontekstu.
     *
     * @param applicationContext kontekst springa
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        appContext = applicationContext;
    }

    /**
     * Wywoluje funkcje <tt>f</tt> z argumentami <tt>args</tt>.
     * <p>
     * W tym celu:
     * <ol>
     * <li>znajduje beana springowego o nazwie <tt>f.beanName</tt>,
     * <li>znajduje metode o nazwie <tt>f.methodName</tt> korzystajac z cache'a
     * </ol>
     *
     * @param f    funkcja typu spring
     * @param args argumenty wywolania
     *
     * @return obiekt zwrocony przez metode beana
     *
     * @throws ParamException jesli wywolanie zostanie przerwane przez wyjatek, ({@link ErrorCode#FUNCTION_INVOKE_ERROR})
     */
    Object call(SpringFunction f, Object... args) {
        try {
            // pobranie beana z kontekstu springa - moze rzucic BeansException
            Object bean = appContext.getBean(f.getBeanName());

            // znalezienie metody (cache) pasujacej do podanych kryteriow (name, args)
            Method m = findMethod(bean.getClass(), f.getMethodName(), args);

            // wykonanie metody z argumentami args
            return m.invoke(bean, args);

        } catch (Exception e) {
            logger.error("", e);
            throw new SmartParamException(SmartParamErrorCode.FUNCTION_INVOKE_ERROR, e, "Error invoking function: " + f);
        }
    }
}
