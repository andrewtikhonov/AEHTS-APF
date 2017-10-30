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
import processor.util.TheSettings;
import processor.util.Util;
import uk.ac.ebi.rcloud.server.RServices;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 28/10/2013
 * Time: 10:37
 * To change this template use File | Settings | File Templates.
 */
public class SubProcessor4Checked extends SubProcessorBase {

    private final static Logger log = LoggerFactory.getLogger(SubProcessor4Checked.class);

    public String prepareOptions(String submOptions) {
        final String ALIGN   = "align";
        final String COUNT   = "count";
        final String ESET    = "eset";
        final String REPORTS = "reports";
        final String ON      = "on";

        String options = "";
        String steps = "";

        for(String pair : submOptions.split("&")) {
            String[] items = pair.split("=");

            // add reports
            if (REPORTS.equals(items[0])) {
                options = addParam(options,
                        "want.reports = " + (ON.equals(items[1]) ? "TRUE" : "FALSE"));
            }

            // add steps
            if (ALIGN.equals(items[0]) ||
                    COUNT.equals(items[0]) ||
                    ESET.equals(items[0])) {

                if (ON.equals(items[1])) {
                    steps = addParam(steps, "\"" + items[0] + "\"");
                }
            }
        }

        // add steps to options
        if (steps.length() > 0) {
            options = addParam(options,
                    "steplist = c(" + steps + ")");
        }

        // add comma as necessary
        if (options.length() > 0) {
            options = ", " + options;
        }

        return options;
    }

    public String addParam(String base, String param) {
        if (base.length() > 0) {
            base = base + ", ";
        }
        return base + param;
    }


    class CheckedSubmissionRunnable implements Runnable {
        private SubmDataDB subm = null;
        private String mainwork = null;

        public CheckedSubmissionRunnable(SubmDataDB subm, String mainwork) {
            this.subm = subm;
            this.mainwork = mainwork;
        }

        public void run() {

            // get settings
            //
            String poolname = TheSettings.getSetting(SettingsConst.DEFAULT_POOLNAME);
            String procFolder = TheSettings.getSetting(SettingsConst.PROCESSING_LOCATION);
            String refFolder = TheSettings.getSetting(SettingsConst.REFERENCE_LOCATION);
            String owner = TheSettings.getSetting(SettingsConst.PROCESS_USERNAME);
            String psrfolder = TheSettings.getSetting(SettingsConst.STATUS_FOLDER_NAME);

            RCloudFactory rFactory = null;

            try {
                DAL dal = DALDefaults.getDAL();

                HashMap<String, String> fixmap = Util.makeStringMap(
                        "{PROCFOLDER}", procFolder,
                        "{PSRFOLDER}", psrfolder,
                        "{REFFOLDER}", refFolder,
                        "{EXPID}", subm.getExperimentId(),
                        "{SUBMID}", subm.getSubmissionId(),
                        "{OPTIONS}", prepareOptions(subm.getOptions()));

                /*
                // create folders
                new File(processingPath).mkdirs();
                */

                String mainwork0 = insertParameters(mainwork, fixmap);

                log.info("Resource:\n" + mainwork0);

                // get R
                //
                rFactory = new RCloudFactory();

                RServices r = rFactory.getRServices(poolname);

                if (r == null) {
                    log.info("No R servers provided");
                    return;
                }

                String servername = r.getServantName();

                // set process owner
                r.setOwner(owner);

                // create directories
                //r.consoleSubmit("dir.create('" + processingPath + "', recursive=TRUE)");

                // update db records
                subm.getMap().put(SubmDataDB.STATUS, DataConst.COMPUTING);
                subm.getMap().put(SubmDataDB.MASTER_NAME, servername);

                dal.updateSubm(subm);
                dal.updateSubmStartTime(subm);

                // say something
                //
                log.info("Processing {}:{}", subm.getSubmissionId(), subm.getExperimentId());

                // source the resources
                //
                r.sourceFromBuffer(mainwork0);

            } catch (Exception ex) {
                log.error("Error!", ex);

            } finally {
                if (rFactory != null) {

                    log.info("Releasing R server");

                    // leave it running
                    //
                    rFactory.freeRServices();

                    log.info("Released");
                }
            }
        }
    }

    public void processCheckedSubmissions() throws Exception {

        log.info("Processing New Submissions");

        String mainworkfname = System.getProperty( "mainwork" );

        if (mainworkfname == null || mainworkfname.length() == 0) {
            log.error("'resource' property much be set, use e.g. -Dmainwork=/path/to/mainwork.R");
            return;
        }

        String resource = Util.readFileIntoString(mainworkfname);

        // get register DAL
        //
        DAL dal = DALDefaults.getDAL();

        // get settings
        //
        /*
        String poolname = TheSettings.getSetting(SettingsConst.DEFAULT_POOLNAME);
        String procFolder = TheSettings.getSetting(SettingsConst.PROCESSING_LOCATION);
        String refFolder = TheSettings.getSetting(SettingsConst.REFERENCE_LOCATION);
        String owner = TheSettings.getSetting(SettingsConst.PROCESS_USERNAME);
        String psrfolder = TheSettings.getSetting(SettingsConst.STATUS_FOLDER_NAME);
        */

        Integer processes_in_batch;

        try {
            processes_in_batch = Integer.parseInt(TheSettings.
                    getSetting(SettingsConst.PROCESSES_IN_BATCH));

        } catch (Exception ex) {
            processes_in_batch = 10;
        }




        /*
        // validate them
        //
        if (!new File(procFolder.getOptionValue()).exists()) {
            log.error("Failed to locate " + procFolder.getOptionValue() + " defined in " + SettingsConst.PROCESSING_LOCATION);
            return;
        }

        if (!new File(refFolder.getOptionValue()).exists()) {
            log.error("Failed to locate " + refFolder.getOptionValue() + " defined in " + SettingsConst.REFERENCE_LOCATION);
            return;
        }
        */

        // get what to process
        //
        Vector<SubmDataDB> toprocess = dal.listSubms(
                Util.makeStringMap(SubmDataDB.STATUS, DataConst.CHECKED_SUITABLE));

        if (toprocess.size() > 0) {

            log.info(toprocess.size() + " new submissions found");

            // process
            //
            for (SubmDataDB subm : toprocess) {

                if (processes_in_batch > 0) {
                    new Thread(new CheckedSubmissionRunnable(subm, resource)).start();
                }
                processes_in_batch--;
            }

            log.info("Waiting for threads to complete.");

        } else {
            log.info("No new submissions found");
        }

    }

}

