package com.vormadal.mongodb;

import com.mongodb.MongoClient;
import com.vormadal.mongodb.exceptions.MorphiaException;
import com.vormadal.mongodb.options.DaoOptions;
import com.vormadal.mongodb.options.DbOptions;
import lombok.extern.slf4j.Slf4j;
import dev.morphia.Datastore;
import dev.morphia.Morphia;


/**
 * Created: 21-09-2018
 * author: Runi
 */
@Slf4j
public class MorphiaHandler implements DbProvider {

    private Datastore datastore;

    public MorphiaHandler() throws MorphiaException{
        this(null, new DbOptions());
    }
    public MorphiaHandler(DbOptions options) throws MorphiaException{
        this(null, options);
    }

    /**
     * Instantiates {@link MongoClient} and {@link Datastore} as well as ensuring indexes.
     * If a {@link SetupHandler} is provided its onSetup method can be used to create initial data or other setup steps that requires access to the database.
     * @param setupHandler - can be null. {@link SetupHandler}.onSetup will be called after instantiation of MongoClient and datastore.
     * @param options - can NOT be null. Can be used to provide a custom MongoClient as well as a list of packages containing models to be mapped in morphia.
     * @throws MorphiaException if the {@link MongoClient} or {@link Datastore} could not be instantiated. Or the provided ModelsPackages could not be mapped.
     */
    public MorphiaHandler(SetupHandler setupHandler, DbOptions options) throws MorphiaException {
        try {
            MongoClient client = options.getMongoClient();

            if (client == null) {
                log.warn("no mongoclient provided using default on localhost");
                client = new MongoClient();
            }

            log.info("Mapping packages... ");
            //Map DTOS
            Morphia morphia = new Morphia();
            if (options.getModelsPackages() != null) {
                for (String pack : options.getModelsPackages()) {
                    morphia.mapPackage(pack);
                }
            }
            log.info("Creating datastore for database: " + options.getDatabase());
            datastore = morphia.createDatastore(client, options.getDatabase());
            datastore.ensureIndexes();
        }catch(Exception e){
            throw new MorphiaException("Could not complete initialization of the MorphiaHandler", e);
        }
        if(setupHandler != null)setupHandler.onSetup(this);
    }

    public Datastore getDatastore() {
        return datastore;
    }


    @Override
    public MorphiaHandler getDb() {
        return this;
    }

    @Override
    public DaoOptions getDaoOptions() {
        return new DaoOptions();
    }
}
