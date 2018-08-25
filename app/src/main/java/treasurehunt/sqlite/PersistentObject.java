package treasurehunt.sqlite;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import treasurehunt.model.marshalling.JsonObjectMapperBuilder;

public class PersistentObject<T> {

    public String id;
    private T object;
    public String tableName;
    public String idKeyName;
    public String serialisationKeyName;

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public void setObject(String serialisation) {
        try {
            this.object = new JsonObjectMapperBuilder().buildJacksonObjectMapper().readValue(serialisation, new TypeReference<T>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getObjectSerialisation() {
        try {
            return new JsonObjectMapperBuilder().buildJacksonObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getTableCreationQuery() {
        return String.format("CREATE TABLE %s ( %s TEXT primary key, %s TEXT);",
                tableName,
                idKeyName,
                serialisationKeyName);
    }

    public String getSelectRecordQuery() {
        return String.format("SELECT * FROM %s WHERE %s='%s';",
                tableName,
                idKeyName,
                id);
    }

    public String getSelectAllRecordsQuery() {
        return String.format("SELECT * FROM %s;",
                tableName);
    }
}
