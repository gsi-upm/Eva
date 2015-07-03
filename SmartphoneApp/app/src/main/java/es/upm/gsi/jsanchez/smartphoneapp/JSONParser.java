package es.upm.gsi.jsanchez.smartphoneapp;

/**
 * Created by JesúsManuel on 08/06/2015.
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

import org.json.JSONObject;

import android.util.Log;

public class JSONParser {

    final String TAG = "JsonParser.java";

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    // constructor
    public JSONParser() {

    }

    public JSONObject getJSONFromUrl(String url) {

        Log.v(TAG, "Final Requsting URL is : :"+url);

        String line = "";
        String responseJsonData = null;
        JSONObject json = null;

        try {
            StringBuilder sb = new StringBuilder();
            String x = "";
            URL httpurl = new URL(url);
            URLConnection tc= httpurl.openConnection();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(tc.getInputStream()));

            if(in !=null){
                while ((line = in.readLine()) != null) {
                    sb.append(line + "\n");
                    x = sb.toString();
                }
                responseJsonData = new String(x);
                in.close();

               // Log.e(TAG, responseJsonData);

            }
        }
        catch (UnknownHostException uh){
            Log.v("NewWebHelper", "Unknown host :");
            uh.printStackTrace();
        }
        catch (FileNotFoundException e) {
            Log.v("NewWebHelper", "FileNotFoundException :");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.v("NewWebHelper", "IOException :");
            e.printStackTrace();
        }
        catch (Exception e) {
            Log.v("NewWebHelper", "Exception :");
            e.printStackTrace();
        }
        try{
            json = new JSONObject(responseJsonData);
        }catch (Exception e){
            e.printStackTrace();
        }
        return json;
    }

}
