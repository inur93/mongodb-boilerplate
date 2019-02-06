package com.vormadal.mongodb;

import com.vormadal.mongodb.exceptions.MorphiaException;
import com.vormadal.mongodb.models.HasId;
import com.vormadal.mongodb.models.ListWithTotal;
import com.vormadal.mongodb.models.Order;
import xyz.morphia.query.ValidationException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created: 21-09-2018
 * author: Runi
 */
public interface BaseDao<T extends HasId> {

    long getCount();

    T create(T element) throws MorphiaException;
    List<T> createMultiple(List<T> elements) throws MorphiaException;

    T get(String id) throws MorphiaException, ValidationException;
    @SuppressWarnings("unchecked")
    ListWithTotal<T> getByFieldsAnd(Map<String, String> fields, int page, int noPrPage, String orderBy, Order order);
    @SuppressWarnings("unchecked")
    ListWithTotal<T> getByFieldsOr(Map<String, String> fields, int page, int noPrPage, String orderBy, Order order, boolean wildcardSearch);
    List<T> multiGet(Collection<String> ids) throws MorphiaException, ValidationException;
    List<T> getAll() throws MorphiaException;
    ListWithTotal<T> getAllWithTotal(int page, int size, String orderBy, Order order);

    T update(String id, T element) throws MorphiaException;
    T update(String id, T element, Class fields) throws MorphiaException;
    T updateAll(String id, T element) throws MorphiaException;

    List<T> updateMultiple(List<T> elements) throws MorphiaException;
    List<T> updateMultiple(List<T> elements, Class fields) throws MorphiaException;

    boolean delete(String id) throws MorphiaException, ValidationException;
    boolean deleteMultiple(List<String> ids) throws MorphiaException, ValidationException;


}

