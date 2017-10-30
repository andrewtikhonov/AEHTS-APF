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
 * Time: 11:45
 * To change this template use File | Settings | File Templates.
 */
public class StepDataDB implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(StepDataDB.class);
    private static final long serialVersionUID = 1L;

    private HashMap<String, Object> map = new HashMap<String, Object>();

    public static String STEP_ID       = "STEP_ID";
    public static String DESCRIPTION   = "DESCRIPTION";

    public StepDataDB(HashMap<String, Object> options) {
        map = options;
    }

    public String getStepId() {
        return (String) map.get(STEP_ID);
    }

    public String getDescription() {
        return (String) map.get(DESCRIPTION);
    }

    public boolean isEmpty()
    {
        return (map == null || map.isEmpty());
    }

    public HashMap<String, Object> getMap() {
        return map;
    }

	public String toString() {
		return "StepId=" + getStepId() +
                " Description="+ getDescription();
	}

}
