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

    /**
     *
     * @return the count of this collection
     */
    long getCount();

    /**
     * @param element to be created
     * @return the created element with an id
     * @throws MorphiaException if element already has an id.
     * If the id already exists this method could override an existing element
     */
    T create(T element) throws MorphiaException;

    /**
     *
     * @param elements to be created
     * @return the created elements with ids attached
     * @throws MorphiaException if any of the elements already have an id.
     */
    List<T> createMultiple(List<T> elements) throws MorphiaException;

    /**
     *
     * @param id
     * @return
     * @throws MorphiaException
     * @throws ValidationException
     */
    T get(String id) throws MorphiaException, ValidationException;

    /**
     *
     * @param fields
     * @param page
     * @param noPrPage
     * @param orderBy
     * @param order
     * @return
     */
    ListWithTotal<T> getByFieldsAnd(Map<String, String> fields, int page, int noPrPage, String orderBy, Order order);

    /**
     *
     * @param fields
     * @param page
     * @param noPrPage
     * @param orderBy
     * @param order
     * @param wildcardSearch
     * @return
     */
    ListWithTotal<T> getByFieldsOr(Map<String, String> fields, int page, int noPrPage, String orderBy, Order order, boolean wildcardSearch);

    /**
     *
     * @param ids
     * @return
     * @throws MorphiaException
     * @throws ValidationException
     */
    List<T> multiGet(Collection<String> ids) throws MorphiaException, ValidationException;

    /**
     *
     * @return
     * @throws MorphiaException
     */
    List<T> getAll() throws MorphiaException;

    /**
     *
     * @param page
     * @param size
     * @param orderBy
     * @param order
     * @return
     */
    ListWithTotal<T> getAllWithTotal(int page, int size, String orderBy, Order order);

    /**
     *
     * @param id
     * @param element
     * @return
     * @throws MorphiaException
     */
    T update(String id, T element) throws MorphiaException;

    /**
     *
     * @param id
     * @param element
     * @param fields
     * @return
     * @throws MorphiaException
     */
    T update(String id, T element, Class fields) throws MorphiaException;

    /**
     *
     * @param id
     * @param element
     * @return
     * @throws MorphiaException
     */
    T updateAll(String id, T element) throws MorphiaException;

    /**
     *
     * @param elements
     * @return
     * @throws MorphiaException
     */
    List<T> updateMultiple(List<T> elements) throws MorphiaException;

    /**
     *
     * @param elements
     * @param fields
     * @return
     * @throws MorphiaException
     */
    List<T> updateMultiple(List<T> elements, Class fields) throws MorphiaException;

    /**
     *
     * @param id
     * @return
     * @throws MorphiaException
     * @throws ValidationException
     */
    boolean delete(String id) throws MorphiaException, ValidationException;

    /**
     *
      * @param ids
     * @return
     * @throws MorphiaException
     * @throws ValidationException
     */
    boolean deleteMultiple(List<String> ids) throws MorphiaException, ValidationException;


}

