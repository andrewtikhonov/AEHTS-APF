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
 * Date: 23/03/2012
 * Time: 14:01
 * To change this template use File | Settings | File Templates.
 */
public class TimeDataDB implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(TimeDataDB.class);

    private static final long serialVersionUID = 1L;

    private HashMap<String, Object> map = new HashMap<String, Object>();

    public static String TIMESTAMP   = "TIMESTAMP";

    public TimeDataDB( HashMap<String, Object> map ) {
        this.map = map;
    }

    public Timestamp getTimestamp() {
        Object timestamp = map.get(TIMESTAMP);

        if (timestamp != null && timestamp instanceof oracle.sql.TIMESTAMP){
            try {
                timestamp = ((oracle.sql.TIMESTAMP)timestamp).timestampValue();
            } catch (Exception ex) {
                log.error("Error!", ex);
                timestamp = null;
            }
        }

        return (Timestamp) timestamp;
    }

	public String toString() {
		return "Timestamp=" + getTimestamp().toString();
	}

}
