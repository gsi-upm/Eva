<!--
Copyright (C) 2013 Google Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->
<%@ page import="com.google.api.client.auth.oauth2.Credential" %>
<%@ page import="com.google.api.services.mirror.model.Contact" %>
<%@ page import="com.google.glassware.MirrorClient" %>
<%@ page import="com.google.glassware.WebUtil" %>
<%@ page import="com.google.glassware.mongodb.dao.MongoDBUserDAO" %>
<%@ page import="com.google.glassware.mongodb.model.User" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.api.services.mirror.model.TimelineItem" %>
<%@ page import="com.google.api.services.mirror.model.Subscription" %>
<%@ page import="com.google.api.services.mirror.model.Attachment" %>
<%@ page import="com.google.glassware.MainServlet" %>
<%@ page import="org.apache.commons.lang3.StringEscapeUtils" %>
<%@ page import="com.mongodb.Mongo" %>
<%@ page import="com.mongodb.BasicDBObjectBuilder" %>
<%@ page import="com.mongodb.DB" %>
<%@ page import="com.mongodb.DBCollection" %>
<%@ page import="com.mongodb.DBCursor" %>
<%@ page import="com.mongodb.DBObject" %>
<%@ page import="com.mongodb.MongoClient" %>
<%@ page import="com.mongodb.BasicDBObject" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%@page isELIgnored="false"%>

<!doctype html>
<%
  String userId = com.google.glassware.AuthUtil.getUserId(request);
  String appBaseUrl = WebUtil.buildUrl(request, "/");

  Credential credential = com.google.glassware.AuthUtil.getCredential(userId);

  Contact contact = MirrorClient.getContact(credential, MainServlet.CONTACT_ID);

  List<TimelineItem> timelineItems = MirrorClient.listItems(credential, 3L).getItems();


  List<Subscription> subscriptions = MirrorClient.listSubscriptions(credential).getItems();
  boolean timelineSubscriptionExists = false;
  boolean locationSubscriptionExists = false;


%>
<%         

            
            MongoDBUserDAO userDAO = new MongoDBUserDAO();
            List<User> users = userDAO.readAllPerson();
            MongoClient mongo = userDAO.getClient();
            mongo.close();


  %>


<html>
<head>
 <meta name="viewport" content="width=device-width, initial-scale=1">
     <link rel="stylesheet" href="css/site.min.css">
    <link href="http://fonts.googleapis.com/css?family=Open+Sans:400,300,600,800,700,400italic,600italic,700italic,800italic,300italic" rel="stylesheet" type="text/css">
    <script type="text/javascript" src="js/site.min.js"></script>
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <link rel="stylesheet" href="./bootstrap.css" media="screen">
    <link rel="stylesheet" href="../assets/css/bootswatch.min.css">
<script src="jquery.js" type="text/javascript"></script>
<script src="main.js" type="text/javascript"></script>
<style>
#preview{
  position:absolute;
  border:1px solid #000;
  background:#333;
  padding:5px;
  display:none;
  color:#000;
  }

/*  */
</style>
</head>
<body>


    <c:url value="/adduser" var="addURL"></c:url>
    <c:url value="/editPerson" var="editURL"></c:url>

 

<!--<div class="navbar navbar-inverse navbar-fixed-top">
  <div class="navbar-inner">
    <div class="container">
      <a class="brand" href="#" >Notification launcher: Evacuation service</a>
    </div> 
  </div>
</div>-->

<nav class="navbar navbar-default">
  <div class="container-fluid">
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-2">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="#">Evacuation service: Notification launcher</a>
    </div>
  </div>
</nav>

<div class="container">
  
    <ul class="nav nav-pills">
        <li class="active"><a data-toggle="tab" href="#sectionA">Notification panel</a></li>
        <li><a data-toggle="tab" href="#sectionB" >Users</a></li>    
    </ul>

<div class="tab-content">
        <div id="sectionA" class="tab-pane fade in active">


  <hr/>

    
      <h2>Notification Panel</h2>
      <% String flash = WebUtil.getClearFlash(request);
    if (flash != null) { %>
  <div class="alert alert-dismissible alert-info">
  <button type="button" class="close" data-dismiss="alert">×</button>
  <%= StringEscapeUtils.escapeHtml4(flash) %>
 </div>
  <% } %>
<center>
      <p>Click the following button to launch an alert.</p>


<div class="btn-group-vertical">
  
    <form action="<%= WebUtil.buildUrl(request, "/main") %>" method="post">
        <input type="hidden" name="operation" value="insertFireCard">
        
       <a href="card.png" class="preview"><button id="boton" class="btn btn-primary btn-lg btn-block" type="submit"> 
    
          Launch fire alert notification</button></a>
      </form>

      <form action="<%= WebUtil.buildUrl(request, "/main") %>" method="post">
        <input type="hidden" name="operation" value="insertEarthquakeCard">
        <button id="boton" class="btn btn-primary btn-lg btn-block" type="submit">
    
          Launch earthquake alert notification</button>
      </form>
      <form action="<%= WebUtil.buildUrl(request, "/main") %>" method="post">
        <input type="hidden" name="operation" value="insertWaterCard">
        <button id="boton" class="btn btn-primary btn-lg btn-block" type="submit">
    
          Launch water leak alert notification</button>
      </form>
      <form action="<%= WebUtil.buildUrl(request, "/main") %>" method="post">
        <input type="hidden" name="operation" value="insertWarningCard">
        <button id="boton" class="btn btn-primary btn-lg btn-block" type="submit">
    
          Launch warning alert notification</button>
      </form>
      <form action="<%= WebUtil.buildUrl(request, "/main") %>" method="post">
        <input type="hidden" name="operation" value="insertGasCard">
        <button id="boton" class="btn btn-primary btn-lg btn-block" type="submit">
          
          Launch gas alert notification</button>
      </form>

     

    </div>
    </center>
  </div>
      <div id="sectionB" class="tab-pane fade">
      <hr/>
      <h2>Users</h2>
      <div class="span5">
       

        <form class="form-horizontal" action="/adduser" method="post" accept-charset="utf-8">
              <fieldset class="register-group">
              <legend>Subscription form</legend>
                  <div class="form-group">
                   <label for="name" class="col-lg-2 control-label">Name</label>
                     <div class="col-lg-10">
                       <input input type="text" class="form-control" name="name" id="name" required>
                     </div>
                  </div>
              
                  <div class="form-group">
                  <label for="email" class="col-lg-2 control-label">Email</label>
                  <div class="col-lg-10">
                  <input input type="text" class="form-control" name="email" id="email" required>
                  </div>
                  </div>
                  
                  

              </fieldset>
              <div class="form-group">
              <div class="col-lg-10 col-lg-offset-2">
              <button type="reset" class="btn btn-default">Cancel</button>
              <form action="/adduser" method="post">
                <button id="boton" class="btn btn-primary" type="submit">
                Submit</button>
                </form>
                </div>
                </div>
        </form>

        </div>
        <hr/>
        <h2>Subsriptors</h2></br>
     
           <table class="table table-striped table-hover table-bordered table-responsive">
            <tbody>
                <tr>
                    
                    <th>Name</th>
                    <th>Email</th>
                   
                    <th>Delete</th>
                </tr>
                <c:forEach items="<%= users %>" var="user">
                    
                    <c:url value="/deleteuser" var="deleteURL">
                        <c:param name="id" value="${user.id}"></c:param>
                    </c:url>
                    <tr>
                        
                        <td><c:out value="${user.name}"></c:out></td>
                        <td><c:out value="${user.email}"></c:out></td>
                        <td><a
                            href='<c:out value="${deleteURL}" escapeXml="true"></c:out>'><button id="boton" class="btn btn-danger btn-sm" type="submit">
                Delete</button></a></td>
                        
                    </tr>
                </c:forEach>
            </tbody>
        </table>

          

   

      </div>
</div>
</div>

    
     


    <center>
      
            <a href="http://www.gsi.dit.upm.es/" target="new"><img class="logo" src="http://i61.tinypic.com/23w1p5l.png" align="center"></a>
      
    </center>



<script type="text/javascript">
    $("[data-toggle=\"tooltip\"]").tooltip();
</script>
</body>
</html>
