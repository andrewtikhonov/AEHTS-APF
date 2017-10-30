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

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 04/04/2012
 * Time: 16:59
 * To change this template use File | Settings | File Templates.
 */
public class SettingDataDB implements Serializable {

    private static final long serialVersionUID = 1L;

    private HashMap<String, Object> map = new HashMap<String, Object>();

    public static String OPTION_NAME     = "OPTION_NAME";
    public static String OPTION_VALUE    = "OPTION_VALUE";

    public SettingDataDB( HashMap<String, Object> options ) {
        map = options;
    }

    public String getOptionName() {
        return (String) map.get(OPTION_NAME);
    }

    public String getOptionValue() {
        return (String) map.get(OPTION_VALUE);
    }

    public HashMap<String, Object> getMap() {
        return map;
    }

	public String toString() {
		return getOptionValue();
	}

}
