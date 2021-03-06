// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/** Servlet that returns user-generated comments.*/
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  /** Retrieves user-generated comments from datastore to load onto page each time page is refreshed.
    * Includes email of user that posted the comment. */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    ArrayList<String> strResponse = new ArrayList<>();
    UserService userService = UserServiceFactory.getUserService();

    // If user is not logged in, do not display comments.
    if (!userService.isUserLoggedIn()) return;

    Query query = new Query("comment").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);

    // Copy datastore comments into strResponse
    for (Entity entity : results.asIterable()) {
      String text = (String) entity.getProperty("content");
      String user = (String) entity.getProperty("user");
      strResponse.add("<p>" + user + ": " + text + "</p>");
    }

    // Set response and return JSON.
    response.setContentType("text/json;");
    String json = convertToJsonUsingGson(strResponse);
    response.getWriter().println(json);
  }

  /** Retrieves comments from request form to store in datastore, along with the email of the user
    * that posted it. This function assumes that a user cannot post without being logged in.
    * Function finishes with a redirect call to /index.html which will effectively call doGet(). */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();

    // Retrieve comment, comment quantity, and current user.
    String comment = request.getParameter("comment");
    String email = userService.getCurrentUser().getEmail();
    Entity user = getUser(userService.getCurrentUser().getUserId());

    // Create entity for datastore.
    Entity commentEntity = new Entity("comment", user.getKey());
    commentEntity.setProperty("content", comment);
    commentEntity.setProperty("email", email);
    commentEntity.setProperty("user", (String) user.getProperty("nickname"));
    commentEntity.setProperty("timestamp", System.currentTimeMillis());

    datastore.put(commentEntity);
    response.setContentType("text/html");
    response.sendRedirect("/index.html");
  }

  /** Converts ArrayList of Strings to JSON using GSON. */
  private String convertToJsonUsingGson(ArrayList<String> comments) {
    Gson gson = new Gson();
    String json = gson.toJson(comments);
    return json;
  }

  /** Returns the entity coresponding to the current user. */
  private Entity getUser(String id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("UserInfo")
      .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
    PreparedQuery results = datastore.prepare(query);
    return results.asSingleEntity();
  }
}


