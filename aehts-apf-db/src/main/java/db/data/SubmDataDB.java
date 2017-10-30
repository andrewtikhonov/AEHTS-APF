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
package db.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 13/03/2012
 * Time: 16:31
 * To change this template use File | Settings | File Templates.
 */
public class SubmDataDB implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(SubmDataDB.class);
    private static final long serialVersionUID = 1L;

    private HashMap<String, Object> map = new HashMap<String, Object>();

    public static String SUBM_ID      = "SUBM_ID";
    public static String EXP_ID       = "EXP_ID";
    public static String STATUS       = "STATUS";
    public static String STATUS_TEXT  = "STATUS_TEXT";
    public static String SUBMITTER    = "SUBMITTER";
    public static String SUBMIT_TIME  = "SUBMIT_TIME";
    public static String START_TIME   = "START_TIME";
    public static String FINISH_TIME  = "FINISH_TIME";
    public static String MASTER_NAME  = "MASTER_NAME";
    public static String ENSEMBL_REL_VER
                                      = "ENSEMBL_REL_VER";
    public static String FILE_TIME    = "FILE_TIME";
    public static String NOTIFIED     = "NOTIFIED";
    public static String COPIED       = "COPIED";
    public static String USER_NOTES   = "USER_NOTES";
    public static String OPTIONS      = "OPTIONS";
    public static String METADATA     = "METADATA";

    public static String AUTOPREFIX   = "SUBM_ID_";

    public SubmDataDB(HashMap<String, Object> options) {
        map = options;
    }

    public String getSubmissionId() {
        return (String) map.get(SUBM_ID);
    }

    public String getExperimentId() {
        return (String) map.get(EXP_ID);
    }

    public String getStatus() {
        return (String) map.get(STATUS);
    }

    public String getStatusText() {
        Object o = map.get(STATUS_TEXT);
        return o == null ? "" : ((String) o);
    }

    public String getSubmitter() {
        return (String) map.get(SUBMITTER);
    }

    public String getMasterServerName() {
        return (String) map.get(MASTER_NAME);
    }

    public String getUserNotes() {
        return (String) map.get(USER_NOTES);
    }

    public String getOptions() {
        return (String) map.get(OPTIONS);
    }

    public String getMetadata() {
        return (String) map.get(METADATA);
    }

    public String getEnsemblRelVer() {
        return (String) map.get(ENSEMBL_REL_VER);
    }

    public Object convertToTimestamp(Object timestamp) {
        if (timestamp != null && timestamp instanceof oracle.sql.TIMESTAMP){
            try {
                timestamp = ((oracle.sql.TIMESTAMP)timestamp).timestampValue();
            } catch (Exception ex) {
                log.error("Error!", ex);
                timestamp = null;
            }
        }
        return timestamp;
    }

    public Integer getNotified() {
        Object obj = map.get(NOTIFIED);

        if (obj instanceof java.math.BigDecimal){
            return ((java.math.BigDecimal) obj).intValue();
        }
        return ((Integer) obj);
    }

    public Integer getCopied() {
        Object obj = map.get(COPIED);

        if (obj instanceof java.math.BigDecimal){
            return ((java.math.BigDecimal) obj).intValue();
        }
        return ((Integer) obj);
    }

    public long getFileTime() {
        Object obj = map.get(FILE_TIME);

        if (obj instanceof java.math.BigDecimal){
            return ((java.math.BigDecimal) obj).longValue();
        }
        return ((Long) obj);
    }

    public Timestamp getSubmitTime() {
        Object timestamp = map.get(SUBMIT_TIME);
        return (Timestamp) convertToTimestamp(timestamp);
    }

    public Timestamp getStartTime() {
        Object timestamp = map.get(START_TIME);
        return (Timestamp) convertToTimestamp(timestamp);
    }

    public Timestamp getFinishTime() {
        Object timestamp = map.get(FINISH_TIME);
        return (Timestamp) convertToTimestamp(timestamp);
    }

    public boolean isEmpty()
    {
        return (map == null || map.isEmpty());
    }

    public HashMap<String, Object> getMap() {
        return map;
    }

	public String toString() {
		return "SubmissionId=" + getSubmissionId() +
                " ExperimentId=" + getExperimentId() +
                " Status=" + getStatus() +
                " getStatusText=" + getStatusText();
	}

}
