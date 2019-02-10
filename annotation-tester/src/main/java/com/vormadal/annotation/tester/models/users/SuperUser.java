package com.vormadal.annotation.tester.models.users;

import com.vormadal.annotation.tester.models.BaseDto;
import com.vormadal.mongodb.annotations.FieldsClass;
import com.vormadal.mongodb.annotations.PartialClass;
import com.vormadal.mongodb.annotations.PartialClasses;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>Created: 03-02-2019</p>
 * <p>author: Runi</p>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@FieldsClass
@PartialClasses({
        @PartialClass(name ="CreateSuperUser", includeFields = {"mySecretField"})
})
public class SuperUser extends BaseDto {
    private String mySecretField;
}
