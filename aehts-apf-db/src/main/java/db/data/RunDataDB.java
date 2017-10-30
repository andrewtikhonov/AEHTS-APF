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
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 22/03/2012
 * Time: 11:37
 * To change this template use File | Settings | File Templates.
 */
public class RunDataDB implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(RunDataDB.class);
    private static final long serialVersionUID = 1L;

    private HashMap<String, Object> map = new HashMap<String, Object>();

    public static String SUBM_ID           = "SUBM_ID";
    public static String EXP_ID            = "EXP_ID";
    public static String RUN_ID            = "RUN_ID";
    public static String STATUS            = "STATUS";
    public static String STATUS_TEXT       = "STATUS_TEXT";
    public static String FILE_TIME         = "FILE_TIME";

    public RunDataDB(HashMap<String, Object> options) {
        map = options;
    }

    public String getSubmissionId() {
        return (String) map.get(SUBM_ID);
    }

    public String getExperimentId() {
        return (String) map.get(EXP_ID);
    }

    public String getRunId() {
        return (String) map.get(RUN_ID);
    }

    public String getStatus() {
        return (String) map.get(STATUS);
    }

    public String getStatusText() {
        return (String) map.get(STATUS_TEXT);
    }

    public long getFileTime() {
        Object obj = map.get(FILE_TIME);

        if (obj instanceof java.math.BigDecimal){
            return ((java.math.BigDecimal) obj).longValue();
        }
        return ((Long) obj);
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
                " RunId=" + getRunId() +
                " Status="+getStatus() +
                " Status Text="+getStatusText();
	}

}
