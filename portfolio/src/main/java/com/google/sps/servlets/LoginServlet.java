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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import com.google.gson.Gson;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    UserService userService = UserServiceFactory.getUserService();
    ArrayList<String> strResponse = new ArrayList<>();

    if (userService.isUserLoggedIn()) {
      String userEmail = userService.getCurrentUser().getEmail();
      String urlToRedirectToAfterUserLogsOut = "/";
      String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);

      strResponse.add("True");
      strResponse.add("<p>Hello " + userEmail + "!</p>");
      strResponse.add("<p>Logout <a href=\"" + logoutUrl + "\">here</a>.</p>");
    } else {
      String urlToRedirectToAfterUserLogsIn = "/";
      String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);

      strResponse.add("False");
      strResponse.add("<p>You must login to create quotes.</p>");
      strResponse.add("<p>Login <a href=\"" + loginUrl + "\">here</a>.</p>");
    }

    // Set response and return JSON.
      response.setContentType("text/json;");
      String json = convertToJsonUsingGson(strResponse);
      response.getWriter().println(json);
  }
 
  /* 
   * Converts ArrayList of Strings to JSON using GSON. 
   */
  private String convertToJsonUsingGson(ArrayList<String> stringResponse) {
    Gson gson = new Gson();
    String json = gson.toJson(stringResponse);
    return json;
  }
}