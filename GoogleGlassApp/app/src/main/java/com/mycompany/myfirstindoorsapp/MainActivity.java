package com.mycompany.myfirstindoorsapp;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;


import com.customlbs.library.callbacks.RoutingCallback;

import com.customlbs.coordinates.GeoCoordinate;
import com.customlbs.library.IndoorsException;
import com.customlbs.library.IndoorsFactory;
import com.customlbs.library.IndoorsLocationListener;
import com.customlbs.library.callbacks.ZoneCallback;
import com.customlbs.library.model.Building;
import com.customlbs.library.model.Zone;
import com.customlbs.shared.Coordinate;
import com.customlbs.surface.library.DefaultSurfacePainterConfiguration;
import com.customlbs.surface.library.IndoorsSurfaceFactory;
import com.customlbs.surface.library.IndoorsSurfaceFragment;
import com.customlbs.surface.library.SurfacePainterConfiguration;
import com.customlbs.surface.library.ViewMode;

/**
 * Sample Android project, powered by indoo.rs :)
 *
 * @author indoo.rs | Philipp Koenig
 *
 */
public class MainActivity extends FragmentActivity implements IndoorsLocationListener {

	private IndoorsSurfaceFragment indoorsFragment;
    private double middleX15, middleY15, middleX50, middleY50;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		IndoorsFactory.Builder indoorsBuilder = new IndoorsFactory.Builder();
       // indoorsBuilder.setEvaluationMode(true);
		IndoorsSurfaceFactory.Builder surfaceBuilder = new IndoorsSurfaceFactory.Builder();
		indoorsBuilder.setContext(this);
		// TODO: replace this with your API-key
		indoorsBuilder.setApiKey("5e795acc-ba1f-4442-bc72-b8c5b8848738");
		// TODO: replace 12345 with the id of the building you uploaded to
		// our cloud using the MMT
		indoorsBuilder.setBuildingId((long) 328644119);



		// callback for indoo.rs-events
		indoorsBuilder.setUserInteractionListener(this);
		surfaceBuilder.setIndoorsBuilder(indoorsBuilder);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
        Bitmap navigationArrow = bitmap;
        Bitmap navigationPoint = bitmap;

        SurfacePainterConfiguration configuration = DefaultSurfacePainterConfiguration.getConfiguration();
        configuration.setNavigationArrow(navigationArrow);
        configuration.setNavigationPoint(navigationPoint);

        surfaceBuilder.setSurfacePainterConfiguration(configuration);

		indoorsFragment = surfaceBuilder.build();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(android.R.id.content, indoorsFragment, "indoors");



        transaction.commit();


    }

	public void positionUpdated(Coordinate userPosition, int accuracy) {
		GeoCoordinate geoCoordinate = indoorsFragment.getCurrentUserGpsPosition();
      //  indoorsFragment.setViewMode(ViewMode.LOCK_ON_ME);

          //  Coordinate start = new Coordinate(15252, 28733, 2);
            Coordinate end = new Coordinate(17451, 3563, 0);
           // Coordinate bath = new Coordinate((int)middleX15, (int)middleY15, 0);


            indoorsFragment.getIndoors().getRouteAToB(userPosition, end, new RoutingCallback() {
                @Override
                public void onError(IndoorsException arg0) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void setRoute(ArrayList<Coordinate> arg0) {
                    indoorsFragment.getSurfaceState().setRoutingPath(arg0, false);
                    indoorsFragment.updateSurface();
                }
            });

        /**if (geoCoordinate != null) {
			Toast.makeText(
			    this,
			    "User is located at " + geoCoordinate.getLatitude() + ","
			    + geoCoordinate.getLongitude(), Toast.LENGTH_SHORT).show();
		}
         */
	}

	public void buildingLoaded(Building building) {
		// indoo.rs SDK successfully loaded the building you requested and
		// calculates a position now
		Toast.makeText(
		    this,
		    "Building is located at " + building.getLatOrigin() / 1E6 + ","
		    + building.getLonOrigin() / 1E6, Toast.LENGTH_SHORT).show();

       /* indoorsFragment.getIndoors().getZones(building, new ZoneCallback() {
            @Override
            public void setZones(ArrayList<Zone> zones) {
            // TODO: replace
                for (Zone zonas : zones) {

                    System.out.println(zonas.getName());
                     if (zonas.getName().equals("Bathroom")) {
                        for (Coordinate c : zonas.getZonePoints()) {
                            middleX15 += c.x;
                            middleY15 = c.y;
                        }
                        middleX15 /= zonas.getZonePoints().size();
                        middleY15 /= zonas.getZonePoints().size();
                    }
                    if (zonas.getName().equals("Jesus")) {
                        for (Coordinate c : zonas.getZonePoints()) {
                            middleX50 += c.x;
                            middleY50 = c.y;
                        }
                        middleX50 /= zonas.getZonePoints().size();
                        middleY50 /= zonas.getZonePoints().size();
                    }

                }}
        });
        */
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

	public void loadingBuilding(int progress) {
		// indoo.rs is still downloading or parsing the requested building
	}

	public void orientationUpdated(float orientation) {
		// user changed the direction he's heading to
	}

	public void enteredZones(List<Zone> zones) {
		// user entered one or more zones
	}
}
