package es.upm.gsi.jsanchez.smartphoneapp;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


import com.customlbs.library.callbacks.LoadingBuildingStatus;
import com.customlbs.library.callbacks.RoutingCallback;


import com.customlbs.library.IndoorsException;
import com.customlbs.library.IndoorsFactory;
import com.customlbs.library.IndoorsLocationListener;
import com.customlbs.library.model.Building;
import com.customlbs.library.model.Zone;
import com.customlbs.shared.Coordinate;
import com.customlbs.surface.library.IndoorsSurfaceFactory;
import com.customlbs.surface.library.IndoorsSurfaceFragment;
import com.customlbs.surface.library.ViewMode;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Sample Android project, powered by indoo.rs :)
 *
 * @author indoo.rs | Philipp Koenig
 *
 */
public class MainActivity extends FragmentActivity implements IndoorsLocationListener {

    private IndoorsSurfaceFragment indoorsFragment;

    private final int X_OFFSET = 12;    //Offset to adjust the coordinates from the app to UbikSim
    private final int Y_OFFSET = 5;     //Offset to adjust the coordinates from the app to UbikSim
    private final int X_AXIS_CHANGE = 203;    //Offset to adjust the X axis in the app, Y axis in UbikSim


    private static UUID id = UUID.randomUUID();
    private static Context context = null;
    private int way = 0;

    private static String closestExit = "";
    private static String safestExit = "";
    private static String leastLoadedExit = "";
    public static final int zones = 8;
    public static int[] occupation = new int[zones];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.context = getApplicationContext();
        IndoorsFactory.Builder indoorsBuilder = new IndoorsFactory.Builder();
      //  indoorsBuilder.setEvaluationMode(true);
        IndoorsSurfaceFactory.Builder surfaceBuilder = new IndoorsSurfaceFactory.Builder();
        indoorsBuilder.setContext(this);

        indoorsBuilder.setApiKey("2d890f28-cc8d-42fb-a8e9-21e49974c329");

        // our cloud using the MMT

        indoorsBuilder.setBuildingId((long) 466835013);

        // callback for indoo.rs-events
        indoorsBuilder.setUserInteractionListener(this);
        surfaceBuilder.setIndoorsBuilder(indoorsBuilder);

        indoorsFragment = surfaceBuilder.build();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(android.R.id.content, indoorsFragment, "indoors");

        transaction.commit();


    }

    public void positionUpdated(Coordinate userPosition, int accuracy) {

        indoorsFragment.setViewMode(ViewMode.LOCK_ON_ME);

        int x = X_AXIS_CHANGE - ((userPosition.x / 1000) + X_OFFSET);
        int y = (userPosition.y / 1000) + Y_OFFSET;


        new UpdatePosition().execute("" + x, "" + y);  //, ""+id);


        Coordinate end;

        switch (way){
            case 0:
                end = getEnd(closestExit);
                break;
            case 1:
                end = getEnd(safestExit);
                break;
            case 2:
                end = getEnd(leastLoadedExit);
                break;
            default:
                end = getEnd("");
                break;
        }


       indoorsFragment.addOverlay(new SampleSurfaceOverlay());


        indoorsFragment.getIndoors().getRouteAToB(userPosition, end, new RoutingCallback() {
            @Override
            public void onError(IndoorsException arg0) {
                // TODO Auto-generated method stub
                arg0.printStackTrace();
                arg0.getErrorCode();
                Log.d("ADebugable", arg0.toString());
            }

            @Override
            public void setRoute(ArrayList<Coordinate> arg0) {
                indoorsFragment.getSurfaceState().setRoutingPath(arg0, false);
                indoorsFragment.updateSurface();

            }
        });


    }

    private Coordinate getEnd(String yard){
        if (yard.equals("YARD1")) return new Coordinate (120914,25730,0);
        if (yard.equals("YARD2")) return new Coordinate (70065,277974,0);
        if (yard.equals("YARD3")) return new Coordinate (34031,90550,0);
        return new Coordinate(98081,150448,0);
    }

    public void buildingLoaded(Building building) {
        // indoo.rs SDK successfully loaded the building you requested and
        // calculates a position now
        Toast.makeText(
                this,
                "Building is located at " + building.getLatOrigin() / 1E6 + ","
                        + building.getLonOrigin() / 1E6, Toast.LENGTH_SHORT).show();


        showDialog();

    }

    public void showDialog(){
        String[] values = {"Closest exit", "Safest exit", "Least crowded exit"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose your way")
                .setItems(values, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which){
                            case 0:
                                way = which;
                                Log.d("ADebugable", ""+which);
                                break;
                            case 1:
                                way = which;
                                Log.d("ADebugable", ""+which);
                                break;
                            case 2:
                                way = which;
                                Log.d("ADebugable", ""+which);
                                break;
                            default:
                                break;
                        }

                    }
                });
        builder.create().show();
    }

    public void onError(IndoorsException indoorsException) {
        Toast.makeText(this, indoorsException.getMessage(), Toast.LENGTH_LONG).show();
    }

    public void changedFloor(int floorLevel, String name) {
        // user changed the floor
    }

    public void leftBuilding(Building building) {
        // user left the building
    }

    public void loadingBuilding(LoadingBuildingStatus status) {
        // indoo.rs is still downloading or parsing the requested building
        int progress = status.getProgress();
    }

    public void orientationUpdated(float orientation) {
        // user changed the direction he's heading to
    }



    public void enteredZones(List<Zone> zones) {
        // user entered one or more zones
    }
/*
    public static boolean checkConn(Context ctx) {
        ConnectivityManager conMgr = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i == null)
            return false;
        if (!i.isConnected())
            return false;
        if (!i.isAvailable())
            return false;
        return true;
    }

    public static Context getAppContext() {
        return MainActivity.context;
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_chooseway:
                showDialog();
                return true;
            case R.id.action_information:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    class UpdatePosition extends AsyncTask<String, Void, JSONObject> {

        private String url = "http://lab.gsi.dit.upm.es/UbikSimMOSI-AGIL-Server/ubiksim?position=(";
        private String url_play ="http://lab.gsi.dit.upm.es/UbikSimMOSI-AGIL-Server/ubiksim?control=play";
        private String url_goal = "http://lab.gsi.dit.upm.es/UbikSimMOSI-AGIL-Server/ubiksim?position=";
        JSONObject json = new JSONObject();


        protected JSONObject doInBackground(String... passing) {

            Log.d("ADebugable", url + "SmartphoneUser" + id + "," + passing[1] + "," + passing[0] + ")");

            HttpClient client = new DefaultHttpClient();
            HttpGet request2 = new HttpGet(url_play);


            try {
                HttpResponse response2 = client.execute(request2);
                response2.getEntity().consumeContent();

                JSONParser jParser = new JSONParser();
                // Getting JSON from URL
                JSONObject json = jParser.getJSONFromUrl(url + "SmartphoneUser" + id + "," + passing[1] + "," + passing[0] + ")");


                return json;

            }catch (Exception e){
                e.printStackTrace();
            }

            return json;
        }


        @Override

        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}

        @Override
        protected void onPostExecute(JSONObject json) {

            try {
                // Getting JSON Array
                if(json!=null) {

                    JSONObject yard1 = json.getJSONObject("YARD1");
                    JSONObject yard2 = json.getJSONObject("YARD2");
                    JSONObject yard3 = json.getJSONObject("YARD3");

                    int distanceYARD1 = yard1.getInt("distance");
                    int distanceYARD2 = yard2.getInt("distance");
                    int distanceYARD3 = yard3.getInt("distance");

                    int distanceToEmergencyYARD1 = yard1.getInt("distanceToEmergency");
                    int distanceToEmergencyYARD2 = yard2.getInt("distanceToEmergency");
                    int distanceToEmergencyYARD3 = yard3.getInt("distanceToEmergency");

                    int loadYARD1 = yard1.getInt("loadOfExit");
                    int loadYARD2 = yard2.getInt("loadOfExit");
                    int loadYARD3 = yard3.getInt("loadOfExit");

                    MainActivity.closestExit = getMinExit(distanceYARD1, distanceYARD2, distanceYARD3);
                    MainActivity.safestExit = getMaxExit(distanceToEmergencyYARD1, distanceToEmergencyYARD2, distanceToEmergencyYARD3);
                    MainActivity.leastLoadedExit = getMinExit(loadYARD1, loadYARD2, loadYARD3);
                    Log.d("ADebugableExit", "Closest exit " + MainActivity.closestExit);
                    Log.d("ADebugableExit", "Safest exit " + MainActivity.safestExit);
                    Log.d("ADebugableExit", "Least loaded exit " + MainActivity.leastLoadedExit);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        private String getMinExit(int yard1, int yard2, int yard3){
            String exit = "YARD1";
            int min = yard1;
            if (yard2<min) {
                min = yard2;
                exit = "YARD2";
            }
            if (yard3<min){
                min = yard3;
                exit = "YARD3";
            }

            return exit;
        }

        private String getMaxExit(int yard1, int yard2, int yard3){
            String exit = "YARD1";
            int max = yard1;
            if (yard2>max) {
                max = yard2;
                exit = "YARD2";
            }
            if (yard3>max){
                max = yard3;
                exit = "YARD3";
            }

            return exit;
        }

    }

}





