package com.vormadal.annotation.tester.models.users;

import com.vormadal.mongodb.annotations.MongoDto;
import com.vormadal.mongodb.annotations.PartialDto;
import lombok.Data;
import com.vormadal.annotation.tester.models.BaseDto;

/**
 * <p>Created: 31-01-2019</p>
 * <p>author: Runi</p>
 */
@MongoDto(partials = {
        @PartialDto(name = "CreateUserDto", includeFields = {
                "userName", "extras"
        }),
        @PartialDto(name = "UpdateUserDto", excludeFields = {
                "userName"
        })
})
@Data
public class UserDto extends BaseDto {

    private String userName;
    private String name;
    private BaseDto extras;

    public void computeStuff(){

    }
}
