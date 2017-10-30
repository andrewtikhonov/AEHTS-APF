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
import db.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processor.data.StatusInfo;
import processor.factory.RCloudFactory;
import processor.util.TheSettings;
import processor.util.Util;
import uk.ac.ebi.rcloud.server.RServices;
import uk.ac.ebi.rcloud.server.file.FileNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 10/04/2012
 * Time: 11:42
 * To change this template use File | Settings | File Templates.
 */
public class SubProcessor4Computing extends SubProcessorBase {
    private final static Logger log = LoggerFactory.getLogger(SubProcessor4Computing.class);

    public void processOneComputingSubmission(SubmDataDB subm, DAL dal, RServices r) throws Exception {

        // get settings
        //
        String procFolder = TheSettings.getSetting(SettingsConst.PROCESSING_LOCATION);
        String statusFolder = TheSettings.getSetting(SettingsConst.STATUS_FOLDER_NAME);

        log.info("----> Processing {}:{}", subm.getSubmissionId(), subm.getExperimentId());

        // assemble the experiment path
        String processingPath =
                procFolder + FileNode.separator +
                        subm.getExperimentId() + FileNode.separator +
                        subm.getSubmissionId() + FileNode.separator + statusFolder;


        FileNode node = r.readDirectory(processingPath);

        if (node.getChildren() == null || node.getChildren().length == 0) {
            //
            //
            log.info("Empty PSR folder {}", processingPath);

            if (System.currentTimeMillis() > subm.getStartTime().getTime() + 1000 * 60 * 10) { // 10 minutes
                //
                // 10 minutes have passed,
                // no PSR created, likely an error
                //
                subm.getMap().put(SubmDataDB.STATUS, DataConst.COMPUTING_FAILED);
                subm.getMap().put(SubmDataDB.STATUS_TEXT, "Computation didn't start for 10 minutes, PSR folder is missing");

                // update the submission status
                //
                dal.updateSubmStatus(subm);

                // update finish time as necessary
                //
                dal.updateSubmFinishTime(subm);

                log.info("Updated SUBM status {}={}", subm.getExperimentId(),
                        DataConst.COMPUTING_FAILED);

                notifyStatus(subm);
            }

            return;
        }

        for(Object o : node.getChildren()) {

            if (o instanceof FileNode) {

                FileNode n1    = ((FileNode) o);

                String n1name  = n1.getName();

                // see if its the "STATUS" file
                //
                if (n1name.toUpperCase().contains("STATUS")) {

                    // "EXP" status and
                    // needs to update
                    //
                    if (n1name.toUpperCase().contains(".EXP.") && n1.lastModified() > subm.getFileTime()) {

                        log.info("----> Located EXP STATUS file {}", n1name);

                        // download & parse status file
                        //
                        HashMap<String, StatusPair> m0 = parseStatusMap(downloadFile(n1.getPath(), r));

                        log.info("Parsed EXP status map");

                        // update Db if status file has updated
                        for (Map.Entry<String, StatusPair> entry : m0.entrySet()) {

                            if (DataConst.EXP_STATUS.equals(entry.getKey())) {

                                // update the EXP status
                                //
                                subm.getMap().put(SubmDataDB.STATUS, entry.getValue().status);
                                subm.getMap().put(SubmDataDB.STATUS_TEXT, entry.getValue().status_text);

                                // the actual write
                                //
                                dal.updateSubmStatus(subm);

                                // update finish time as necessary
                                //
                                if (StatusInfo.isShowstopper(entry.getValue().status)) {
                                    // update
                                    //
                                    dal.updateSubmFinishTime(subm);
                                }

                                notifyStatus(subm);
                                dal.updateSubmNotified(subm);

                                log.info("Updated SUBM status {}={}", subm.getExperimentId(),
                                        entry.getValue().status);

                            } else {
                                // record each exp step
                                //
                                dal.createExpStep(makeExpStep(subm, entry.getKey(),
                                        entry.getValue().status,
                                        entry.getValue().status_text));

                                log.info("Updated EXP step {}={}={}",
                                        new String[]{ entry.getKey(), entry.getValue().status,
                                                entry.getValue().status_text});
                            }
                        }

                        // update the last update time
                        //
                        subm.getMap().put(SubmDataDB.FILE_TIME, n1.lastModified());

                        dal.updateSubmFileTime(subm);

                        log.info("Updated EXP update time");
                    }

                    // "RUN" status file
                    //
                    if (n1name.toUpperCase().contains(".RUN.")) {

                        log.info("----> Located RUN status file {}", n1name);

                        // figure out the runid from filename
                        //
                        int firstdot = n1name.indexOf(".");
                        String runid = n1name.substring(0, firstdot);

                        // get run record
                        //
                        RunDataDB run = dal.getRun(new RunDataDB(
                                Util.makeObjectMap(RunDataDB.SUBM_ID, subm.getSubmissionId(),
                                        RunDataDB.RUN_ID, runid)));

                        if (run == null) {
                            // if does not exist, create
                            //
                            run = makeRun(subm, runid, DataConst.COMPUTING, "");
                            dal.createRun(run);

                            log.info("Created RUN {}", runid);
                        }

                        // see if file has been updated
                        // update the DB as necessary
                        //
                        if (n1.lastModified() > run.getFileTime()) {

                            // download an parse the status file
                            //
                            HashMap<String, StatusPair> m0 = parseStatusMap(downloadFile(n1.getPath(), r));

                            log.info("Parsed RUN status map");

                            for (Map.Entry<String, StatusPair> entry : m0.entrySet()) {

                                if (DataConst.RUN_STATUS.equals(entry.getKey())) {

                                    //
                                    // update the RUN status
                                    //
                                    run.getMap().put(RunDataDB.STATUS, entry.getValue().status);
                                    run.getMap().put(RunDataDB.STATUS_TEXT, entry.getValue().status_text);

                                    // the actual write
                                    //
                                    dal.updateRunStatus(run);

                                    log.info("Updated RUN status {}={}={}", new String[] { runid,
                                            entry.getValue().status,
                                            entry.getValue().status_text});

                                } else {
                                    // record each run step
                                    //
                                    dal.createRunStep(makeRunStep(subm, runid,entry.getKey(),
                                            entry.getValue().status,
                                            entry.getValue().status_text));

                                    log.info("Updated RUN step status {}={}={}", new String[]{
                                            entry.getKey(), entry.getValue().status,
                                            entry.getValue().status_text});
                                }
                            }
                        }

                        // update the last update time
                        //
                        run.getMap().put(RunDataDB.FILE_TIME, n1.lastModified());

                        dal.updateRunFileTime(run);
                    }
                }
            }
        }

        // say something
        //
        log.info("Monitored processing for {}:{}", subm.getSubmissionId(), subm.getExperimentId());
    }

    public void processComputingSubmissions() throws Exception {

        log.info("Processing Running Submissions");

        // get register DAL
        //
        DAL dal = DALDefaults.getDAL();

        // get settings
        //
        Vector<SubmDataDB> toprocess = dal.listSubms(
                Util.makeStringMap(SubmDataDB.STATUS, DataConst.COMPUTING));


        RCloudFactory rFactory = null;

        // anything to process ?
        if (toprocess.size() > 0) {

            log.info(toprocess.size() + " running submissions found");

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
                    processOneComputingSubmission(subm, dal, r);
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
            log.info("No running submissions found");
        }

    }

    public static void main(String[] args) {

        try {
            new SubProcessor4Computing().processComputingSubmissions();
        } catch (Exception ex) {
            log.error("Error!", ex);
        }
    }

}
