package com.vormadal.annotation.tester.models;

import com.vormadal.mongodb.annotations.MongoDto;
import lombok.Data;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.PrePersist;
import org.mongodb.morphia.annotations.Version;

import java.util.Date;

/**
 * Created by Christian on 01-11-2017.
 */
@MongoDto
@Data
public abstract class BaseDto implements com.vormadal.mongodb.models.BaseDto {

    @SuppressWarnings("WeakerAccess")
    public BaseDto(){
        this.setId(new ObjectId().toString());
        /*this.setId(UUID.randomUUID().toString());*/
    }

    @Id
    private String id = new ObjectId().toString();//UUID.randomUUID().toString();

    @Version
    private long v;

    private Date created;
    private String createdBy;

    private Date modified;
    private String modifiedBy;

    @PrePersist
    public void prePersist(){
        modified = new Date();
        if(created == null) created = modified;
    }

}
