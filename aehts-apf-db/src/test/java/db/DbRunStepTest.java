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
import db.data.RunStepDataDB;
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
 * Date: 27/03/2012
 * Time: 16:53
 * To change this template use File | Settings | File Templates.
 */
public class DbRunStepTest {

    private static final Logger log = LoggerFactory.getLogger(DbRunStepTest.class);

    private DAL dal;
    private SubmDataDB testexp;
    private RunStepDataDB testrunstep1;
    private RunStepDataDB testrunstep2;

    private String test_subm_id = "TEST-SUBM-ID-001";
    private String test_exp_id = "E-TEST-16190";
    private String test_user_notes = "Test Comment";
    private String test_run_id1 = "SRR-TEST-0001";
    private String test_run_id2 = "SRR-TEST-0002";
    private String test_step_id1 = "STEP-ID-TEST-0001";
    private String test_step_id2 = "STEP-ID-TEST-0002";
    private String test_status = "UNDEFINED";
    private String test_status_text = "Test Status Text";
    private String test_submitter = "Vasja Pupkin";

    public DbRunStepTest() {
        try {
            dal = DALDefaults.getDAL();
        } catch (Exception ex) {
            log.error("Error!", ex);
        }

        assertTrue((dal != null));

        //log.info("DbRunStepTest-DbRunStepTest-DAL-created");
    }

    @Before
    public void prepare() throws InstantiationException {

        //log.info("DbRunStepTest-prepare");

        HashMap<String, Object> expmap01 = new HashMap<String, Object>();

        expmap01.put(SubmDataDB.SUBM_ID,     test_subm_id);
        expmap01.put(SubmDataDB.EXP_ID,      test_exp_id);
        expmap01.put(SubmDataDB.STATUS,      test_status);
        expmap01.put(SubmDataDB.STATUS_TEXT, test_status_text);
        expmap01.put(SubmDataDB.SUBMITTER,   test_submitter);
        expmap01.put(SubmDataDB.USER_NOTES,  test_user_notes);

        testexp = new SubmDataDB(expmap01);

        try {
            dal.createSubm(testexp);

            //log.info("DbRunStepTest-prepare-createExperiment-OK");
        } catch (AlreadyExistsException aee) {
            log.info("Info!", aee);
        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        HashMap<String, Object> runstepmap01 = new HashMap<String, Object>();

        runstepmap01.put(RunStepDataDB.SUBM_ID, test_subm_id);
        runstepmap01.put(RunStepDataDB.RUN_ID,  test_run_id1);
        runstepmap01.put(RunStepDataDB.STEP_ID,  test_step_id1);
        runstepmap01.put(RunStepDataDB.STATUS,  test_status);
        runstepmap01.put(RunStepDataDB.STATUS_TEXT,  test_status_text);

        testrunstep1 = new RunStepDataDB(runstepmap01);

        try {
            dal.createRunStep(testrunstep1);

            //log.info("DbRunStepTest-prepare-createRunStep-OK");
        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        HashMap<String, Object> runstepmap02 = new HashMap<String, Object>();

        runstepmap02.put(RunStepDataDB.SUBM_ID, test_subm_id);
        runstepmap02.put(RunStepDataDB.RUN_ID,  test_run_id1);
        runstepmap02.put(RunStepDataDB.STEP_ID,  test_step_id2);
        runstepmap02.put(RunStepDataDB.STATUS,  test_status);
        runstepmap02.put(RunStepDataDB.STATUS_TEXT,  test_status_text);

        testrunstep2 = new RunStepDataDB(runstepmap02);

        try {
            dal.createRunStep(testrunstep2);

            //log.info("DbRunStepTest-prepare-createRunStep-OK");
        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }


        //log.info("DbRunStepTest-prepare-done");
    }

    @After
    public void cleanup() {
        //log.info("DbRunStepTest-cleanup");

        try {
            dal.deleteRunStep(testrunstep1);
            dal.deleteRunStep(testrunstep2);
            dal.deleteSubm(testexp);
        } catch (GeneralException ge) {
            log.error("Error!", ge);
            assertTrue(false);
        }
    }

    @Test
    public void testDBconstraintViolation() {

        log.info("DbRunTest-constraintViolation");

        String test_nonexistent_subm_id = test_subm_id + "ASD002";


        HashMap<String, Object> runmap01 = new HashMap<String, Object>();

        runmap01.put(RunStepDataDB.SUBM_ID, test_nonexistent_subm_id);
        runmap01.put(RunStepDataDB.RUN_ID,  test_run_id1);
        runmap01.put(RunStepDataDB.STEP_ID,  test_exp_id);
        runmap01.put(RunStepDataDB.STATUS,  test_status);
        runmap01.put(RunStepDataDB.STATUS_TEXT,  test_status_text);

        try {
            dal.createRunStep(new RunStepDataDB(runmap01));

            log.info("DbRunTest-constraintViolation-Failed");

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
                dal.deleteRunStep(new RunStepDataDB(runmap01));
            } catch (Exception ex) {
            }
        }

        log.info("DbRunTest-constraintViolation-done");
    }

    @Test
    public void testDBgetRunStep() {

        log.info("DbRunStepTest-getRunStep");

        try {
            HashMap<String, Object> runmap02 = new HashMap<String, Object>();

            runmap02.put(RunStepDataDB.SUBM_ID, test_subm_id);
            runmap02.put(RunStepDataDB.RUN_ID,  test_run_id1);
            runmap02.put(RunStepDataDB.STEP_ID, test_step_id1);

            RunStepDataDB run03 = dal.getRunStep(new RunStepDataDB(runmap02));

            assertTrue(run03 != null);
            assertEquals(run03.getSubmissionId(), testrunstep1.getSubmissionId());
            assertEquals(run03.getRunId(),   testrunstep1.getRunId());
            assertEquals(run03.getStepId(),  testrunstep1.getStepId());
            assertEquals(run03.getStatus(),  testrunstep1.getStatus());
            assertEquals(run03.getStatusText(),  testrunstep1.getStatusText());

            log.info("DbRunStepTest-getRunStep-OK");

        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        log.info("DbRunStepTest-getRunStep-done");
    }

    @Test
    public void testDBlistRunSteps() {

        log.info("DbRunStepTest-listRunSteps");

        try {
            HashMap<String, String> reqmap = new HashMap<String, String>();
            reqmap.put(RunStepDataDB.SUBM_ID, test_subm_id);

            Vector<RunStepDataDB> records = dal.listRunSteps(reqmap);

            log.info("DbRunStepTest-listRunSteps-OK");

            assertTrue(records.size() > 0);

        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        log.info("DbRunStepTest-listRunSteps-done");
    }

    @Test
    public void testDBgetRunStepData() {

        log.info("DbRunStepTest-getRunStepData");

        try {
            Vector<RunStepDataDB> records =
                    dal.getRunStepData(RunStepDataDB.RUN_ID+ " = " + BaseSQL.wrap(test_run_id1));

            assertTrue(records.size() > 0);

            log.info("DbRunStepTest-getRunStepData-OK");

        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        log.info("DbRunStepTest-getRunStepData-done");
    }

    @Test
    public void testDBupdateRunStatus() {

        log.info("DbRunStepTest-updateRunStepStatus");

        try {
            String test_status_22446688 = "test_status_22446688";
            String test_status_text_22446688 = "test_status_text_22446688";

            testrunstep1.getMap().put(RunStepDataDB.STATUS, test_status_22446688);
            testrunstep1.getMap().put(RunStepDataDB.STATUS_TEXT, test_status_text_22446688);

            dal.updateRunStepStatus(testrunstep1);

            log.info("DbRunStepTest-updateRunStepStatus-updateRunStepStatus-OK");

            HashMap<String, String> reqmap = new HashMap<String, String>();
            reqmap.put(RunStepDataDB.SUBM_ID, test_subm_id);
            reqmap.put(RunStepDataDB.RUN_ID, test_run_id1);
            reqmap.put(RunStepDataDB.STEP_ID, test_step_id1);

            Vector<RunStepDataDB> records = dal.listRunSteps(reqmap);

            log.info("DbRunStepTest-updateRunStepStatus-listRunSteps-OK");

            assertTrue(records.size() > 0);

            for (RunStepDataDB item: records) {
                assertEquals(item.getSubmissionId(), testrunstep1.getSubmissionId());
                assertEquals(item.getRunId(), testrunstep1.getRunId());
                assertEquals(item.getStepId(), testrunstep1.getStepId());
                assertEquals(item.getStatus(), testrunstep1.getStatus());
                assertEquals(item.getStatusText(), testrunstep1.getStatusText());
            }

            log.info("DbRunStepTest-updateRunStepStatus-OK");

        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        log.info("DbRunStepTest-updateRunStepStatus-done");
    }

    /*

    // steps
    public void createStep(StepDataDB step) throws AlreadyExistsException, GeneralException;
    public void updateStepDescription(StepDataDB step) throws GeneralException;
    public void deleteStep(StepDataDB step) throws GeneralException;
    public StepDataDB getStep(StepDataDB step) throws GeneralException;
    public Vector<StepDataDB> getStepData(String condition) throws GeneralException;
    */
    
}
