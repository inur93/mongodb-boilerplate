package com.vormadal.mongodb;

import com.vormadal.mongodb.exceptions.MorphiaException;
import com.vormadal.mongodb.models.BaseDto;
import com.vormadal.mongodb.models.ListWithTotal;
import com.vormadal.mongodb.models.Order;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.ValidationException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created: 21-09-2018
 * author: Runi
 */

public interface BaseDao<T extends BaseDto> {

    long getCount();
    T create(T element) throws MorphiaException;
    List<T> createMultiple(List<T> elements) throws MorphiaException;

    T update(T element) throws MorphiaException;
    T updateAll(T element) throws MorphiaException;
    T updateFields(Collection<String> fields, T element) throws MorphiaException;
    List<T> updateMultiple(List<T> elements) throws MorphiaException;

    T get(String id) throws MorphiaException, ValidationException;
    T get(ObjectId id) throws MorphiaException, ValidationException;

    @SuppressWarnings("unchecked")
    ListWithTotal<T> getByFieldsAnd(Map<String, String> fields, int page, int noPrPage, String orderBy, Order order);
    @SuppressWarnings("unchecked")
    ListWithTotal<T> getByFieldsOr(Map<String, String> fields, int page, int noPrPage, String orderBy, Order order, boolean wildcardSearch);

    List<T> multiGet(Collection<String> ids) throws MorphiaException, ValidationException;
    List<T> getAll() throws MorphiaException;
    ListWithTotal<T> getAllWithTotal(int page, int size, String orderBy, Order order);

    boolean delete(String oid) throws MorphiaException, ValidationException;
    boolean deleteMultiple(List<String> ids) throws MorphiaException, ValidationException;


}

