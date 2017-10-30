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
package submitter;

import db.DAL;
import db.DALDefaults;
import db.DataConst;
import db.data.SubmDataDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 05/07/2013
 * Time: 16:57
 * To change this template use File | Settings | File Templates.
 */
public class Submitter {

    final private static Logger log = LoggerFactory.getLogger(Submitter.class);

    private static String getParameter(String paramname) throws Exception {
        String param = System.getProperty(paramname);
        if (param == null || param.length() == 0) {
            String message = "Parameter " + paramname + " is not defined";
            log.error(message);
            throw new Exception(message);
        }

        log.info(paramname + "=[" + param + "]");

        return param;
    }

    public static void main(String[] args) {

        try {

            final String accession = getParameter("accession");
            final String submitter = getParameter("submitter");
            final String rel_ver = getParameter("relver");
            final String comment = getParameter("comment");
            final String options = getParameter("options");

            StringTokenizer tokenizer = new StringTokenizer(accession);

            //final String options =
            //        encode("align", request) + "&" +
            //        encode("count", request) + "&" +
            //        encode("eset", request) + "&" +
            //        encode("reports", request);

            log.info("");

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

            log.info("Created " + cnt + " submission(s) for processing");

            System.exit(0);

        } catch(Exception ex) {

            log.error("Error!", ex);
        }
    }

}
