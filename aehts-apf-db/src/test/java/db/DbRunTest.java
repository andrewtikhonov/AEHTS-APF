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

import db.data.SubmDataDB;
import db.data.RunDataDB;
import db.exception.AlreadyExistsException;
import db.exception.GeneralException;
import db.sql.BaseSQL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 27/03/2012
 * Time: 13:03
 * To change this template use File | Settings | File Templates.
 */
public class DbRunTest {

    private static final Logger log = LoggerFactory.getLogger(DbRunTest.class);

    private DAL dal;
    private SubmDataDB testexp;
    private RunDataDB testrun1;
    private RunDataDB testrun2;

    private String test_subm_id = "TEST-SUBM-ID-001";
    private String test_user_notes = "Test Submission";
    private String test_exp_id = "E-TEST-16190";
    private String test_run_id1 = "SRR-TEST-0001";
    private String test_run_id2 = "SRR-TEST-0002";
    private String test_status = "UNDEFINED";
    private String test_status_text = "Test Status Text";
    private String test_submitter = "Vasja Pupkin";

    public DbRunTest() {
        try {
            dal = DALDefaults.getDAL();
        } catch (Exception ex) {
            log.error("Error!", ex);
        }

        assertTrue((dal != null));

        //log.info("DbRunTest-DbRunTest-DAL-created");
    }

    @Before
    public void prepare() throws InstantiationException {

        //log.info("DbRunTest-prepare");

        HashMap<String, Object> expmap01 = new HashMap<String, Object>();

        expmap01.put(SubmDataDB.SUBM_ID,     test_subm_id);
        expmap01.put(SubmDataDB.EXP_ID,      test_exp_id);
        expmap01.put(SubmDataDB.STATUS,      test_status);
        expmap01.put(SubmDataDB.STATUS_TEXT, test_status_text);
        expmap01.put(SubmDataDB.USER_NOTES,  test_user_notes);
        expmap01.put(SubmDataDB.SUBMITTER,   test_submitter);

        testexp = new SubmDataDB(expmap01);

        try {
            dal.createSubm(testexp);

            //log.info("DbRunTest-prepare-createExperiment-OK");
        } catch (AlreadyExistsException aee) {
            log.info("Info!", aee);
        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        HashMap<String, Object> runmap01 = new HashMap<String, Object>();

        runmap01.put(RunDataDB.SUBM_ID, test_subm_id);
        runmap01.put(RunDataDB.EXP_ID,  test_exp_id);
        runmap01.put(RunDataDB.RUN_ID,  test_run_id1);
        runmap01.put(RunDataDB.STATUS,  test_status);
        runmap01.put(RunDataDB.STATUS_TEXT,  test_status_text);

        testrun1 = new RunDataDB(runmap01);

        try {
            dal.createRun(testrun1);

            //log.info("DbRunTest-prepare-createRun-OK");
        } catch (AlreadyExistsException aee) {
            log.info("Info!", aee);
        } catch (GeneralException ge) {
            log.error("Error!", ge);
            assertTrue(false);
        }

        HashMap<String, Object> runmap02 = new HashMap<String, Object>();

        runmap02.put(RunDataDB.SUBM_ID, test_subm_id);
        runmap02.put(RunDataDB.EXP_ID,  test_exp_id);
        runmap02.put(RunDataDB.RUN_ID,  test_run_id2);
        runmap02.put(RunDataDB.STATUS,  test_status);
        runmap02.put(RunDataDB.STATUS_TEXT,  test_status_text);

        testrun2 = new RunDataDB(runmap02);

        try {
            dal.createRun(testrun2);

            //log.info("DbRunTest-prepare-createRun-OK");
        } catch (AlreadyExistsException aee) {
            log.info("Info!", aee);
        } catch (GeneralException ge) {
            log.error("Error!", ge);
            assertTrue(false);
        }

        //log.info("DbRunTest-prepare-done");
    }

    @After
    public void cleanup() {
        //log.info("DbRunTest-cleanup");

        try {
            dal.deleteRun(testrun1);
        } catch (GeneralException ge) {
            log.error("Error!", ge);
            assertTrue(false);
        }

        try {
            dal.deleteRun(testrun2);
        } catch (GeneralException ge) {
            log.error("Error!", ge);
            assertTrue(false);
        }

        try {
            dal.deleteSubm(testexp);
        } catch (GeneralException ge) {
            log.error("Error!", ge);
            assertTrue(false);
        }
    }

    @Test
    public void testDBAlreadyExists() {

        log.info("DbRunTest-testDBAlreadyExists");

        try {
            dal.createRun(testrun1);

            // assert if returned
            // without exception
            assertTrue(false);

        } catch (AlreadyExistsException aee) {
            // OK
            log.info("DbRunTest-testDBAlreadyExists-OK");

        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        log.info("DbRunTest-testDBAlreadyExists-done");
    }

    @Test
    public void testDBconstraintViolation() {

        log.info("DbRunTest-constraintViolation");

        String test_nonexistent_subm_id = test_subm_id + "ASD001";
        

        HashMap<String, Object> runmap01 = new HashMap<String, Object>();

        runmap01.put(RunDataDB.SUBM_ID, test_nonexistent_subm_id);
        runmap01.put(RunDataDB.RUN_ID,  test_run_id1);
        runmap01.put(RunDataDB.EXP_ID,  test_exp_id);
        runmap01.put(RunDataDB.STATUS,  test_status);
        runmap01.put(RunDataDB.STATUS_TEXT,  test_status_text);

        try {
            dal.createRun(new RunDataDB(runmap01));

            log.info("DbRunTest-constraintViolation-Failed");

            assertTrue(false);
        } catch (AlreadyExistsException aee) {
            log.error("Error!", aee);

            assertTrue(false);
        } catch (GeneralException ge) {
            if (ge.getCause() instanceof SQLIntegrityConstraintViolationException) {
                // OK
                log.info("DbRunTest-constraintViolation-OK");
            } else {
                log.error("Error!", ge);
                assertTrue(false);
            }
        } finally {
            try {
                dal.deleteRun(new RunDataDB(runmap01));
            } catch (Exception ex) {
            }
        }

        log.info("DbRunTest-constraintViolation-done");
    }
    
    @Test
    public void testDBgetRun() {

        log.info("DbRunTest-getRun");

        try {
            HashMap<String, Object> runmap02 = new HashMap<String, Object>();

            runmap02.put(RunDataDB.SUBM_ID, test_subm_id);
            runmap02.put(RunDataDB.RUN_ID,  test_run_id1);

            RunDataDB run03 = dal.getRun(new RunDataDB(runmap02));

            assertTrue(run03 != null);
            assertEquals(run03.getSubmissionId(), testrun1.getSubmissionId());
            assertEquals(run03.getExperimentId(), testrun1.getExperimentId());
            assertEquals(run03.getRunId(), testrun1.getRunId());
            assertEquals(run03.getStatus(), testrun1.getStatus());
            assertEquals(run03.getStatusText(), testrun1.getStatusText());

            log.info("DbRunTest-getRun-OK");

        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        log.info("DbRunTest-getRun-done");
    }

    @Test
    public void testDBlistRuns() {

        log.info("DbRunTest-listRuns");

        try {
            HashMap<String, String> reqmap = new HashMap<String, String>();
            reqmap.put(RunDataDB.SUBM_ID, test_subm_id);

            Vector<RunDataDB> records = dal.listRuns(reqmap);

            log.info("DbRunTest-listRuns-OK");

            assertTrue(records.size() > 0);

        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        log.info("DbRunTest-listRuns-done");
    }

    @Test
    public void testDBgetRunData() {

        log.info("DbRunTest-getRunData");

        try {
            Vector<RunDataDB> records =
                    dal.getRunData(RunDataDB.EXP_ID + " = " + BaseSQL.wrap(test_exp_id));

            assertTrue(records.size() > 0);

            log.info("DbRunTest-getRunData-OK");

        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        log.info("DbRunTest-getRunData-done");
    }

    @Test
    public void testDBupdateRunStatus() {

        log.info("DbRunTest-updateRunStatus");

        try {
            String test_status_22 = "test_status_22";
            String test_status_text_22 = "test_status_22";

            testrun1.getMap().put(RunDataDB.STATUS, test_status_22);
            testrun1.getMap().put(RunDataDB.STATUS_TEXT, test_status_text_22);

            dal.updateRunStatus(testrun1);

            log.info("DbRunTest-updateRunStatus-updateRunStatus-OK");

            HashMap<String, String> reqmap = new HashMap<String, String>();
            reqmap.put(RunDataDB.SUBM_ID, test_subm_id);
            reqmap.put(RunDataDB.RUN_ID, test_run_id1);

            Vector<RunDataDB> records = dal.listRuns(reqmap);

            log.info("DbRunTest-updateRunStatus-listRuns-OK");

            assertTrue(records.size() > 0);

            for (RunDataDB item: records) {
                assertEquals(item.getSubmissionId(), testrun1.getSubmissionId());
                assertEquals(item.getStatus(), testrun1.getStatus());
                assertEquals(item.getStatusText(), testrun1.getStatusText());
            }

            log.info("DbRunTest-updateRunStatus-OK");

        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        log.info("DbRunTest-updateRunStatus-done");
    }

    @Test
    public void testDBupdateRunFileTime() {

        log.info("DbRunTest-updateRunFileTime");

        try {
            long test_filetime_4444 = new File("/").lastModified();

            testrun1.getMap().put(RunDataDB.FILE_TIME, test_filetime_4444);

            dal.updateRunFileTime(testrun1);

            log.info("DbRunTest-updateRunStatus-updateRunFileTime-OK");

            HashMap<String, String> reqmap = new HashMap<String, String>();
            reqmap.put(RunDataDB.SUBM_ID, test_subm_id);
            reqmap.put(RunDataDB.RUN_ID, test_run_id1);

            Vector<RunDataDB> records = dal.listRuns(reqmap);

            log.info("DbRunTest-updateRunFileTime-listRuns-OK");

            log.info("DbRunTest-updateRunFileTime-records.size()={}", records.size());

            assertTrue(records.size() > 0);

            for (RunDataDB item: records) {
                log.info("DbRunTest-updateRunFileTime-item.getSubmissionId={} testrun1.getSubmissionId={}",
                        item.getSubmissionId(), testrun1.getSubmissionId());

                assertTrue(item.getSubmissionId().equals(testrun1.getSubmissionId()));

                log.info("DbRunTest-updateRunFileTime-item.getFileTime={} testrun1.getFileTime={}",
                        item.getFileTime(), testrun1.getFileTime());

                assertEquals(item.getFileTime(), testrun1.getFileTime());
            }

            log.info("DbRunTest-updateRunFileTime-OK");

        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        log.info("DbRunTest-updateRunFileTime-done");
    }

}
