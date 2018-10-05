package com.vormadal.mongodb;

import com.vormadal.mongodb.options.DaoOptions;

/**
 * Created: 21-09-2018
 * author: Runi
 */

public interface DbProvider {
    MorphiaHandler getDb();
    DaoOptions getDaoOptions();
}
