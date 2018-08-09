package treasurehunt.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.treasurehuntapp.createhunt.NextStepActivity;

public class CourseLiteManager {

    private static final String TABLE_NAME = "courseLite";
    public static final String KEY_ID_COURSE="id_course";
    public static final String KEY_STRING_COURSE="string_course";
    public static final String CREATE_TABLE_COURSE = "CREATE TABLE "+TABLE_NAME+
            " (" +
            " "+KEY_ID_COURSE+" TEXT primary key," +
            " "+KEY_STRING_COURSE+" TEXT" +
            ");";
    private MySQLite maBaseSQLite; // notre gestionnaire du fichier SQLite
    private SQLiteDatabase db;

    // Constructeur
    public CourseLiteManager(Context context)
    {
        maBaseSQLite = MySQLite.getInstance(context);
    }

   

    public void open()
    {
        //on ouvre la table en lecture/écriture
        db = maBaseSQLite.getWritableDatabase();
    }

    public void close()
    {
        //on ferme l'accès à la BDD
        db.close();
    }

    public long addCourse(CourseLite course) {
        // Ajout d'un enregistrement dans la table

        ContentValues values = new ContentValues();
        values.put(KEY_STRING_COURSE, course.courseString);

        // insert() retourne l'id du nouvel enregistrement inséré, ou -1 en cas d'erreur
        return db.insert(TABLE_NAME,null,values);
    }

    public int modCourse(CourseLite course) {
        // modification d'un enregistrement
        // valeur de retour : (int) nombre de lignes affectées par la requête

        ContentValues values = new ContentValues();
        values.put(KEY_STRING_COURSE, course.courseString);

        String where = KEY_ID_COURSE+" = ?";
        String[] whereArgs = {course.idCourse+""};

        return db.update(TABLE_NAME, values, where, whereArgs);
    }

    public int supCourse(CourseLite course) {
        // suppression d'un enregistrement
        // valeur de retour : (int) nombre de lignes affectées par la clause WHERE, 0 sinon

        String where = KEY_STRING_COURSE+" = ?";
        String[] whereArgs = {course.idCourse+""};

        return db.delete(TABLE_NAME, where, whereArgs);
    }

    public CourseLite getCourse(int id) {
        // Retourne la course dont l'id est passé en paramètre

        CourseLite a=new CourseLite("0","");

        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+KEY_ID_COURSE+"="+id, null);
        if (c.moveToFirst()) {
            a.idCourse=c.getString(c.getColumnIndex(KEY_ID_COURSE));
            a.courseString=c.getString(c.getColumnIndex(KEY_STRING_COURSE));
            c.close();
        }

        return a;
    }

    public Cursor getCourses() {
        // sélection de tous les enregistrements de la table
        return db.rawQuery("SELECT * FROM "+TABLE_NAME, null);
    }

} // class CourseLiteManager

