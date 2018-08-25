package treasurehunt.sqlite;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.treasurehuntapp.client.AppContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import treasurehunt.model.marshalling.JsonObjectMapperBuilder;

public class PersistenceManager {

    private MySQLite maBaseSQLite; // notre gestionnaire du fichier SQLite
    private SQLiteDatabase db;
    private static NetworkChangeReceiver networkChangeReceiver;

    // Constructeur
    public PersistenceManager(Context context)
    {
        maBaseSQLite = MySQLite.getInstance(context);
        //on ouvre la table en lecture/écriture
        db = maBaseSQLite.getWritableDatabase();
        if (networkChangeReceiver == null) {
            networkChangeReceiver = new NetworkChangeReceiver();
        }
    }

    /*public void finalize()
    {
        //on ferme l'accès à la BDD
        db.close();
    }*/

    public boolean insertObject(PersistentObject po) {
        // Ajout d'un enregistrement dans la table

        ContentValues values = new ContentValues();
        values.put(po.serialisationKeyName, po.getObjectSerialisation());

        // insert() retourne l'id du nouvel enregistrement inséré, ou -1 en cas d'erreur
        return (-1 != db.insert(po.tableName,null,values));
    }

    public boolean updateObject(PersistentObject po) {
        // modification d'un enregistrement
        // valeur de retour : (int) nombre de lignes affectées par la requête

        ContentValues values = new ContentValues();
        values.put(po.serialisationKeyName, po.getObjectSerialisation());

        String where = po.idKeyName+" = ?";
        String[] whereArgs = {po.id+""};

        return (1 == db.update(po.tableName, values, where, whereArgs));
    }

    public int deleteObject(PersistentObject lpo) {
        // suppression d'un enregistrement
        // valeur de retour : (int) nombre de lignes affectées par la clause WHERE, 0 sinon

        String where = lpo.serialisationKeyName+" = ?";
        String[] whereArgs = {lpo.id+""};

        return db.delete(lpo.tableName, where, whereArgs);
    }

    public boolean getObject(PersistentObject po,String id) {
        // Retourne la course dont l'id est passé en paramètre
        po.id = id;
        Cursor c = db.rawQuery(po.getSelectRecordQuery(), null);
        if (c.moveToFirst()) {
            po.setObject(c.getString(c.getColumnIndex(po.serialisationKeyName)));
            c.close();
            return true;
        } else {
            return false;
        }
    }

    public boolean insertOrUpdateObject(PersistentObject po) {
        if (updateObject(po)) {
            return true;
        } else {
            return insertObject(po);
        }
    }

    public <T> ArrayList<PersistentObject<T>> getObjects(PersistentObjectFactory factory) {
        // sélection de tous les enregistrements de la table
        PersistentObject referenceObject = factory.makePersistentObject();
        ArrayList<PersistentObject<T>> result = new ArrayList<PersistentObject<T>>();
        Cursor c = db.rawQuery(referenceObject.getSelectAllRecordsQuery(), null);
        if (c.moveToFirst()) {
            PersistentObject currentObject;
            do {
                    currentObject = factory.makePersistentObject(c.getString(c.getColumnIndex(referenceObject.idKeyName)),
                            c.getString(c.getColumnIndex(referenceObject.serialisationKeyName)));
                    result.add(currentObject);
            }
            while (c.moveToNext());
        }

        return result;
    }

}

