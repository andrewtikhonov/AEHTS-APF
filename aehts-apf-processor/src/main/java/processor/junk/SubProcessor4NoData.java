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
package processor.junk;

import db.DAL;
import db.DALDefaults;
import db.DataConst;
import db.data.SubmDataDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processor.util.MClient;
import processor.factory.RCloudFactory;
import processor.handler.SubProcessorBase;
import processor.util.Util;

import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 29/08/2012
 * Time: 16:57
 * To change this template use File | Settings | File Templates.
 */
public class SubProcessor4NoData extends SubProcessorBase {

    private final static Logger log = LoggerFactory.getLogger(SubProcessor4NoData.class);

    private void notifyNoData(SubmDataDB subm){

        try {

            MClient client = new MClient();

            String subj   = "Submission " + subm.getSubmissionId() + " has No Data";
            String text   =
                    "Submission:\t " + subm.getSubmissionId() + "<br>" +
                    "Experiment:\t " + subm.getExperimentId() + "<br>" +
                    "Status:\t " + subm.getStatus() + "<br><br>" +
                    "Submitter:\t " + subm.getSubmitter() + "<br>" +
                    "Submitted:\t " + subm.getSubmitTime() + "<br>" +
                    "Started:\t " + subm.getStartTime() + "<br>" +
                    "Stopped:\t " + subm.getFinishTime() + "<br><br>" +

                    "AEHTS Automatic Framework";

            String from   = "rcloud@ebi.ac.uk";

            // get arrays
            String [] to = new String[]{ "andrew@ebi.ac.uk" };
            String [] a  = new String[]{};

            client.sendMail( from, to, subj, text, a );

        } catch ( Exception e ) {
            log.error("Error!", e);
        }

        log.info("sent e-mail...");
    }

    public void processOneNoDataSubmission(SubmDataDB subm, DAL dal) throws Exception {

        notifyNoData(subm);
        dal.updateSubmNotified(subm);

        // say something
        //
        log.info("Finished processing for {}:{}", subm.getSubmissionId(), subm.getExperimentId());
    }


    public void processNoDataSubmissions() throws Exception {

        log.info("Processing NoData Submissions");

        // get register DAL
        //
        DAL dal = DALDefaults.getDAL();

        // get settings
        //
        Vector<SubmDataDB> toprocess = dal.listSubms(
                Util.makeStringMap(
                        SubmDataDB.STATUS, DataConst.COMPUTED,
                        SubmDataDB.NOTIFIED, "0"));


        RCloudFactory rFactory = null;

        // anything to process ?
        if (toprocess.size() > 0) {

            log.error(toprocess.size() + " NoData submissions found");

            // process
            //
            for (SubmDataDB subm : toprocess) {
                //
                //
                processOneNoDataSubmission(subm, dal);

            }
        } else {
            log.info("No NoData submissions found");
        }
    }


    public static void main(String[] args) {

        try {
            new SubProcessor4NoData().processNoDataSubmissions();
        } catch (Exception ex) {
            log.error("Error!", ex);
        }
    }

}
