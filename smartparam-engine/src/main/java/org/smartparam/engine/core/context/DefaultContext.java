/*
 * Copyright 2013 Adam Dubiel, Przemek Hertel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartparam.engine.core.context;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import org.smartparam.engine.util.reflection.InnerReflectiveOperationException;
import org.smartparam.engine.util.reflection.ReflectionSetterInvoker;

/**
 * Implementation of dynamic {@link ParamContext}.
 *
 * Core of default context is the initialization algorithm, that can turn list of loosely provided
 * values into a meaningful context ({@link DefaultContext#DefaultContext(java.lang.Object[]) }),
 * that is later translated into string array of level values (with help of level creators)
 * returned by {@link ParamContext#getLevelValues() }.
 *
 * DefaultContext should be extended to form specialized user contexts, which can
 * provide support for application domain objects. Thanks to DefaultContext
 * initialization algorithm, it is enough to pass an object to the constructor and
 * it will take care of running a setter in user implemented context to set the
 * value.
 *
 * @author Przemek Hertel
 * @since 1.0.0
 */
public class DefaultContext extends BaseParamContext {

    private static final Locale DEFAULT_LOCALE = Locale.getDefault();

    private Locale locale = DEFAULT_LOCALE;

    /**
     * Setter cache. Keeps reference to setter methods extracted via reflection
     * mechanisms. Speeds up initialization of context up to 3-4 times.
     */
    private static ReflectionSetterInvoker sharedSetterInvoker = new ReflectionSetterInvoker();

    private ReflectionSetterInvoker setterInvoker = sharedSetterInvoker;

    private Map<String, Object> userContext;

    /**
     * Puts provided values into context using algorithm:
     * <ol>
     * <li>if args[i] is String[] level values are set using {@link #setLevelValues(Object...)}  } </li>
     * <li>if args[i] is Object[] level values are set using {@link #setLevelValues(java.lang.Object[]) } </li>
     * <li>if args[i] is String args[i+1] value is taken and put into context under args[i] key using {@link #setLevelValues(Object...)}  }</li>
     * <li>if args[i] is Locale, it is set as a locale used for lowercase operations in {@link #set(Object)}  }</li>
     * <li>else, setter lookup is performed using {@link ReflectionSetterInvoker} to find any setter of current context object that accepts args[i]</li>
     * <li>eventually, args[i] is put into context under its class name using {@link #set(java.lang.Object) }</li>
     * </ol>.
     *
     * This mechanism should be used with caution, as sometimes it can produce
     * unexpected (although perfectly valid and deterministic) results. Biggest pitfall
     * is hidden in implementation of {@link ReflectionSetterInvoker#findSetter(java.lang.Class, java.lang.Object) },
     * which may lead to nondeterministic behavior if used incorrectly. In short,
     * make sure you don't use automatic setter invocation if you need to define
     * two setters that accept same type of object (i.e. two setters for different
     * Date objects).
     *
     * There is one more, power-user property. It is possible to substitute default
     * implementation of {@link ReflectionSetterInvoker}, utility responsible
     * for efficient setter invoking (includes inner cache). DefaultContext keeps
     * default setter invoker as a static property, to share its caching abilities
     * among all instances of DeaultContext. To substitute it with own
     * implementation, pass ReflectionSetterInvoker object as <b>first</b>
     * argument. Passing setter invoker on any other position will have no effect,
     * as it will be treated as a normal argument.
     *
     * @param args
     */
    public DefaultContext(Object... args) {
        initialize(args);
    }

    /**
     * Create empty context, use setter methods to initialize it.
     *
     * @see #DefaultContext(java.lang.Object[])
     */
    public DefaultContext() {
    }

    /**
     * Implementation of value initializing algorithm.
     *
     * @param args
     *
     * @see #DefaultContext(java.lang.Object[])
     */
    protected final void initialize(Object... args) {
        for (int argumentIndex = 0; argumentIndex < args.length; ++argumentIndex) {
            Object arg = getArgumentAt(args, argumentIndex);

            if (argumentIndex == 0 && arg instanceof ReflectionSetterInvoker) {
                setterInvoker = (ReflectionSetterInvoker) arg;
            } else if (arg instanceof String[]) {
                setLevelValues((Object[]) arg);
            } else if (arg instanceof Object[]) {
                setLevelValues((Object[]) arg);
            } else if (arg instanceof Locale) {
                locale = (Locale) arg;
            } else if (arg instanceof String) {
                // skip one, cos it is being consumed now
                argumentIndex++;
                with((String) arg, getArgumentAt(args, argumentIndex));
            } else if (arg != null) {
                boolean setterFound = findAndInvokeSetter(arg);
                if (!setterFound) {
                    set(arg);
                }
            }
        }
    }

    /**
     * Put value under lowercase(key). Will throw a
     * {@link DuplicateContextItemException} if there was value registered already.
     *
     * @param key
     * @param value
     * @return
     *
     */
    public final DefaultContext with(String key, Object value) {
        return with(key, value, false);
    }

    /**
     * Put value under key lowercase(key). allowOverwrite flag
     * determines what happens in case of key collision. If overwriting is allowed,
     * new value replaces old one, otherwise {@link DuplicateContextItemException} is
     * thrown. Lowercase function uses default JVM locale, if none other specified.
     *
     * @param key
     * @param value
     * @param allowOverwrite
     * @return
     *
     * @see Locale#getDefault()
     */
    public final DefaultContext with(String key, Object value, boolean allowOverwrite) {
        if (userContext == null) {
            userContext = new TreeMap<String, Object>();
        }

        String lowerKey = lowercase(key);
        if (userContext.containsKey(lowerKey) && !allowOverwrite) {
            throw new DuplicateContextItemException(key);
        }

        userContext.put(lowerKey, value);
        return this;
    }

    private String lowercase(final String string) {
        return string.toLowerCase(locale);
    }

    /**
     * Put value under lowercase(value.class.getSimpleName()) in user
     * context map.
     *
     * @param value
     * @return
     */
    public final DefaultContext set(Object value) {
        return with(value.getClass().getSimpleName(), value);
    }

    /**
     * Return object stored under key. Lowercase function uses default JVM locale,
     * if none other specified.
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        return userContext != null ? userContext.get(lowercase(key)) : null;
    }

    /**
     * Return object stored under key and cast it to given class.
     *
     * @param <T>
     * @param key
     * @param targetClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> targetClass) {
        return (T) get(key);
    }

    /**
     * Return object stored under key as String.
     *
     * @see #get(java.lang.String)
     * @param key
     * @return
     */
    public String getString(String key) {
        return get(key, String.class);
    }

    /**
     * Looks for object of class clazz (or object which class is
     * assignable from clazz. Algorithm:
     * <ol>
     * <li>look for object stored under clazz.getSimpleName(), return if not null and class match</li>
     * <li>iterate through all context values to look for first object that matches provided clazz</li>
     * </ol>
     *
     * @param <T>
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> clazz) {

        if (userContext != null) {

            Object obj = get(clazz.getSimpleName());
            if (obj != null && obj.getClass() == clazz) {
                return (T) obj;
            }

            for (Object contextValue : userContext.values()) {
                if (contextValue == null) {
                    continue;
                }

                if (clazz.isAssignableFrom(contextValue.getClass())) {
                    return (T) contextValue;
                }
            }
        }
        return null;
    }

    private Object getArgumentAt(Object[] args, int index) {
        if (index < args.length) {
            return args[index];
        }
        throw new InvalidContextArgumentsCountException(index, args.length);
    }

    private boolean findAndInvokeSetter(Object arg) {
        try {
            return setterInvoker.invokeSetter(this, arg);
        } catch (InnerReflectiveOperationException exception) {
            throw new ContextInitializationException(exception, arg);
        }
    }

    /**
     * Explicitly set locale used for lowercase operation on map keys used by
     * {@link #get(java.lang.String) }.
     *
     * @param locale
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public DefaultContext withLevelValues(String... levelValues) {
        setLevelValues((Object[]) levelValues);
        return this;
    }

    public DefaultContext withLevelValues(Object... levelValues) {
        setLevelValues(levelValues);
        return this;
    }

    /**
     * Return map representing parameter evaluation context.
     *
     * @return
     */
    protected Map<String, Object> getUserContext() {
        return userContext;
    }

    @Override
    public String toString() {
        return "DefaultContext[levelValues=" + Arrays.toString(getLevelValues()) + ", userContext=" + userContext + ']';
    }
}
