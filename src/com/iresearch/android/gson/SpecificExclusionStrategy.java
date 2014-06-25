
package com.iresearch.android.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * 序列化json字符串时忽略父类的属性 
 * 
 * 使用过滤器序列化json字符串
 * 
 * Gson gson2 = new GsonBuilder().setExclusionStrategies(
 *          new SpecificClassExclusionStrategy(null, Model.class))
 *          .create();
 *          
 * 过滤字段：
 *      1. 使用java关键字transient
 *      2. 没有被注解 @Expose;标注的字段排除   new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
 *      3. 使用ExclusionStrategy定制字段排除策略
 *
 */
public class SpecificExclusionStrategy implements ExclusionStrategy {
    private final Class<?> excludedThisClass;
    private final Class<?> excludedThisClassFields;

    /***
     * 过滤器初始化
     * 
     * @param excludedThisClass 该类和继承自该类的对象实例将被忽略
     * @param excluedThisClassFields 该类的属性将不被序列化
     */
    public SpecificExclusionStrategy(Class<?> excludedThisClass,
            Class<?> excluedThisClassFields) {
        this.excludedThisClass = excludedThisClass;
        this.excludedThisClassFields = excluedThisClassFields;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        if (clazz == null)
            return false;
        if (clazz.equals(excludedThisClass))
            return true;
        return shouldSkipClass(clazz.getSuperclass());
    }

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return f.getDeclaringClass().equals(excludedThisClassFields);
    }
}
