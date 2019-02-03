package com.vormadal.annotation.tester.models;

import com.vormadal.mongodb.annotations.MongoDto;
import lombok.Data;

/**
 * <p>Created: 31-01-2019</p>
 * <p>author: Runi</p>
 */
@MongoDto
@Data
public class BaseDto {
    private String id;
}
