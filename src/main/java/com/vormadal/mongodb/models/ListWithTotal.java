package com.vormadal.mongodb.models;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created: 21-09-2018
 * author: Runi
 */
@Getter
public class ListWithTotal<T> {
    private List<T> list;
    private Long count;

    public ListWithTotal(List<T> list, Long count){
        this.list = list == null ? new ArrayList<>() : list;
        this.count = count == null ? 0 : count;
    }
    public ListWithTotal(List<T> list, long count){
        this(list, Long.valueOf(count));
    }
}
