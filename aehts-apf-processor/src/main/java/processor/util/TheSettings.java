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
package processor.util;

import db.DAL;
import db.DALDefaults;
import db.data.SettingDataDB;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 11/04/2012
 * Time: 16:12
 * To change this template use File | Settings | File Templates.
 */
public class TheSettings {
    
    private static DAL dal = null;
    private static HashMap<String, SettingDataDB> map = new HashMap<String, SettingDataDB>();
    
    static {
        try {
            dal = DALDefaults.getDAL();
            
        } catch (Exception ex) {
        }
    }

    public static String getSetting(String name) {
        
        if (map.containsKey(name)) {
            return map.get(name).getOptionValue();
        } else {
            try {
                SettingDataDB setting = dal.getSetting(name);
                map.put(name, setting);

                return setting.getOptionValue();
            } catch (Exception ex) {
                return null;
            }
        }
    }

}
