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

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private ArrayList<String> quotes = new ArrayList<>();
  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      // Initialize all quotes and copy local quotes into allQuotes
      ArrayList<String> allQuotes = new ArrayList<>();

      Query query = new Query("comment").addSort("content", SortDirection.ASCENDING);
      PreparedQuery results = datastore.prepare(query);

      for (Entity entity : results.asIterable()) {
          String curr = (String) entity.getProperty("content");
          allQuotes.add(curr);
      }

      response.setContentType("text/json;");
      String json = convertToJsonUsingGson(allQuotes);
      response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String comment = request.getParameter("comment");
      quotes.add(comment);

      response.setContentType("text/html");
      response.getWriter().println("Thanks for your input.");

      Entity commentEntity = new Entity("comment");
      commentEntity.setProperty("content", comment);

      datastore.put(commentEntity);

      // Redirect back to the HTML page.
      response.sendRedirect("/index.html");
  }

  private String convertToJsonUsingGson(ArrayList<String> quotes) {
    Gson gson = new Gson();
    String json = gson.toJson(quotes);
    return json;
  }
}


