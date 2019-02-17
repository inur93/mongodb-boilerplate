package com.vormadal.annotation.tester.database;

import com.vormadal.annotation.tester.models.BaseDto;
import com.vormadal.mongodb.DbProvider;
import com.vormadal.mongodb.MongoBaseDao;
import com.vormadal.mongodb.exceptions.MorphiaException;
import org.bson.types.ObjectId;

/**
 * <p>Created: 17-02-2019</p>
 * <p>author: Runi</p>
 */

public class MongoDao extends MongoBaseDao<BaseDto, ObjectId> {


    public MongoDao(DbProvider provider, Class<BaseDto> type) {
        super(provider, type);
    }

    @Override
    public BaseDto update(ObjectId id, BaseDto element) throws MorphiaException {
        return null;
    }
}
