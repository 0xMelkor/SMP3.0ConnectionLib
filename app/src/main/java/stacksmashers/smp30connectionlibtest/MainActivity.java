package stacksmashers.smp30connectionlibtest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.ion.Response;

import java.util.ArrayList;
import java.util.List;

import stacksmashers.smp30connectionlib.delegate.ODataHttpClientCallback;
import stacksmashers.smp30connectionlib.delegate.SmpConnectionEventsDelegate;
import stacksmashers.smp30connectionlib.smp.ODataHttpClient;
import stacksmashers.smp30connectionlib.smp.SmpConnection;

public class MainActivity extends AppCompatActivity{

    String TAG = getClass().getCanonicalName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //testEntityFetch();
                test();

                Log.d(TAG, "asas");
            }
        });
    }

    public void test() {

        SmpConnection connection = getConnection();
        connection.setIgnoreCookies(true);
        connection.setDelegate(new SmpConnectionEventsDelegate() {
            @Override
            public void onCredentialsRequired() {
                Log.d(TAG, "onCredentialsRequired");
            }

            @Override
            public void onLoginError(Exception e, Response<String> result) {
                Log.d(TAG, "onLoginError");
            }

            @Override
            public void onRegistrationError(Exception e, Response<String> result) {
                Log.d(TAG, "onRegistrationError");
            }

            @Override
            public void onConnectionSuccess() {
                Log.d(TAG, "onConnectionSuccess");
                fetchData();
            }

            @Override
            public void onNetworkError(Exception e, Response<String> result) {
                Log.d(TAG, "onNetworkError");
            }
        });
        connection.connect();
    }

    private void fetchData() {
        new ODataHttpClient(getConnection(), "mss")
                .with(this)
                .setDeserializer(TypeTeamMember.class, new TypeTeamMember.TypeTeamMemberDeserializer())
                .setDelegate(new ODataHttpClientCallback<TypeTeamMember1>() {
                    @Override
                    public void onErrorCallback(Exception ex, Response<JsonObject> response) {
                        Log.d(TAG, "onErrorCallback");
                    }

                    @Override
                    public void onFetchEntitySuccessCallback(TypeTeamMember1 result) {
                        Log.d(TAG, "onFetchEntitySuccessCallback");
                    }

                    @Override
                    public void onFetchEntitySetSuccessCallback(List<TypeTeamMember1> result) {
                        Log.d(TAG, "onFetchEntitySetSuccessCallback");
                    }

                }).fetchODataEntitySet("TeamSet", new TypeToken<ArrayList<TypeTeamMember>>() {}.getType());
    }


    private SmpConnection getConnection() {
        SmpConnection smpConnection = new SmpConnection().with(getApplicationContext());
        String smp_endpoint = "https://swfmmobilepp.aceaspa.it";
        String smp_appid = "swfm";
        smpConnection.setSmpEndpoint(smp_endpoint, smp_appid);
        smpConnection.setUserCredentials("TestSwfm1", "Acea2017");
        return smpConnection;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
