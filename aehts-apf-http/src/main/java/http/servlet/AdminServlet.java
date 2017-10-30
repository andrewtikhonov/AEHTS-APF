/**
 * Copyright 2012-2017 Functional Genomics Development Team, European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * @author Andrew Tikhonov <andrew.tikhonov@gmail.com>
 **/
package http.servlet;

import http.util.ServletUtil;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 29/03/2012
 * Time: 15:30
 * To change this template use File | Settings | File Templates.
 */
public class AdminServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

    public AdminServlet() {
   		super();
   	}

   	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
   		doAny(request, response);
   	}

   	protected void doAny(final HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
   		for (Object key : request.getParameterMap().keySet()) {
   			System.setProperty((String) key, request.getParameter((String) key));
   		}
   		ServletOutputStream out = response.getOutputStream();
   		out.println("<html>\n<head>\n</head>\n<body>\n");
   		out.println("-----------------<BR>");
   		out.println("<strong>System Properties:</strong><BR>");
   		out.println("-----------------<BR>");
   		for (Object key : ServletUtil.orderO(System.getProperties().keySet())) {
   			out.println("<strong>" + key + "</strong> = " + System.getProperty((String) key) + "<BR>");
   		}
   		out.println("-----------------<BR>");
   		out.println("<strong>Environment Variables:</strong><BR>");
   		out.println("-----------------<BR>");
   		for (String key : ServletUtil.orderS(System.getenv().keySet())) {
   			out.println("<strong>" + key + "</strong> = " + System.getenv(key) + "<BR>");
   		}
   		out.println("<body><html>\n");
   		out.flush();
   	}

}
