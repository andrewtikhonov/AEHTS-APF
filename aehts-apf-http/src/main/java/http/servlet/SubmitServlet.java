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

import db.DAL;
import db.DALDefaults;

import db.DataConst;
import db.data.SubmDataDB;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 29/03/2012
 * Time: 15:24
 * To change this template use File | Settings | File Templates.
 */
public class SubmitServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

    public SubmitServlet() {
   		super();
   	}

   	protected void doGet(HttpServletRequest request, HttpServletResponse response)
               throws ServletException, IOException {
   		doAny(request, response);
   	}

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws javax.servlet.ServletException, java.io.IOException {
        doAny(request, response);
    }

    private String encode(String name, HttpServletRequest request) {
        return name + "=" + request.getParameter(name);
    }

    protected void doAny(final HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

   		HttpSession session = null;
   		Object result = null;

        try {

            final String accession = request.getParameter("accession");
            final String submitter = request.getParameter("submitter");
            final String rel_ver = request.getParameter("relver");
            final String comment = request.getParameter("comment");

            StringTokenizer tokenizer = new StringTokenizer(accession);

            final String options =
                    encode("align", request) + "&" +
                    encode("count", request) + "&" +
                    encode("eset", request) + "&" +
                    encode("reports", request);

            DAL dal = DALDefaults.getDAL();

            int cnt = 0;

            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();

                // trim starting ","s
                while(token.startsWith(",")) {
                    token = token.substring(1);
                }

                // trim trailing ","s
                while(token.endsWith(",")) {
                    token = token.substring(0, token.length() - 1);
                }

                HashMap<String, Object> opts = new HashMap<String, Object>();

                opts.put(SubmDataDB.EXP_ID, token);
                opts.put(SubmDataDB.SUBMITTER, submitter);
                opts.put(SubmDataDB.ENSEMBL_REL_VER, rel_ver);
                opts.put(SubmDataDB.STATUS, DataConst.PROCESSING_REQUESTED);
                opts.put(SubmDataDB.USER_NOTES, comment);
                opts.put(SubmDataDB.OPTIONS, options);

                SubmDataDB submission = dal.createSubmAutoname(new SubmDataDB(opts));

                cnt++;
            }


            ServletOutputStream out = response.getOutputStream();
            out.println("<div>\n");
            out.println("Created " + cnt + " submission(s) for processing.<br>");
            out.println("</div>");
            out.flush();

        } catch(Exception ex) {
            
            ServletOutputStream out = response.getOutputStream();
            out.println("<div>\n");
            out.println("Error submitting subission to the sumission database.<br>");
            out.println("What a shame.<br>");
            out.println("</div>");
            out.flush();
        }
    }

}
