package treasurehunt.sqlite;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private static boolean connectivityFirstConnect=true;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null) {
            if(connectivityFirstConnect) {
                if (activeNetInfo.isConnected()) {
                    // pousser les éventuels enregistrements locaux vers le serveur lors du rétablissmeent de la connection internet
                    Toast.makeText(context, "Connexion retrouvée !", Toast.LENGTH_LONG).show();
                }
                connectivityFirstConnect = false;
            }
        }
        else {
            connectivityFirstConnect= true;
        }

    }

}