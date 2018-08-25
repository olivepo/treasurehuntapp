package treasurehunt.sqlite;

import java.io.IOException;

import treasurehunt.model.Course;
import treasurehunt.model.marshalling.JsonObjectMapperBuilder;

public class CoursePersistentFactory implements PersistentObjectFactory {

    @Override
    public PersistentObject makePersistentObject() {
        PersistentObject<Course> result = new PersistentObject<Course>();
        result.tableName = "courseLite";
        result.idKeyName = "id_course";
        result.serialisationKeyName = "string_course";
        return result;
    }

    @Override
    public PersistentObject makePersistentObject(String id,Object object) {
        PersistentObject<Course> result = makePersistentObject();
        result.id = id;
        result.setObject((Course) object);
        return result;
    }

    @Override
    public PersistentObject makePersistentObject(String id, String objectSerialisation) {
        try {
            return makePersistentObject(id,
                    JsonObjectMapperBuilder.buildJacksonObjectMapper().readValue(objectSerialisation,Course.class));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
