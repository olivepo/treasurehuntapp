package treasurehunt.sqlite;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import com.example.treasurehuntapp.client.AppContext;

import java.util.ArrayList;

import treasurehunt.client.CourseRESTMethods;
import treasurehunt.client.RunThroughRESTMethods;
import treasurehunt.model.Course;
import treasurehunt.model.RunThrough;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private static boolean connectivityFirstConnect=true;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null) {
            if(connectivityFirstConnect) {
                if (activeNetInfo.isConnected()) {
                    LocalDataSender sender = new LocalDataSender(context);
                    sender.execute();
                }
                connectivityFirstConnect = false;
            }
        }
        else {
            connectivityFirstConnect= true;
        }

    }

    class LocalDataSender extends AsyncTask<Void,Void,Void> {

        private Context context;

        public LocalDataSender(Context c) {
            context = c;
        }

        @Override
        protected Void doInBackground(Void... voids) {// pousser les éventuels enregistrements locaux vers le serveur lors du rétablissmeent de la connection internet
            PersistenceManager pm = new PersistenceManager(context);
            AppContext appContext = AppContext.getInstance(context);
            ArrayList<PersistentObject<Course>> courseList = pm.getObjects(new CoursePersistentFactory());
            for (PersistentObject<Course> coursePO : courseList) {
                try {
                    if (coursePO.toUpdate && CourseRESTMethods.put(appContext.getRequestQueue(),coursePO.getObject())) {
                        pm.deleteObject(coursePO);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            ArrayList<PersistentObject<RunThrough>> runThroughList = pm.getObjects(new RunThroughPersistentFactory());
            for (PersistentObject<RunThrough> runThroughPO : runThroughList) {
                try {
                    if (runThroughPO.toUpdate && RunThroughRESTMethods.put(appContext.getRequestQueue(),runThroughPO.getObject())) {
                        pm.deleteObject(runThroughPO);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return null;
        }


    }

}