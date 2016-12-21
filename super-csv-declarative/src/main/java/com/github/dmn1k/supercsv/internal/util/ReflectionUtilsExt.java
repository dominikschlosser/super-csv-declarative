package com.github.dmn1k.supercsv.internal.util;

import org.supercsv.exception.SuperCsvReflectionException;
import org.supercsv.util.BeanInterfaceProxy;

public class ReflectionUtilsExt {

    /**
     * Instantiates the bean (or creates a proxy if it's an interface).
     *
     * @param clazz the bean class to instantiate (a proxy will be created if an interface is supplied), using the
     * default (no argument) constructor
     * @return the instantiated bean
     * @throws SuperCsvReflectionException if there was a reflection exception when instantiating the bean
     */
    public static <T> T instantiateBean(final Class<T> clazz) {
        final T bean;
        if (clazz.isInterface()) {
            bean = BeanInterfaceProxy.createProxy(clazz);
        } else {
            try {
                bean = clazz.newInstance();
            } catch (InstantiationException e) {
                throw new SuperCsvReflectionException(String.format(
                        "error instantiating bean, check that %s has a default no-args constructor", clazz.getName()), e);
            } catch (IllegalAccessException e) {
                throw new SuperCsvReflectionException("error instantiating bean", e);
            }
        }

        return bean;
    }
}
