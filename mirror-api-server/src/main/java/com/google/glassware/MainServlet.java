/*
 * Copyright (C) 2013 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.glassware;



import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.mirror.model.MenuItem;
import com.google.api.services.mirror.model.NotificationConfig;
import com.google.api.services.mirror.model.TimelineItem;
import com.google.glassware.mongodb.dao.MongoDBUserDAO;
import com.google.glassware.mongodb.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles POST requests from index.jsp
 *
 * @author Jenny Murphy - http://google.com/+JennyMurphy
 */
public class MainServlet extends HttpServlet {

    /**
     * Private class to process batch request results.
     * <p/>
     * For more information, see
     * https://code.google.com/p/google-api-java-client/wiki/Batch.
     */
    private final class BatchCallback extends JsonBatchCallback<TimelineItem> {
        private int success = 0;
        private int failure = 0;

        @Override
        public void onSuccess(TimelineItem item, HttpHeaders headers) throws IOException {
            ++success;
        }

        @Override
        public void onFailure(GoogleJsonError error, HttpHeaders headers) throws IOException {
            ++failure;
            LOG.info("Failed to insert item: " + error.getMessage());
        }
    }

    // private static final Logger LOG = Logger.getLogger(MainServlet.class.getSimpleName());
    private static final Logger LOG = Logger.getLogger(AuthServlet.class.getSimpleName());
    public static final String CONTACT_ID = "com.google.glassware.contact.java-quick-start";
    public static final String CONTACT_NAME = "Java Quick Start";
    private static final String PAUSE_URL = "http://lab.gsi.dit.upm.es/UbikSimMOSI-AGIL-Server/ubiksim?control=pause";
    private static final String PLAY_URL = "http://lab.gsi.dit.upm.es/UbikSimMOSI-AGIL-Server/ubiksim?control=play";
    private static final String EMERGENCY_URL = "http://lab.gsi.dit.upm.es/UbikSimMOSI-AGIL-Server/ubiksim?position=emergency";
    public enum UbikSimStatus {
        PAUSED, STOPPED
    }

    public UbikSimStatus status = UbikSimStatus.STOPPED;

    private static final String FIRE_CARD =
            "<article class='author'>"
                    + "<img src='http://i60.tinypic.com/693plj.jpg' width='100%' height='100%'>"
                    + "<div class='overlay-full'/> "
                    + "<header>"
                    + "<img src='http://i60.tinypic.com/28wjiwh.png'/>"
                    + "<h1>Facility management team</h1>"
                    + "<h2>GSI, Madrid</h2>"
                    + "</header>"
                    + "<section>"
                    + "<p class='text-auto-size'> Please <span class='yellow'>evacuate</span> the building <span class='yellow'>immediately </span>. Access the <span class='yellow'>evacuation app </span>by tapping the touch pad.</p>"
                    + "</section>"
                    + "</article>";

    private static final String EARTHQUAKE_CARD =
            "<article class='author'>"
                    + "<img src='http://i60.tinypic.com/2ih5few.jpg' width='100%' height='100%'>"
                    + "<div class='overlay-full'/> "
                    + "<header>"
                    + "<img src='http://i61.tinypic.com/2s7xt8o.png'/>"
                    + "<h1>Facility management team</h1>"
                    + "<h2>GSI, Madrid</h2>"
                    + "</header>"
                    + "<section>"
                    + "<p class='text-auto-size'> Please <span class='yellow'>evacuate</span> the building <span class='yellow'>immediately </span>. Access the <span class='yellow'>evacuation app </span>by tapping the touch pad.</p>"
                    + "</section>"
                    + "</article>";

    private static final String WATER_LEAK_CARD =
            "<article class='author'>"
                    + "<img src='http://i58.tinypic.com/11ie6tk.jpg' width='100%' height='100%'>"
                    + "<div class='overlay-full'/> "
                    + "<header>"
                    + "<img src='http://i57.tinypic.com/2how9kx.png'/>"
                    + "<h1>Facility management team</h1>"
                    + "<h2>GSI, Madrid</h2>"
                    + "</header>"
                    + "<section>"
                    + "<p class='text-auto-size'> Please <span class='yellow'>evacuate</span> the building <span class='yellow'>immediately </span>. Access the <span class='yellow'>evacuation app </span>by tapping the touch pad.</p>"
                    + "</section>"
                    + "</article>";


    private static final String WARNING_CARD =
            "<article class='author'>"
                    + "<img src='http://i62.tinypic.com/2cxj95v.png' width='100%' height='100%'>"
                    + "<div class='overlay-full'/> "
                    + "<header>"
                    + "<img src='http://i60.tinypic.com/2zjm5ag.png'/>"
                    + "<h1>Facility management team</h1>"
                    + "<h2>GSI, Madrid</h2>"
                    + "</header>"
                    + "<section>"
                    + "<p class='text-auto-size'> Please <span class='yellow'>evacuate</span> the building <span class='yellow'>immediately </span>. Access the <span class='yellow'>evacuation app </span>by tapping the touch pad.</p>"
                    + "</section>"
                    + "</article>";

    private static final String GAS_CARD =
            "<article class='author'>"
                    + "<img src='http://i57.tinypic.com/212ua8w.png' width='100%' height='100%'>"
                    + "<div class='overlay-full'/> "
                    + "<header>"
                    + "<img src='http://i61.tinypic.com/9iajq8.png'/>"
                    + "<h1>Facility management team</h1>"
                    + "<h2>GSI, Madrid</h2>"
                    + "</header>"
                    + "<section>"
                    + "<p class='text-auto-size'> Please <span class='yellow'>evacuate</span> the building <span class='yellow'>immediately </span>. Access the <span class='yellow'>evacuation app </span>by tapping the touch pad.</p>"
                    + "</section>"
                    + "</article>";

    /**
     * Do stuff when buttons on index.jsp are clicked
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

        String userId = AuthUtil.getUserId(req);

        MongoDBUserDAO userDAO = new MongoDBUserDAO();
        List<User> users = userDAO.readAllPerson();
        List<Credential> credentials = new ArrayList<Credential>();
        for (int i=0; i<users.size(); i++){

            credentials.add(AuthUtil.newAuthorizationCodeFlow().loadCredential(users.get(i).getUserId()));


        }
    /*  LOG.info("userId2" + userId);
    Credential credential = AuthUtil.newAuthorizationCodeFlow().loadCredential(userId);
      LOG.info("credencial2" + credential);*/
        String message = "";

        if (req.getParameter("operation").equals("insertFireCard")) {
            LOG.fine("Inserting Timeline Item");
            TimelineItem timelineItem = new TimelineItem();
            timelineItem.setHtml(FIRE_CARD);
            timelineItem.setTitle("Evacuate Notification");
            timelineItem.setSpeakableText("Evacuation Alert");
            List<MenuItem> menuItemList = new ArrayList<MenuItem>();

            menuItemList.add(new MenuItem().setAction("READ_ALOUD"));
            menuItemList.add(new MenuItem().setAction("DELETE"));
            menuItemList.add(new MenuItem().setAction("OPEN_URI").setPayload("evacuapp.scheme://open/"));


            timelineItem.setMenuItems(menuItemList);

            // Triggers an audible tone when the timeline item is received
            timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));

            for(int i=0; i< credentials.size(); i++){
                MirrorClient.insertTimelineItem(credentials.get(i), timelineItem);
                LOG.info(users.get(i).getName());

            }

            // MirrorClient.insertTimelineItem(credential, timelineItem);
            //  message = userId;
            message = "A fire alert card has been inserted.";

            if (status != UbikSimStatus.PAUSED){

                URL url = new URL(PAUSE_URL);
                InputStream inputStreamPause = url.openStream();
                inputStreamPause.close();
                URL urlPlay = new URL(PLAY_URL);
                InputStream inputStreamPlay = urlPlay.openStream();
                inputStreamPlay.close();
                URL urlEmergency = new URL(EMERGENCY_URL);
                InputStream inputStreamEmergency = urlEmergency.openStream();
                inputStreamEmergency.close();

                status = UbikSimStatus.PAUSED;
            }




        }else if (req.getParameter("operation").equals("insertEarthquakeCard")) {
            LOG.fine("Inserting Timeline Item");
            TimelineItem timelineItem = new TimelineItem();
            timelineItem.setHtml(EARTHQUAKE_CARD);
            timelineItem.setTitle("Evacuate Notification");
            timelineItem.setSpeakableText("Evacuation Alert");
            List<MenuItem> menuItemList = new ArrayList<MenuItem>();

            menuItemList.add(new MenuItem().setAction("READ_ALOUD"));
            menuItemList.add(new MenuItem().setAction("DELETE"));
            menuItemList.add(new MenuItem().setAction("OPEN_URI").setPayload("evacuapp.scheme://open/"));


            timelineItem.setMenuItems(menuItemList);

            // Triggers an audible tone when the timeline item is received
            timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));


            for(int i=0; i< credentials.size(); i++){
                MirrorClient.insertTimelineItem(credentials.get(i), timelineItem);
                LOG.info(users.get(i).getName());

            }

            // MirrorClient.insertTimelineItem(credential, timelineItem);

            message = "An earthquake alert card item has been inserted.";
            if (status != UbikSimStatus.PAUSED){
                URL url = new URL(PAUSE_URL);
                InputStream inputStreamPause = url.openStream();
                inputStreamPause.close();
                URL urlPlay = new URL(PLAY_URL);
                InputStream inputStreamPlay = urlPlay.openStream();
                inputStreamPlay.close();
                URL urlEmergency = new URL(EMERGENCY_URL);
                InputStream inputStreamEmergency = urlEmergency.openStream();
                inputStreamEmergency.close();

                status = UbikSimStatus.PAUSED;
            }

        }else if (req.getParameter("operation").equals("insertWaterCard")) {
            LOG.fine("Inserting Timeline Item");
            TimelineItem timelineItem = new TimelineItem();
            timelineItem.setHtml(WATER_LEAK_CARD);
            timelineItem.setTitle("Evacuate Notification");
            timelineItem.setSpeakableText("Evacuation Alert");
            List<MenuItem> menuItemList = new ArrayList<MenuItem>();

            menuItemList.add(new MenuItem().setAction("READ_ALOUD"));
            menuItemList.add(new MenuItem().setAction("DELETE"));
            menuItemList.add(new MenuItem().setAction("OPEN_URI").setPayload("evacuapp.scheme://open/"));


            timelineItem.setMenuItems(menuItemList);

            // Triggers an audible tone when the timeline item is received
            timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));


            for(int i=0; i< credentials.size(); i++){
                MirrorClient.insertTimelineItem(credentials.get(i), timelineItem);
                LOG.info(users.get(i).getName());

            }

            // MirrorClient.insertTimelineItem(credential, timelineItem);

            message = "A water leak alert card has been inserted.";
            if (status != UbikSimStatus.PAUSED){
                URL url = new URL(PAUSE_URL);
                InputStream inputStreamPause = url.openStream();
                inputStreamPause.close();
                URL urlPlay = new URL(PLAY_URL);
                InputStream inputStreamPlay = urlPlay.openStream();
                inputStreamPlay.close();
                URL urlEmergency = new URL(EMERGENCY_URL);
                InputStream inputStreamEmergency = urlEmergency.openStream();
                inputStreamEmergency.close();

                status = UbikSimStatus.PAUSED;
            }

        }else if (req.getParameter("operation").equals("insertWarningCard")) {
            LOG.fine("Inserting Timeline Item");
            TimelineItem timelineItem = new TimelineItem();
            timelineItem.setHtml(WARNING_CARD);
            timelineItem.setTitle("Evacuate Notification");
            timelineItem.setSpeakableText("Evacuation Alert");
            List<MenuItem> menuItemList = new ArrayList<MenuItem>();

            menuItemList.add(new MenuItem().setAction("READ_ALOUD"));
            menuItemList.add(new MenuItem().setAction("DELETE"));
            menuItemList.add(new MenuItem().setAction("OPEN_URI").setPayload("evacuapp.scheme://open/"));


            timelineItem.setMenuItems(menuItemList);

            // Triggers an audible tone when the timeline item is received
            timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));

            for(int i=0; i< credentials.size(); i++){
                MirrorClient.insertTimelineItem(credentials.get(i), timelineItem);
                LOG.info(users.get(i).getName());

            }

            // MirrorClient.insertTimelineItem(credential, timelineItem);

            message = "A warning alert card has been inserted.";
            if (status != UbikSimStatus.PAUSED){
                URL url = new URL(PAUSE_URL);
                InputStream inputStreamPause = url.openStream();
                inputStreamPause.close();
                URL urlPlay = new URL(PLAY_URL);
                InputStream inputStreamPlay = urlPlay.openStream();
                inputStreamPlay.close();
                URL urlEmergency = new URL(EMERGENCY_URL);
                InputStream inputStreamEmergency = urlEmergency.openStream();
                inputStreamEmergency.close();

                status = UbikSimStatus.PAUSED;
            }

        }else if (req.getParameter("operation").equals("insertGasCard")) {
            LOG.fine("Inserting Timeline Item");
            TimelineItem timelineItem = new TimelineItem();
            timelineItem.setHtml(GAS_CARD);
            timelineItem.setTitle("Evacuate Notification");
            timelineItem.setSpeakableText("Evacuation Alert");
            List<MenuItem> menuItemList = new ArrayList<MenuItem>();

            menuItemList.add(new MenuItem().setAction("READ_ALOUD"));
            menuItemList.add(new MenuItem().setAction("DELETE"));
            menuItemList.add(new MenuItem().setAction("OPEN_URI").setPayload("evacuapp.scheme://open/"));


            timelineItem.setMenuItems(menuItemList);

            // Triggers an audible tone when the timeline item is received
            timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));


            for(int i=0; i< credentials.size(); i++){
                MirrorClient.insertTimelineItem(credentials.get(i), timelineItem);
                LOG.info(users.get(i).getName());

            }

            // MirrorClient.insertTimelineItem(credential, timelineItem);


            message = "A gas leak alert card has been inserted.";

            if (status != UbikSimStatus.PAUSED){
                URL url = new URL(PAUSE_URL);
                InputStream inputStreamPause = url.openStream();
                inputStreamPause.close();
                URL urlPlay = new URL(PLAY_URL);
                InputStream inputStreamPlay = urlPlay.openStream();
                inputStreamPlay.close();
                URL urlEmergency = new URL(EMERGENCY_URL);
                InputStream inputStreamEmergency = urlEmergency.openStream();
                inputStreamEmergency.close();

                status = UbikSimStatus.PAUSED;
            }

        } else {
            String operation = req.getParameter("operation");
            LOG.warning("Unknown operation specified " + operation);
            message = "I don't know how to do that";
        }
        WebUtil.setFlash(req, message);
        res.sendRedirect(WebUtil.buildUrl(req, "/"));
    }


}
