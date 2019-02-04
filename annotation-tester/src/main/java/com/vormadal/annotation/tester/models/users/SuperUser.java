package com.vormadal.annotation.tester.models.users;

import com.vormadal.annotation.tester.models.BaseDto;
import lombok.Data;

/**
 * <p>Created: 03-02-2019</p>
 * <p>author: Runi</p>
 */
@Data
public class SuperUser extends BaseDto {
    private String mySecretField;
}
