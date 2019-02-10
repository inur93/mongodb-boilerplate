package com.vormadal.annotation.tester.models;

import com.vormadal.mongodb.annotations.FieldsClass;
import com.vormadal.mongodb.models.HasId;
import lombok.Data;
import org.bson.types.ObjectId;
import xyz.morphia.annotations.Id;
import xyz.morphia.annotations.PrePersist;
import xyz.morphia.annotations.Version;

import java.util.Date;

/**
 * Created by Christian on 01-11-2017.
 */
@FieldsClass
@Data
public abstract class BaseDto implements HasId {

    @Id
    private ObjectId id;

    public String getId(){
        if(this.id == null) return null;
        return id.toString();
    }

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
