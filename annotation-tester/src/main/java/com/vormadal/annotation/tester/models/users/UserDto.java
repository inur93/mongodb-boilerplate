package com.vormadal.annotation.tester.models.users;

import com.vormadal.mongodb.annotations.MongoDto;
import com.vormadal.mongodb.annotations.PartialDto;

import lombok.Data;
import com.vormadal.annotation.tester.models.BaseDto;
import lombok.EqualsAndHashCode;

/**
 * <p>Created: 31-01-2019</p>
 * <p>author: Runi</p>
 */
@MongoDto(partials = {
        @PartialDto(name = "CreateUserDto", includeFields = {
                "userName", "extras", "id"
        }),
        @PartialDto(name = "UpdateUserDto", excludeFields = {
                "userName", "id"
        })
}, includeInheritedFields = true)
@Data
@EqualsAndHashCode(callSuper = false)
public class UserDto extends SuperUser {

    private String userName;
    private String name;
    private BaseDto extras;

    public void computeStuff(){

    }
}
