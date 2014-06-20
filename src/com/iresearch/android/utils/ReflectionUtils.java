package com.iresearch.android.utils;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * @className ReflectionUtils
 * @create 2014年4月16日 上午11:42:45
 * @author lilong (dreamxsky@gmail.com)
 * @description A set of helper methods for best-effort method calls via reflection.
 */
public class ReflectionUtils {

	public static Object tryInvoke(Object target, String methodName, Object... args) {
        Class<?>[] argTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }

        return tryInvoke(target, methodName, argTypes, args);
    }
	
	public static Object tryInvoke(Object target, String methodName, Class<?>[] argTypes,
            Object... args) {
        try {
        	Method method = target.getClass().getMethod(methodName, argTypes);
        	return method.invoke(target, args);
        } catch (NoSuchMethodException ignored) {
        	ignored.printStackTrace();
        } catch (IllegalAccessException ignored) {
        	ignored.printStackTrace();
        } catch (InvocationTargetException ignored) {
        	ignored.printStackTrace();
        }

        return null;
    }
	
	@SuppressWarnings("unchecked")
	public static <E> E callWithDefault(Object target, String methodName, E defaultValue) {
        try {
            //noinspection unchecked
            return (E) target.getClass().getMethod(methodName, (Class[]) null).invoke(target);
        } catch (NoSuchMethodException ignored) {
        } catch (IllegalAccessException ignored) {
        } catch (InvocationTargetException ignored) {
        }

        return defaultValue;
    }
}
