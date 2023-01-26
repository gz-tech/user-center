package com.upuphone.cloudplatform.usercenter.common.util;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 *
 * @author hanzhumeng
 * Created: 2022/1/4
 */
public final class OrikaUtil {

    /**
     * 默认字段工厂
     */
    private static final MapperFactory MAPPER_FACTORY = new DefaultMapperFactory.Builder().build();
    /**
     * 默认字段实例
     */
    private static final MapperFacade MAPPER_FACADE = MAPPER_FACTORY.getMapperFacade();
    /**
     * 默认字段实例集合
     */
    private static final Map<String, MapperFacade> CACHE_MAPPER_FACADE_MAP = new ConcurrentHashMap<>();

    private OrikaUtil() {
    }

    /**
     * 映射实体（默认字段）
     *
     * @param data  数据对象
     * @param toObj 映射类对象
     */
    public static <E, T> void map(T data, E toObj) {
        MAPPER_FACADE.map(data, toObj);
    }

    /**
     * 映射实体（默认字段）
     *
     * @param toClass 映射类对象
     * @param data    数据（对象）
     * @return 映射类对象
     */
    public static <E, T> E map(T data, Class<E> toClass) {
        return MAPPER_FACADE.map(data, toClass);
    }

    /**
     * 映射实体（自定义配置）
     *
     * @param toClass   映射类对象
     * @param data      数据（对象）
     * @param configMap 自定义配置 key=原类型字段名 value=新类型字段名
     * @return 映射类对象
     */
    public static <E, T> E map(T data, Class<E> toClass, Map<String, String> configMap) {
        MapperFacade mapperFacade = getMapperFacade(toClass, data.getClass(), configMap);
        return mapperFacade.map(data, toClass);
    }

    /**
     * 映射集合（默认字段）
     *
     * @param toClass 映射类对象
     * @param data    数据（集合）
     * @return 映射类对象
     */
    public static <E, T> List<E> mapAsList(Collection<T> data, Class<E> toClass) {
        return MAPPER_FACADE.mapAsList(data, toClass);
    }


    /**
     * 映射集合（自定义配置）
     *
     * @param toClass   映射类
     * @param data      数据（集合）
     * @param configMap 自定义配置 key=原类型字段名 value=新类型字段名
     * @return 映射类对象
     */
    public static <E, T> List<E> mapAsList(Collection<T> data, Class<E> toClass, Map<String, String> configMap) {
        T t = data.stream().findFirst().orElseThrow(() -> new IllegalArgumentException("映射集合，数据集合为空"));
        MapperFacade mapperFacade = getMapperFacade(toClass, t.getClass(), configMap);
        return mapperFacade.mapAsList(data, toClass);
    }

    /**
     * 获取自定义映射
     *
     * @param toClass   映射类
     * @param dataClass 数据映射类
     * @param configMap 自定义配置
     * @return 映射类对象
     */
    private static <E, T> MapperFacade getMapperFacade(Class<E> toClass, Class<T> dataClass, Map<String, String> configMap) {
        String mapKey = dataClass.getCanonicalName() + "_" + toClass.getCanonicalName();
        MapperFacade mapperFacade = CACHE_MAPPER_FACADE_MAP.get(mapKey);
        if (Objects.isNull(mapperFacade)) {
            MapperFactory factory = new DefaultMapperFactory.Builder().build();
            ClassMapBuilder<T, E> classMapBuilder = factory.classMap(dataClass, toClass);
            // map key=原类型字段名 value=新类型字段名
            configMap.forEach(classMapBuilder::field);
            classMapBuilder.byDefault().register();
            mapperFacade = factory.getMapperFacade();
            CACHE_MAPPER_FACADE_MAP.put(mapKey, mapperFacade);
        }
        return mapperFacade;
    }
}