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
package db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 26/03/2012
 * Time: 14:08
 * To change this template use File | Settings | File Templates.
 */
public abstract class DALDefaults {

    final private static Logger log = LoggerFactory.getLogger(DALDefaults.class);

    public static final java.lang.String DEFAULT_DB_TYPE = "";
    public static final java.lang.String DEFAULT_DB_HOST = "";
    public static final int              DEFAULT_DB_PORT = 0;
    public static final java.lang.String DEFAULT_DB_NAME = "";
    public static final java.lang.String DEFAULT_DB_USERNAME = "";
    public static final java.lang.String DEFAULT_DB_PASSWORD = "";

    public static DAL _dal = null;
    public static Integer _lock = new Integer(0);

	public static String _dbUrl;
	public static String _dbDriver;
	public static String _dbUsername;
	public static String _dbPassword;

    private static boolean _propertiesInjected = false;
    private static Properties staticprops = new Properties();

	static {
		init();
	}

    private static String initProperty(String name, String defaultValue) {
        String value = System.getProperty(name);
        String valueToAdd = (value != null && value.length() > 0) ? value : defaultValue;
        staticprops.put(name, valueToAdd);
        return valueToAdd;
    }

    private static void readProperties(InputStream is, boolean override) {
        if (is != null) {
            try {
                Properties props = new Properties();
                props.loadFromXML(is);

                //log.info("Properties : " + props);

                Enumeration<Object> keys = props.keys();
                while (keys.hasMoreElements()) {
                    String key = (String) keys.nextElement();
                    if (override || staticprops.containsKey(key)) {
                        staticprops.put(key, props.getProperty(key));
                    }
                }
            } catch (Exception e) {
                log.error("Error!", e);
            }
        }
    }

    public static void injectProperties() {

        if (_propertiesInjected) return;

        readProperties(DALDefaults.class.getResourceAsStream("/dal.properties"), true);

        String extension = System.getProperty("properties.extension");

        if (extension != null && extension.length() > 0) {
            if (new File(extension).exists()) {
                try {
                    readProperties(new FileInputStream(extension), true);
                } catch (Exception e) {
                    log.error("Error!", e);
                }
            } else {
                log.info("Invalid File Name in 'properties.extension' <" + extension + ">");
            }
        }

        _propertiesInjected = true;
    }

	public static void init()	{

        String _DB_TYPE = initProperty("register.db.type", DEFAULT_DB_TYPE);
        String _DB_HOST = initProperty("register.db.host", DEFAULT_DB_HOST);
		int    _DB_PORT = Integer.decode(
                          initProperty("register.db.port", Integer.toString(DEFAULT_DB_PORT)));
        String _DB_NAME = initProperty("register.db.name", DEFAULT_DB_NAME);

		if (_DB_TYPE.equals("mysql")) {
			_dbUrl = "jdbc:mysql://"+_DB_HOST+":"+_DB_PORT+"/"+_DB_NAME;
			_dbDriver="org.gjt.mm.mysql.Driver";

		} else if (_DB_TYPE.equals("oracle")) {
			_dbUrl = "jdbc:oracle:thin:@"+_DB_HOST+":"+_DB_PORT+":"+_DB_NAME;
			_dbDriver="oracle.jdbc.driver.OracleDriver";
		}

		_dbUsername = initProperty("register.db.username", DEFAULT_DB_USERNAME);
		_dbPassword = initProperty("register.db.password", DEFAULT_DB_PASSWORD);

	}

    public static DAL getDAL() throws Exception {
   		if (_dal != null)
   			return _dal;
   		synchronized (_lock) {
   			if (_dal == null) {
                Class.forName(_dbDriver);
                    _dal = DAL.getLayer(getDBType(_dbUrl), new DALConnectionProvider() {
                        public Connection newConnection() throws java.sql.SQLException {
                            return DriverManager.getConnection(_dbUrl, _dbUsername, _dbPassword);
                        }
      			});
   			}
   			return _dal;
   		}
   	}

    public static String getDBType(String jdbcUrl) {
   		int p1 = jdbcUrl.indexOf(':');
   		int p2 = jdbcUrl.indexOf(':', p1 + 1);
   		return jdbcUrl.substring(p1 + 1, p2);
   	}

}

