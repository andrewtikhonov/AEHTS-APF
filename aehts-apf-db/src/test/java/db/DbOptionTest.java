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

import db.data.SettingDataDB;
import db.data.SettingDataDB;
import db.data.SettingDataDB;
import db.exception.AlreadyExistsException;
import db.exception.GeneralException;
import db.sql.BaseSQL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 05/04/2012
 * Time: 10:34
 * To change this template use File | Settings | File Templates.
 */
public class DbOptionTest {
    
    private static final Logger log = LoggerFactory.getLogger(DbOptionTest.class);

    private DAL dal;
    private SettingDataDB testsetting;

    private String test_opt_name = "OPT-TEST-NAME-001";
    private String test_opt_value = "OPT-TEST-VALUE-001";

    public DbOptionTest() {
        try {
            dal = DALDefaults.getDAL();
        } catch (Exception ex) {
            log.error("Error!", ex);
        }

        assertTrue((dal != null));

        //log.info("DbOptionTest-DbOptionTest-DAL-created");
    }

    @Before
    public void prepare() throws InstantiationException {

        //log.info("DbOptionTest-prepare");

        HashMap<String, Object> opt01 = new HashMap<String, Object>();

        opt01.put(SettingDataDB.OPTION_NAME, test_opt_name);
        opt01.put(SettingDataDB.OPTION_VALUE, test_opt_value);

        testsetting = new SettingDataDB(opt01);

        try {
            dal.createSetting(testsetting);

            //log.info("DbOptionTest-prepare-createExperiment-OK");
        } catch (AlreadyExistsException aee) {
            log.info("Info!", aee);
        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        //log.info("DbOptionTest-prepare-done");
    }

    @After
    public void cleanup() {
        //log.info("DbOptionTest-cleanup");

        try {
            dal.deleteSetting(testsetting);
        } catch (GeneralException ge) {
            log.error("Error!", ge);
            assertTrue(false);
        }
    }

    @Test
    public void testDBAlreadyExists() {

        log.info("DbOptionTest-testDBAlreadyExists");

        try {
            dal.createSetting(testsetting);

            // assert if returned
            // without exception
            assertTrue(false);

        } catch (AlreadyExistsException aee) {
            // OK
            log.info("DbOptionTest-testDBAlreadyExists-OK");

        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        log.info("DbOptionTest-testDBAlreadyExists-done");
    }

    @Test
    public void testDBgetSetting() {

        log.info("DbOptionTest-getSetting");

        try {
            SettingDataDB set03 = dal.getSetting(test_opt_name);

            assertTrue(set03 != null);
            assertEquals(set03.getOptionValue(), test_opt_value);

            log.info("DbOptionTest-getSetting-OK");

        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        log.info("DbOptionTest-getSetting-done");
    }

    @Test
    public void testDBgetSettingData() {

        log.info("DbOptionTest-getSettingData");

        try {
            Vector<SettingDataDB> records =
                    dal.getSettingData(SettingDataDB.OPTION_NAME + " = " + BaseSQL.wrap(test_opt_name));

            assertTrue(records.size() > 0);

            log.info("DbOptionTest-getSettingData-OK");

        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        log.info("DbOptionTest-getSettingData-done");
    }

    @Test
    public void testDBupdateSetting() {

        log.info("DbOptionTest-updateSetting");

        try {
            String test_value_22 = "test_value_22";

            testsetting.getMap().put(SettingDataDB.OPTION_VALUE, test_value_22);

            dal.updateSetting(testsetting);

            log.info("DbOptionTest-updateSetting-updateSetting-OK");

            SettingDataDB record = dal.getSetting(test_opt_name);

            log.info("DbOptionTest-updateSetting-getSetting-OK");

            assertTrue(record != null);
            assertEquals(record.getOptionValue(), test_value_22);

            log.info("DbOptionTest-updateSetting-OK");

        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        log.info("DbOptionTest-updateSetting-done");
    }

}
