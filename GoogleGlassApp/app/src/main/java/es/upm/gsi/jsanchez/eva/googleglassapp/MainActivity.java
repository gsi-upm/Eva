package es.upm.gsi.jsanchez.eva.googleglassapp;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Context;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


import com.customlbs.library.IndoorsException;
import com.customlbs.library.IndoorsFactory;
import com.customlbs.library.IndoorsLocationListener;
import com.customlbs.library.callbacks.LoadingBuildingStatus;
import com.customlbs.library.callbacks.RoutingCallback;
import com.customlbs.library.model.Building;
import com.customlbs.library.model.Zone;
import com.customlbs.shared.Coordinate;
import com.customlbs.surface.library.IndoorsSurfaceFactory;
import com.customlbs.surface.library.IndoorsSurfaceFragment;

import com.customlbs.surface.library.ViewMode;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.view.WindowUtils;

import android.view.MotionEvent;

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
    private GestureDetector mGestureDetector;

    private final int X_OFFSET = 12;    //Offset to adjust the coordinates from the app to UbikSim
    private final int Y_OFFSET = 5;     //Offset to adjust the coordinates from the app to UbikSim
    private final int X_AXIS_CHANGE = 203;    //Offset to adjust the X axis in the app, Y axis in UbikSim
    private int contador = 0;

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
        mGestureDetector = createGestureDetector(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);
		IndoorsFactory.Builder indoorsBuilder = new IndoorsFactory.Builder();
		IndoorsSurfaceFactory.Builder surfaceBuilder = new IndoorsSurfaceFactory.Builder();
		indoorsBuilder.setContext(this);

		indoorsBuilder.setApiKey("2d890f28-cc8d-42fb-a8e9-21e49974c329");

		// our cloud using the MMT
		indoorsBuilder.setBuildingId((long) 466835013);
		// callback for indoo.rs-events
		indoorsBuilder.setUserInteractionListener(this);
       // indoorsBuilder.setEvaluationMode(true);
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
        Log.d("ADebugableExit", yard);
        if (yard.equals("YARD1")) return new Coordinate (120914,25730,0);
        if (yard.equals("YARD2")) return new Coordinate (70065,277974,0);
        if (yard.equals("YARD3")) return new Coordinate (34031,90550,0);
        return new Coordinate(98081,150448,0);
    }

	public void buildingLoaded(Building building) {
		// indoo.rs SDK successfully loaded the building you requested and
		// calculates a position now

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

    private GestureDetector createGestureDetector(Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);

        //Create a base listener for generic gestures
        gestureDetector.setBaseListener( new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if (gesture == Gesture.TAP) {
                  indoorsFragment.getIndoorsSurface().scrollBy(0,50);
                    return true;
                } else if (gesture == Gesture.SWIPE_UP) {
                    indoorsFragment.getIndoorsSurface().scrollBy(0,-50);
                    return true;
                } else if (gesture == Gesture.SWIPE_RIGHT) {
                    indoorsFragment.getIndoorsSurface().zoomIn();
                    return true;
                } else if (gesture == Gesture.SWIPE_LEFT) {
                    indoorsFragment.getIndoorsSurface().zoomOut();
                    return true;
                } else if (gesture == Gesture.LONG_PRESS) {
                    openOptionsMenu();
                    return true;
                } else if (gesture == Gesture.SWIPE_DOWN){
                    finish();
                }
                return false;
            }
        });

        gestureDetector.setFingerListener(new GestureDetector.FingerListener() {
            @Override
            public void onFingerCountChanged(int previousCount, int currentCount) {
                // do something on finger count changes
            }
        });

        gestureDetector.setScrollListener(new GestureDetector.ScrollListener() {
            @Override
            public boolean onScroll(float displacement, float delta, float velocity) {
                // do something on scrolling
                return true;
            }
        });

        return gestureDetector;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (mGestureDetector != null) {
            return mGestureDetector.onMotionEvent(event);
        }
        return false;
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu){
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS || featureId ==  Window.FEATURE_OPTIONS_PANEL) {
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        return super.onCreatePanelMenu(featureId, menu);
    }


    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS || featureId ==  Window.FEATURE_OPTIONS_PANEL) {
            switch (item.getItemId()) {
                case R.id.closestExit:
                    way=0;
                    break;
                case R.id.safestExit:
                    way=1;
                    break;
                case R.id.leastCrowdedExit:
                    way=2;

                    break;
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }


    class UpdatePosition extends AsyncTask<String, Void, JSONObject> {

        private String url = "http://lab.gsi.dit.upm.es/UbikSimMOSI-AGIL-Server/ubiksim?position=(";
        private String url_play ="http://lab.gsi.dit.upm.es/UbikSimMOSI-AGIL-Server/ubiksim?control=play";
        private String url_goal = "http://lab.gsi.dit.upm.es/UbikSimMOSI-AGIL-Server/ubiksim?position=";
        JSONObject json = new JSONObject();


        protected JSONObject doInBackground(String... passing) {
            if (isCancelled()) {
                return null;
        }
            if(!isCancelled()){

            Log.d("ADebugable", url + "GoogleGlassUser" + id + "," + passing[1] + "," + passing[0] + ")");

            HttpClient client = new DefaultHttpClient();
            HttpGet request2 = new HttpGet(url_play);


            try {
                HttpResponse response2 = client.execute(request2);
                response2.getEntity().consumeContent();

                JSONParser jParser = new JSONParser();
                // Getting JSON from URL
                JSONObject json = jParser.getJSONFromUrl(url + "GoogleGlassUser" + id + "," + passing[1] + "," + passing[0] + ")");


                return json;

            }catch (Exception e){
                e.printStackTrace();
            }

            return json;}
        return null;}


        @Override

        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}

        @Override
        protected void onPostExecute(JSONObject json) {
            if(isCancelled()) return;
            if(!isCancelled()) {
                try {
                    // Getting JSON Array
                    if (json != null) {

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

                        closestExit = getMinExit(distanceYARD1, distanceYARD2, distanceYARD3);
                        safestExit = getMaxExit(distanceToEmergencyYARD1, distanceToEmergencyYARD2, distanceToEmergencyYARD3);
                        leastLoadedExit = getMinExit(loadYARD1, loadYARD2, loadYARD3);
                        this.cancel(true);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
