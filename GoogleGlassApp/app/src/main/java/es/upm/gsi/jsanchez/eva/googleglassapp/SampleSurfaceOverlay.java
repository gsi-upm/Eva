package es.upm.gsi.jsanchez.eva.googleglassapp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;

import com.customlbs.surface.library.IndoorsSurfaceOverlay;
import com.customlbs.surface.library.IndoorsSurfaceOverlayUtil;
import com.customlbs.surface.library.SurfacePainterConfiguration;
import com.customlbs.surface.library.SurfaceState;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JesúsManuel on 20/06/2015.
 */
public class SampleSurfaceOverlay implements IndoorsSurfaceOverlay {

    private Paint paintAreas;

    private final int[] zonesVertexes =
                   {5119,44644,30859,51418,
                    20832,55418,76054,80232,
                    61550,98939,74777,281650,
                    80857,240725,151666,247013,
                    154298,97092,159348,252184,
                    125791,86309,159348,94609,
                    50866,84807,122620,93995,
                    128561,23666,172196,65328}; //All the vertexes of every rectangle (8x4 - left,top,right,bottom)



    @Override
    public void initialize(SurfacePainterConfiguration arg0) {
        paintAreas = new Paint();
        new UpdateOccupation().execute("");
    }

    @Override
    public void paint(Canvas canvas, SurfaceState state) {


        if (state.lastFloorLevelSelectedByLibrary == state.currentFloor.getLevel()) {

            IndoorsSurfaceOverlayUtil.CanvasCoordinate[] coordinates = new IndoorsSurfaceOverlayUtil.CanvasCoordinate[MainActivity.zones*2];
            int k =0;
            while(k<zonesVertexes.length-1){
                IndoorsSurfaceOverlayUtil.CanvasCoordinate coordinatex = IndoorsSurfaceOverlayUtil
                        .buildingCoordinateToCanvasAbsolute(state, zonesVertexes[k],
                                zonesVertexes[k+1]);

                coordinates[k/2]=coordinatex;
                k=k+2;
            }

            for (int i=0; i<coordinates.length; i++){
                // inside
                paintAreas = new Paint();
                paintAreas.setStyle(Paint.Style.FILL);
                paintAreas.setColor(Color.TRANSPARENT);
                canvas.drawRect(coordinates[i].x, coordinates[i].y, coordinates[i+1].x, coordinates[i+1].y, paintAreas);

                // border
                paintAreas.setStyle(Paint.Style.STROKE);
                paintAreas.setColor(getColor(i/2));
                paintAreas.setStrokeWidth(5);
                canvas.drawRect(coordinates[i].x, coordinates[i].y, coordinates[i+1].x, coordinates[i+1].y, paintAreas);


                i++;

            }

        }
    }

    @Override
    public void destroy() {
    }

    public int getColor(int zone){
        //  Log.d("ADebugableCoord", "La ocupacion de la zona "+zone+ " es "+MainActivity.occupation[zone]);
        if(MainActivity.occupation[zone]<10) return Color.GREEN;
        if(MainActivity.occupation[zone]<25) return Color.YELLOW;
        if(MainActivity.occupation[zone]<100) return Color.RED;
        else return Color.GRAY;
    }

    class UpdateOccupation extends AsyncTask<String, Void, JSONObject> {

        private String url = "http://lab.gsi.dit.upm.es/UbikSimMOSI-AGIL-Server/ubiksim?position=people";
        JSONObject json = new JSONObject();

        protected JSONObject doInBackground(String... passing) {

            try {
                JSONParser jParser = new JSONParser();
                // Getting JSON from URL
                JSONObject json = jParser.getJSONFromUrl(url);
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

                int[] occupationAux = new int[MainActivity.zones];
                // Getting JSON Array
                if(json!=null) {
                    for (int i=1; i<=100; i++){
                        if(json.has("a"+i)){
                            JSONObject agent = json.getJSONObject("a"+i);
                            String room = agent.getString("room");
                            if (agent!=null) {

                                if (room.equals("999.G8")) {
                                    int count = occupationAux[0];
                                    count++;
                                    occupationAux[0] = count;
                                }
                                if (room.equals("999.G6")) {
                                    int count = occupationAux[1];
                                    count++;
                                    occupationAux[1] = count;
                                }
                                if (room.equals("099.G7")) {
                                    int count = occupationAux[2];
                                    count++;
                                    occupationAux[2] = count;
                                }
                                if (room.equals("099.G5")) {
                                    int count = occupationAux[3];
                                    count++;
                                    occupationAux[3] = count;
                                }
                                if (room.equals("099.G2")) {
                                    int count = occupationAux[4];
                                    count++;
                                    occupationAux[4] = count;
                                }
                                if (room.equals("099.G1")) {
                                    int count = occupationAux[5];
                                    count++;
                                    occupationAux[5] = count;
                                }
                                if (room.equals("099.G4")) {
                                    int count = occupationAux[6];
                                    count++;
                                    occupationAux[6] = count;
                                }
                                if (room.equals("044.0") || room.equals("045.0") || room.equals("046.1") || room.equals("047.0") || room.equals("048.0") || room.equals("999.K")) {
                                    int count = occupationAux[7];
                                    count++;
                                    occupationAux[7] = count;
                                }
                            }
                        }
                    }//for finished

                    //Loop to asign values
                    for (int i=0; i<MainActivity.zones; i++){
                        MainActivity.occupation[i]=occupationAux[i];
                        Log.d("ADebugableCoord", "La ocupacion de la zona " + i + " es " + MainActivity.occupation[i]);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }


}

