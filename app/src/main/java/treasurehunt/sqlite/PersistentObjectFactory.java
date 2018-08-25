package treasurehunt.sqlite;

public interface PersistentObjectFactory {

    PersistentObject makePersistentObject();
    PersistentObject makePersistentObject(String id,Object object);
    PersistentObject makePersistentObject(String id ,String objectSerialisation);

}
