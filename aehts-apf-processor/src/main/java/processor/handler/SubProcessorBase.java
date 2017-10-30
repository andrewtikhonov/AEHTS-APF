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
import db.SettingsConst;
import db.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processor.data.StatusInfo;
import processor.factory.RCloudFactory;
import processor.util.MClient;
import processor.util.TheSettings;
import processor.util.Util;
import uk.ac.ebi.rcloud.server.RServices;
import util.StringUtil;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 01/05/2012
 * Time: 14:06
 * To change this template use File | Settings | File Templates.
 */
public class SubProcessorBase {

    private final static Logger log = LoggerFactory.getLogger(SubProcessorBase.class);

    /*
    public void notifyComputed(SubmDataDB subm){

        try {

            MClient client = new MClient();

            String subj   = "Submission " + subm.getSubmissionId() + " Computed";
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
    }*/

    public void notifyStatus(SubmDataDB subm){

        try {

            MClient client = new MClient();

            String subj   = "Submission " + subm.getSubmissionId() + " Status";
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


    public String insertParameters(String script, HashMap<String, String> params) {
        String script0 = script;

        for( Map.Entry<String, String> entry : params.entrySet()) {

            if (entry.getValue() == null) {
                System.out.println("entry.getKey()=" + entry.getKey() + " entry.getValue()=" + entry.getValue());
            }

            script0 = StringUtil.replaceAll(script0, entry.getKey(), entry.getValue());
        }

        return script0;
    }

    private HashMap<String, StatusPair> readStatusMap(String filename) {

        HashMap<String, StatusPair> map = new HashMap<String, StatusPair>();

        Vector<String> buffer = Util.readFileIntoVector(filename);

        for(String line : buffer) {
            String[] words = line.split("\n");

            if (words.length != 2) {
                log.error("Error processing status map, line='{}', words.length={}", line, words.length);
            }

            map.put(words[0], new StatusPair(words[1], words[2]) );
        }

        return map;
    }

    class StatusPair {
        public StatusPair(String status, String status_text){
            this.status = status;
            this.status_text = status_text;
        }
        public String status;
        public String status_text;
    }

    public HashMap<String, StatusPair> parseStatusMap(String content) {

        HashMap<String, StatusPair> map = new HashMap<String, StatusPair>();

        String[] buffer = content.split("\n");

        for(String line : buffer) {
            String[] words = line.split("\t");

            if (words.length < 2) {
                log.error("Error processing status map, line='{}', words.length={}", line, words.length);
                continue;
            }

            String step = words[0];
            String status = words[1];
            String text = words.length == 3 ? words[2] : "";

            // severity
            //
            StatusPair pair = map.get(step);

            if (pair == null) {
                // no entry
                //
                map.put(step, new StatusPair(status, text));
            } else {
                //
                // priority, the lower the value the higher the priority, 0 - highest
                // update if status of higher or equal priority is received
                //
                if (StatusInfo.getPriority(status) <=
                        StatusInfo.getPriority(pair.status)) {
                    // update
                    //
                    map.put(step, new StatusPair(status, text));
                }
            }
        }

        return map;
    }

    public RServices allocateDefaultServer(RCloudFactory rFactory) {
        // get register DAL
        //
        DAL dal = null;
        try {
            dal = DALDefaults.getDAL();
        } catch (Exception ex) {
            log.error("Error!", ex);
            return null;
        }

        String poolname = TheSettings.getSetting(SettingsConst.DEFAULT_POOLNAME);
        String servername = TheSettings.getSetting(SettingsConst.DEFAULT_SERVERNAME);
        String owner = TheSettings.getSetting(SettingsConst.PROCESS_USERNAME);

        RServices r = rFactory.locateRServices(servername);

        if (r != null) {
            try {
                if (owner.equals(r.getOwner())) {

                    String response = r.ping();

                    if ("pong".equalsIgnoreCase(response)) {
                        return r;
                    }
                }
            } catch (Exception ex) {
                // wrong server
            }
        }

        // allocate default server
        //

        int cnt = 10;

        while(cnt > 0) {

            rFactory.disconnectFromRServices();

            try {
                r = rFactory.getRServices(poolname);

                String servername0 = r.getServantName();

                r.setOwner(owner);

                SettingDataDB snamesetting = new SettingDataDB(Util.makeObjectMap(
                        SettingDataDB.OPTION_NAME, SettingsConst.DEFAULT_SERVERNAME,
                        SettingDataDB.OPTION_VALUE, servername0));

                dal.updateSetting(snamesetting);

                return r;
            } catch (Exception ex) {
                // failed
                //
            }

            cnt--;
        }

        return null;
    }

    private static final int BLOCKSIZE = 1024 * 16;
    private static final int MAXRETRIES = 20;

    public static String downloadFile(String fromPath, RServices r) throws RemoteException {

        int retries = MAXRETRIES;
        StringBuilder builder = new StringBuilder();

        try {
            long fsize = r.getRandomAccessFileDescription(fromPath).length();

            while (builder.length() < fsize) {

   				byte[] block = r.readRandomAccessFileBlock(fromPath,
                           builder.length(), BLOCKSIZE);

                   if (block == null) {
                       log.error("file transfer error, retrying..");

                       while(retries > 0 && block == null) {
                           try { Thread.sleep(500); } catch (Exception ex) {}

                           retries--;

                           log.info("attempt " + (MAXRETRIES - retries));

                           block = r.readRandomAccessFileBlock(fromPath,
                                   builder.length(), BLOCKSIZE);

                           if (block != null) break;
                       }

                       if (block != null) {
                           log.info("transfer successful.");
                       } else {
                           log.error("transfer unsuccessful " + fromPath);
                       }
                   }
                builder.append(new String(block));
   			}
   		} catch (RemoteException re) {
            log.error("Error!", re);
   		}
        return builder.toString();
   	}

    public ExpStepDataDB makeExpStep(SubmDataDB subm, Object stepid, Object status, Object statustext) {
        return new ExpStepDataDB(
                Util.makeObjectMap(ExpStepDataDB.SUBM_ID, subm.getSubmissionId(),
                        ExpStepDataDB.EXP_ID, subm.getExperimentId(),
                        ExpStepDataDB.STEP_ID, stepid,
                        ExpStepDataDB.STATUS, status,
                        ExpStepDataDB.STATUS_TEXT, statustext));
    }

    public RunDataDB makeRun(SubmDataDB subm, Object runid, Object status, Object statustext) {
        return new RunDataDB(
                Util.makeObjectMap(RunDataDB.SUBM_ID, subm.getSubmissionId(),
                        RunDataDB.RUN_ID, runid,
                        RunDataDB.EXP_ID, subm.getExperimentId(),
                        RunDataDB.FILE_TIME, (long) 0,
                        RunDataDB.STATUS, status,
                        RunDataDB.STATUS_TEXT, statustext));
    }

    public RunStepDataDB makeRunStep(SubmDataDB subm, Object runid, Object stepid, Object status, Object statustext) {
        return new RunStepDataDB(
                Util.makeObjectMap(RunStepDataDB.SUBM_ID, subm.getSubmissionId(),
                        RunStepDataDB.RUN_ID, runid,
                        RunStepDataDB.STEP_ID, stepid,
                        RunStepDataDB.STATUS, status,
                        RunStepDataDB.STATUS_TEXT, statustext));
    }

    public static void main( String[] args ) throws Exception {


        try {
            HashMap<String, StatusPair> map = new SubProcessorBase().readStatusMap(System.getProperty("fname"));

            for(Map.Entry<String, StatusPair> entry : map.entrySet()) {
                log.info("{}:{}:{}", new String[]{ entry.getKey(),
                        entry.getValue().status,
                        entry.getValue().status_text });
            }

        } catch (Exception ex) {
            log.error("Error!", ex);
        }

        log.info("returned from runProcessor");

        //System.exit(0);
	}


}
