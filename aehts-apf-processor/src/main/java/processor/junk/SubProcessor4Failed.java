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
import db.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processor.factory.RCloudFactory;
import processor.handler.SubProcessor4Computing;
import processor.handler.SubProcessorBase;
import processor.util.MClient;
import processor.util.Util;
import uk.ac.ebi.rcloud.server.RServices;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 01/05/2012
 * Time: 14:04
 * To change this template use File | Settings | File Templates.
 */
public class SubProcessor4Failed extends SubProcessorBase {

    private final static Logger log = LoggerFactory.getLogger(SubProcessor4Failed.class);

    private void notifyFailure(SubmDataDB subm){

        try {

            MClient client = new MClient();

            String subj   = "Submission " + subm.getSubmissionId() + " Failed";
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

    public void processOneFailedSubmission(SubmDataDB subm, DAL dal, RServices r) throws Exception {

        notifyFailure(subm);
        dal.updateSubmNotified(subm);


        // say something
        //
        log.info("Finished processing for {}:{}", subm.getSubmissionId(), subm.getExperimentId());
    }


    public void processFailedSubmissions() throws Exception {

        log.info("Processing Failed Submissions");

        // get register DAL
        //
        DAL dal = DALDefaults.getDAL();

        // needed for collection of PSR
        //
        SubProcessor4Computing runningProcessor = new SubProcessor4Computing();
        
        // get settings
        //
        Vector<SubmDataDB> toprocess = dal.listSubms(
                Util.makeStringMap(
                        SubmDataDB.STATUS, DataConst.COMPUTING_FAILED,
                        SubmDataDB.NOTIFIED, "0"));

            
        RCloudFactory rFactory = null;

        if (toprocess.size() > 0) {

            log.error(toprocess.size() + " failed submissions found");

            try {

                // get R
                //
                rFactory = new RCloudFactory();

                RServices r = null;
                //RServices r = rFactory.locateRServices(subm.getMasterServerName());

                r = allocateDefaultServer(rFactory);

                log.info("Located default R server");

                if (r == null) {
                    log.error("Server allocation failure cannot proceed!");
                    return;
                }

                // process
                //
                for (SubmDataDB subm : toprocess) {
                    //
                    //
                    runningProcessor.processOneComputingSubmission(subm, dal, r);
                    processOneFailedSubmission(subm, dal, r);

                }

            }finally {
                if (rFactory != null) {

                    log.info("Disconnecting from server");

                    // leave it running
                    //
                    rFactory.disconnectFromRServices();

                    log.info("Disconnected");
                }
            }

        } else {
            log.info("No failed submissions found");
        }

    }

}
