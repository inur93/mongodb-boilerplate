package com.vormadal.mongodb.options;

/**
 * Created: 21-09-2018
 * author: Runi
 */

public class DaoOptions {

    private String wildcardChar = "*";

    public String getWildcardChar(){
        return wildcardChar == null || "".equals(wildcardChar) ? "*" : wildcardChar;
    }

    public DaoOptions wildcardChar(String ch){
        this.wildcardChar = ch;
        return this;
    }
}
