package treasurehunt.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLite extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "db.sqlite";
    private static final int DATABASE_VERSION = 2;
    private static MySQLite sInstance;

    public static synchronized MySQLite getInstance(Context context) {
        if (sInstance == null) { sInstance = new MySQLite(context); }
        return sInstance;
    }

    private MySQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDataBase) {
        // Création de la base de données
        // on exécute ici les requêtes de création des tables
        createCourseTable(sqLiteDataBase);
        createRunThroughTable(sqLiteDataBase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDataBase, int oldVersion, int newVersion) {
        // Mise à jour de la base de données
        // méthode appelée sur incrémentation de DATABASE_VERSION
        // on peut faire ce qu'on veut ici, comme recréer la base :
        if (newVersion >= 2 && oldVersion <= 1) {
            createRunThroughTable(sqLiteDataBase);
        }

    }

    private void createCourseTable(SQLiteDatabase sqLiteDataBase) {
        sqLiteDataBase.execSQL(new CoursePersistentFactory().makePersistentObject().getTableCreationQuery()); // création table "COURSE"
    }

    private void createRunThroughTable(SQLiteDatabase sqLiteDataBase) {
        sqLiteDataBase.execSQL(new RunThroughPersistentFactory().makePersistentObject().getTableCreationQuery()); // création table "RUNTHROUGH"
    }
}
