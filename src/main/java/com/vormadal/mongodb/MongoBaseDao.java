package com.vormadal.mongodb;

import com.mongodb.WriteResult;
import com.vormadal.mongodb.exceptions.MorphiaException;
import com.vormadal.mongodb.models.BaseDto;
import com.vormadal.mongodb.models.ListWithTotal;
import com.vormadal.mongodb.models.Order;
import com.vormadal.mongodb.options.DaoOptions;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.aggregation.AggregationPipeline;
import org.mongodb.morphia.query.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created: 21-09-2018
 * author: Runi
 */

public abstract class MongoBaseDao<T extends BaseDto> implements BaseDao<T> {

    protected Class<T> type;
    private DbProvider provider;

    private final String wildcardChar;


    public MongoBaseDao(DbProvider provider, Class<T> type) {
        this.provider = provider;
        this.type = type;
        DaoOptions daoOptions = provider.getDaoOptions();
        this.wildcardChar = (daoOptions == null) ? "*" : daoOptions.getWildcardChar();
    }

    protected Datastore ds() {
        return provider.getDb().getDatastore();
    }

    protected Query<T> query() {
        return ds().createQuery(type);
    }

    protected UpdateOperations<T> updateOperation() {

        return ds().createUpdateOperations(type);
    }

    protected AggregationPipeline aggregation() {
        return ds().createAggregation(type);
    }

    public long getCount(){
        return ds().getCount(type);
    }

    public T create(T element) throws MorphiaException {
        ds().save(element);
        return element;
    }

    public List<T> createMultiple(List<T> elements) throws MorphiaException {
        ds().save(elements);
        return elements;
    }

    public abstract T update(T element) throws MorphiaException;

    public T updateFields(Collection<String> fields, T element) throws MorphiaException{
        Query<T> query = query().field("_id").equal(element.getId());
        UpdateOperations<T> operations = updateOperation();
        for(String field : fields){
            try {
                String methodName = "get" + field.substring(0,1).toUpperCase() + field.substring(1);
                Method method = element.getClass().getMethod(methodName);
                Object value = method.invoke(element);
                if(value == null) operations.unset(field);
                else operations.set(field, value);
            } catch (NoSuchMethodException e) {
                throw new MorphiaException("The field '" + field + "' does not exist");
            } catch (IllegalAccessException e) {
                throw new MorphiaException("Illegal access to field '" + field + "'.");
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        ds().update(query, operations);
        return element;
    }

    public T updateAll(T element) throws MorphiaException {
        if (element == null) {
            throw new MorphiaException("element can not be null");
        }
        //this does not work when using the @Version annotation
        //UpdateResults results = ds().updateFirst(query().field("_id").equal(element.getId()), element, false);
        /*if (!results.getUpdatedExisting()) {
            throw new MorphiaException("element of type " + type.getName()
                    + " and _id: " + element.getId()
                    + " does not exist");
        }*/
        ds().save(element);
        return element;
    }

    public List<T> updateMultiple(List<T> elements) throws MorphiaException {
        ds().save(elements);
        return elements;
    }

    public T get(String id) throws MorphiaException, ValidationException {
        return ds().get(type, id);
    }

    public T get(ObjectId id) throws MorphiaException, ValidationException {
        return get(id.toString());
    }

    @SuppressWarnings("unchecked")
    public ListWithTotal<T> getByFieldsAnd(Map<String, String> fields, int page, int noPrPage, String orderBy, Order order) {
        Query query = query();

        for (Map.Entry<String, String> entry : fields.entrySet()) {
            String field = entry.getKey();
            String value = entry.getValue();
            boolean wildcardStart = value.startsWith(wildcardChar);
            boolean wildcardEnd = value.endsWith(wildcardChar);

            if (wildcardStart && wildcardEnd) {
                query.field(field).containsIgnoreCase(value);
            } else if (wildcardStart) {
                query.field(field).endsWithIgnoreCase(value);
            } else if (wildcardEnd) {
                query.field(field).startsWithIgnoreCase(value);
            } else {
                query.field(field).equalIgnoreCase(value);
            }
        }

        long count = query.count();
        List<T> elements = query
                .order(getOrder(orderBy, order))
                .asList(new FindOptions().limit(noPrPage).skip(page * noPrPage));
        return new ListWithTotal<>(elements, count);
    }

    public ListWithTotal<T> getByFieldsOr(Map<String, String> fields, int page, int noPrPage, String orderBy, Order order, boolean wildcardSearch) {
        Query<T> query = query();

        Criteria[] criteria = new Criteria[fields.size()];
        if (wildcardSearch) {
            int i = 0;
            for (Map.Entry<String, String> entry : fields.entrySet()) {
                String field = entry.getKey();
                String value = entry.getValue();
                boolean wildcardStart = value.startsWith(wildcardChar);
                boolean wildcardEnd = value.endsWith(wildcardChar);
                value = value.replace(wildcardChar, "");

                if (wildcardStart && wildcardEnd) {
                    criteria[i] = query.criteria(field).containsIgnoreCase(value);
                } else if (wildcardStart) {
                    criteria[i] = query.criteria(field).endsWithIgnoreCase(value);
                } else if (wildcardEnd) {
                    criteria[i] = query.criteria(field).startsWithIgnoreCase(value);
                } else {
                    criteria[i] = query.criteria(field).equalIgnoreCase(value);
                }
                i++;
            }
        } else {
            int i = 0;
            for (Map.Entry<String, String> entry : fields.entrySet()) {
                criteria[i] = query.criteria(entry.getKey()).containsIgnoreCase(entry.getValue());
                i++;
            }
        }
        query.or(criteria);
        long count = query.count();
        List<T> elements = query
                .order(getOrder(orderBy, order))
                .asList(new FindOptions().limit(noPrPage).skip(page * noPrPage));
        return new ListWithTotal<>(elements, count);
    }

    public List<T> multiGet(Collection<String> ids) throws MorphiaException, ValidationException {
        return ds().get(type, ids).asList();
    }

    public List<T> getAll() throws MorphiaException {
        return query().asList();
    }

    public ListWithTotal<T> getAllWithTotal(int page, int size, String orderBy, Order order) {
        Query<T> query = query();
        List<T> list = query.order(getOrder(orderBy, order)).asList(new FindOptions().skip(page*size).limit(size));
        return new ListWithTotal<>(list, query.count());
    }

    public boolean delete(String oid) throws MorphiaException, ValidationException {
        WriteResult result = ds().delete(type, oid);
        return result.getN() > 0;
    }

    public boolean deleteMultiple(List<String> ids) throws MorphiaException, ValidationException {
        WriteResult result = ds().delete(type, ids);
        return result.getN() > 0;
    }

    private Sort getOrder(String field, Order order){
        switch (order){
            case DESC:
                return Sort.descending(field);
            case ASC:
            default:
                return Sort.ascending(field);
        }
    }
}
