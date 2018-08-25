package treasurehunt.sqlite;

import java.io.IOException;

import treasurehunt.model.RunThrough;
import treasurehunt.model.marshalling.JsonObjectMapperBuilder;

public class RunThroughPersistentFactory implements PersistentObjectFactory {

    @Override
    public PersistentObject makePersistentObject() {
        PersistentObject<RunThrough> result = new PersistentObject<RunThrough>();
        result.tableName = "runThroughLite";
        result.idKeyName = "id_runthrough";
        result.serialisationKeyName = "string_runthrough";
        return result;
    }

    @Override
    public PersistentObject makePersistentObject(String id,Object object) {
        PersistentObject<RunThrough> result = makePersistentObject();
        result.id = id;
        result.setObject((RunThrough) object);
        return result;
    }

    @Override
    public PersistentObject makePersistentObject(String id, String objectSerialisation) {
        try {
            return makePersistentObject(id,
                    JsonObjectMapperBuilder.buildJacksonObjectMapper().readValue(objectSerialisation,RunThrough.class));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
