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
package processor.handler;

import db.DAL;
import db.DALDefaults;
import db.DataConst;
import db.SettingsConst;
import db.data.SubmDataDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processor.factory.RCloudFactory;
import processor.util.RUtil;
import processor.util.TheSettings;
import processor.util.Util;
import uk.ac.ebi.rcloud.server.RServices;
import uk.ac.ebi.rcloud.server.RType.RObject;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 29/08/2012
 * Time: 16:56
 * To change this template use File | Settings | File Templates.
 */
public class SubProcessor4Computed extends SubProcessorBase {

    private final static Logger log = LoggerFactory.getLogger(SubProcessor4Computed.class);

    public void processOneProcessedSubmission(SubmDataDB subm, String afterwork, DAL dal, RServices r) throws Exception {

        // say something
        //
        log.info("Copying files for {}:{}", subm.getSubmissionId(), subm.getExperimentId());

        // update db records
        //
        subm.getMap().put(SubmDataDB.STATUS, DataConst.DELIVERING);
        subm.getMap().put(SubmDataDB.STATUS_TEXT, "Copying files to locations");
        dal.updateSubmStatus(subm);

        // get settings
        //
        String procFolder = TheSettings.getSetting(SettingsConst.PROCESSING_LOCATION);
        String gatefolder = TheSettings.getSetting(SettingsConst.FTP_GATE_FOLDER);
        String prodfolder = TheSettings.getSetting(SettingsConst.PRODUCTION_FOLDER);

        HashMap<String, String> fixmap = Util.makeStringMap(
                "{GATEFOLDER}", gatefolder,
                "{PRODFOLDER}", prodfolder,
                "{PROCFOLDER}", procFolder,
                "{EXPID}", subm.getExperimentId(),
                "{SUBMID}", subm.getSubmissionId());


        log.info("----> Processing {}:{}", subm.getSubmissionId(), subm.getExperimentId());

        try {

            String afterwork0 = insertParameters(afterwork, fixmap);

            log.info("Afterwork:\n" + afterwork0);

            // say something
            //
            log.info("Processing {}:{}", subm.getSubmissionId(), subm.getExperimentId());

            // remove the result
            //
            r.asynchronousConsoleSubmit("rm(postProcessingResult)");

            // run the post processing script
            r.sourceFromBuffer(afterwork0);

            RObject result = r.getObject("try{postProcessingResult}");

            if (RUtil.inherits(result, "try-error")) {

                String rError = RUtil.getString(result);
                log.info("message: " + rError);

                subm.getMap().put(SubmDataDB.STATUS, DataConst.DELIVERY_FAILED);
                subm.getMap().put(SubmDataDB.STATUS_TEXT, rError);
                dal.updateSubmStatus(subm);

                notifyStatus(subm);

            } else {

                // update db records
                //
                dal.updateSubmCopied(subm);
                dal.updateSubmNotified(subm);

                subm.getMap().put(SubmDataDB.STATUS, DataConst.DELIVERED);
                subm.getMap().put(SubmDataDB.STATUS_TEXT, "Files computed and copied");
                dal.updateSubmStatus(subm);

                notifyStatus(subm);
            }

        } catch (Exception ex) {
            log.error("Error!", ex);

            // update db records
            //
            subm.getMap().put(SubmDataDB.STATUS, DataConst.DELIVERY_FAILED);
            subm.getMap().put(SubmDataDB.STATUS_TEXT, "Reason " + ex.getMessage());
            dal.updateSubmStatus(subm);

            notifyStatus(subm);
        }
    }


    public void processProcessedSubmissions() throws Exception {

        log.info("Processing Computed Submissions");

        String afterworkfname = System.getProperty( "afterwork" );

        if (afterworkfname == null || afterworkfname.length() == 0) {
            log.error("'afterwork' property much be set, use e.g. -Dafterwork=/path/to/afterwork.R");
            return;
        }

        String afterwork = Util.readFileIntoString(afterworkfname);

        // get register DAL
        //
        DAL dal = DALDefaults.getDAL();

        // get settings
        //
        Vector<SubmDataDB> toprocess = dal.listSubms(
                Util.makeStringMap(
                        SubmDataDB.STATUS, DataConst.COMPUTED,
                        SubmDataDB.COPIED, "0"));


        RCloudFactory rFactory = null;

        // anything to process ?
        if (toprocess.size() > 0) {

            log.info(toprocess.size() + " Computed submissions found");

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
                    processOneProcessedSubmission(subm, afterwork, dal, r);

                }

            } finally {
                if (rFactory != null) {

                    log.info("Disconnecting from server");

                    // leave it running
                    //
                    rFactory.disconnectFromRServices();

                    log.info("Disconnected");
                }
            }
        } else {
            log.info("No Computed submissions found");
        }
    }


    public static void main(String[] args) {

        try {
            new SubProcessor4Computed().processProcessedSubmissions();
        } catch (Exception ex) {
            log.error("Error!", ex);
        }
    }

}
