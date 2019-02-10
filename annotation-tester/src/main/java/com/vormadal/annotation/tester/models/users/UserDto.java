package com.vormadal.annotation.tester.models.users;

import com.vormadal.mongodb.annotations.FieldsClass;
import com.vormadal.mongodb.annotations.PartialClass;
import com.vormadal.mongodb.annotations.PartialClasses;

import lombok.Data;
import com.vormadal.annotation.tester.models.BaseDto;
import lombok.EqualsAndHashCode;

/**
 * <p>Created: 31-01-2019</p>
 * <p>author: Runi</p>
 */
@FieldsClass
@PartialClasses({
        @PartialClass(name = "CreateUserDto", includeFields = {
                "userName", "extras", "id"
        }),
        @PartialClass(name = "UpdateUserDto", excludeFields = {
                "userName", "id"
        })
})
@Data
@EqualsAndHashCode(callSuper = false)
public class UserDto extends SuperUser {

    private String id;
    private String userName;
    private String name;
    private BaseDto extras;

    public void computeStuff(){

    }
}
