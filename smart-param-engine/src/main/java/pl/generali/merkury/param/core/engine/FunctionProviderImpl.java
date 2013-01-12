package pl.generali.merkury.param.core.engine;

import pl.generali.merkury.param.core.loader.FunctionLoader;
import pl.generali.merkury.param.core.cache.FunctionCache;
import pl.generali.merkury.param.core.exception.ParamDefinitionException;
import pl.generali.merkury.param.core.exception.ParamException.ErrorCode;
import pl.generali.merkury.param.model.Function;

/**
 * Service Provider, ktory dostarcza funkcje z repozytorium o zadanej nazwie.
 * Pobiera funkcje przy pomocy loadera ({@link FunctionLoader}), ktorego zadaniem
 * jest fizyczne wczytani funkcji z bazy danych.
 * Wczytana funkcja jest cache'owana przy pomocy {@link FunctionCache}.
 *
 * @author Przemek Hertel
 * @since 1.0.0
 */
public class FunctionProviderImpl implements FunctionProvider {

    /**
     * Loader, ktory fizycznie wczytuje obiekt funkcji.
     */
    private FunctionLoader loader = null;

    /**
     * Cache, w ktorym zapamietywana sa wczytane funkcje.
     */
    private FunctionCache cache = null;

    @Override
    public Function getFunction(String name) {

        Function f = cache.get(name);

        if (f == null) {
            f = loader.load(name);

            if (f == null) {
                throw new ParamDefinitionException(ErrorCode.UNKNOWN_FUNCTION, "Unknown function: " + name);
            }

            cache.put(name, f);
        }

        return f;
    }

    /**
     * Setter dla cache.
     *
     * @param cache cache.
     */
    public void setCache(FunctionCache cache) {
        this.cache = cache;
    }

    /**
     * Setter dla loadera.
     *
     * @param loader loader
     */
    public void setLoader(FunctionLoader loader) {
        this.loader = loader;
    }
}
