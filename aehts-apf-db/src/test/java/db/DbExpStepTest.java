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
import db.data.ExpStepDataDB;
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
 * Date: 28/03/2012
 * Time: 16:14
 * To change this template use File | Settings | File Templates.
 */
public class DbExpStepTest {
    
    private static final Logger log = LoggerFactory.getLogger(DbExpStepTest.class);

    private DAL dal;
    private SubmDataDB testexp;
    private ExpStepDataDB testexpstep1;

    private String test_subm_id = "TEST-SUBM-ID-001";
    private String test_exp_id = "E-TEST-16190";
    private String test_user_notes = "Test Submission";
    private String test_step_id1 = "STEP-ID-TEST-0001";
    private String test_step_id2 = "STEP-ID-TEST-0002";
    private String test_status = "UNDEFINED";
    private String test_status_text = "Test Status Text";
    private String test_submitter = "Vasja Pupkin";

    public DbExpStepTest() {

        try {
            dal = DALDefaults.getDAL();
        } catch (Exception ex) {
            log.error("Error!", ex);
        }

        assertTrue((dal != null));

        //log.info("DbExpStepTest-DbExpStepTest-DAL-created");
    }

    @Before
    public void prepare() throws InstantiationException {

        //log.info("DbExpStepTest-prepare");

        HashMap<String, Object> expmap01 = new HashMap<String, Object>();

        expmap01.put(SubmDataDB.SUBM_ID,      test_subm_id);
        expmap01.put(SubmDataDB.EXP_ID,       test_exp_id);
        expmap01.put(SubmDataDB.STATUS,       test_status);
        expmap01.put(SubmDataDB.STATUS_TEXT,  test_status_text);
        expmap01.put(SubmDataDB.USER_NOTES,   test_user_notes);
        expmap01.put(SubmDataDB.SUBMITTER,    test_submitter);

        testexp = new SubmDataDB(expmap01);

        try {
            dal.createSubm(testexp);

            //log.info("DbExpStepTest-prepare-createSubm-OK");
        } catch (AlreadyExistsException aee) {
            log.info("Info!", aee);
        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        HashMap<String, Object> ExpStepmap01 = new HashMap<String, Object>();

        ExpStepmap01.put(ExpStepDataDB.SUBM_ID, test_subm_id);
        ExpStepmap01.put(ExpStepDataDB.EXP_ID,  test_exp_id);
        ExpStepmap01.put(ExpStepDataDB.STEP_ID,  test_step_id1);
        ExpStepmap01.put(ExpStepDataDB.STATUS,  test_status);
        ExpStepmap01.put(ExpStepDataDB.STATUS_TEXT,  test_status_text);

        testexpstep1 = new ExpStepDataDB(ExpStepmap01);

        try {
            dal.createExpStep(testexpstep1);

            //log.info("DbExpStepTest-prepare-createExpStep-OK");
        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        //log.info("DbExpStepTest-prepare-done");
    }

    @After
    public void cleanup() {
        //log.info("DbExpStepTest-cleanup");

        try {
            dal.deleteExpStep(testexpstep1);
            dal.deleteSubm(testexp);
        } catch (GeneralException ge) {
            log.error("Error!", ge);
            assertTrue(false);
        }
    }

    @Test
    public void testDBconstraintViolation() {

        log.info("DbExpStepTest-testDBconstraintViolation");

        String test_nonexistent_subm_id = test_subm_id + "ASD002";


        HashMap<String, Object> runmap01 = new HashMap<String, Object>();

        runmap01.put(ExpStepDataDB.SUBM_ID, test_nonexistent_subm_id);
        runmap01.put(ExpStepDataDB.EXP_ID,  test_exp_id);
        runmap01.put(ExpStepDataDB.STEP_ID,  test_exp_id);
        runmap01.put(ExpStepDataDB.STATUS,  test_status);
        runmap01.put(ExpStepDataDB.STATUS_TEXT,  test_status_text);

        try {
            dal.createExpStep(new ExpStepDataDB(runmap01));

            log.info("DbExpStepTest-testDBconstraintViolation-Failed");

            assertTrue(false);
        } catch (GeneralException ge) {
            if (ge.getCause() instanceof SQLIntegrityConstraintViolationException) {
                // OK

                log.info("DbExpStepTest-constraintViolation-OK");
            } else {
                log.error("Error!", ge);
                assertTrue(false);
            }
        } finally {
            try {
                dal.deleteExpStep(new ExpStepDataDB(runmap01));
            } catch (Exception ex) {
            }
        }
    }

    @Test
    public void testDBgetExpStep() {

        log.info("DbExpStepTest-getExpStep");

        try {
            HashMap<String, Object> runmap02 = new HashMap<String, Object>();

            runmap02.put(ExpStepDataDB.SUBM_ID, test_subm_id);
            runmap02.put(ExpStepDataDB.EXP_ID,  test_exp_id);
            runmap02.put(ExpStepDataDB.STEP_ID, test_step_id1);

            ExpStepDataDB run03 = dal.getExpStep(new ExpStepDataDB(runmap02));

            assertTrue(run03 != null);
            assertEquals(run03.getSubmissionId(), testexpstep1.getSubmissionId());
            assertEquals(run03.getExperimentId(), testexpstep1.getExperimentId());
            assertEquals(run03.getStepId(),  testexpstep1.getStepId());
            assertEquals(run03.getStatus(),  testexpstep1.getStatus());
            assertEquals(run03.getStatusText(),  testexpstep1.getStatusText());

            log.info("DbExpStepTest-getExpStep-OK");

        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        log.info("DbExpStepTest-getExpStep-done");
    }

    @Test
    public void testDBlistExpSteps() {

        log.info("DbExpStepTest-listExpSteps");

        try {
            HashMap<String, String> reqmap = new HashMap<String, String>();
            reqmap.put(ExpStepDataDB.SUBM_ID, test_subm_id);

            Vector<ExpStepDataDB> records = dal.listExpSteps(reqmap);

            log.info("DbExpStepTest-listExpSteps-OK");

            assertTrue(records.size() > 0);

        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        log.info("DbExpStepTest-listExpSteps-done");
    }

    @Test
    public void testDBgetExpStepData() {

        log.info("DbExpStepTest-getExpStepData");

        try {
            Vector<ExpStepDataDB> records =
                    dal.getExpStepData(ExpStepDataDB.EXP_ID+ " = " + BaseSQL.wrap(test_exp_id));

            assertTrue(records.size() > 0);

            log.info("DbExpStepTest-getExpStepData-OK");

        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        log.info("DbExpStepTest-getExpStepData-done");
    }

    @Test
    public void testDBupdateRunStatus() {

        log.info("DbExpStepTest-updateExpStepStatus");

        try {
            String test_status_22446688 = "test_status_22446688";
            String test_status_text_22446688 = "test_status_text_22446688";

            testexpstep1.getMap().put(ExpStepDataDB.STATUS, test_status_22446688);
            testexpstep1.getMap().put(ExpStepDataDB.STATUS_TEXT, test_status_text_22446688);

            dal.updateExpStepStatus(testexpstep1);

            log.info("DbExpStepTest-updateExpStepStatus-updateExpStepStatus-OK");

            HashMap<String, String> reqmap = new HashMap<String, String>();
            reqmap.put(ExpStepDataDB.SUBM_ID, test_subm_id);
            reqmap.put(ExpStepDataDB.EXP_ID, test_exp_id);
            reqmap.put(ExpStepDataDB.STEP_ID, test_step_id1);

            Vector<ExpStepDataDB> records = dal.listExpSteps(reqmap);

            log.info("DbExpStepTest-updateExpStepStatus-listExpSteps-OK");

            assertTrue(records.size() > 0);

            for (ExpStepDataDB item: records) {
                assertEquals(item.getSubmissionId(), testexpstep1.getSubmissionId());
                assertEquals(item.getExperimentId(), testexpstep1.getExperimentId());
                assertEquals(item.getStepId(), testexpstep1.getStepId());
                assertEquals(item.getStatus(), testexpstep1.getStatus());
                assertEquals(item.getStatusText(), testexpstep1.getStatusText());
            }

            log.info("DbExpStepTest-updateExpStepStatus-OK");

        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        log.info("DbExpStepTest-updateExpStepStatus-done");
    }

    @Test
    public void testDBdeleteMutlExpSteps() {

        log.info("DbExpStepTest-deleteMutlExpSteps");

        String test_subm_id_01 = test_subm_id + "_01";
        String test_exp_id_01 = test_exp_id + "_01";
        int numrecords = 5;

        try {
            HashMap<String, Object> expmap01 = new HashMap<String, Object>();
    
            expmap01.put(SubmDataDB.SUBM_ID,      test_subm_id_01);
            expmap01.put(SubmDataDB.EXP_ID,       test_exp_id_01);
            expmap01.put(SubmDataDB.STATUS,       test_status);
            expmap01.put(SubmDataDB.STATUS_TEXT,  test_status_text);
            expmap01.put(SubmDataDB.SUBMITTER,    test_submitter);
    
            try {
                dal.createSubm(new SubmDataDB(expmap01));

                log.info("DbExpStepTest-deleteMultExpSteps-createSubm-OK");
            } catch (AlreadyExistsException aee) {
                log.info("Info!", aee);
                assertTrue(false);
            } catch (GeneralException ge) {
                log.error("Error!", ge);
                assertTrue(false);
            }
            
            
            for(int i = 1;i <= numrecords;i++) {
                HashMap<String, Object> expstepmap01 = new HashMap<String, Object>();
        
                expstepmap01.put(ExpStepDataDB.SUBM_ID, test_subm_id_01);
                expstepmap01.put(ExpStepDataDB.EXP_ID,  test_exp_id_01);
                expstepmap01.put(ExpStepDataDB.STEP_ID, test_step_id1 +  "_0" + i);
                expstepmap01.put(ExpStepDataDB.STATUS,  test_status);
                expstepmap01.put(ExpStepDataDB.STATUS_TEXT,  test_status_text);

                try {
                    dal.createExpStep(new ExpStepDataDB(expstepmap01));
        
                    log.info("DbExpStepTest-deleteMultExpSteps-createExpStep-OK");
                } catch (GeneralException ge) {
                    log.error("Error!", ge);
        
                    assertTrue(false);
                }
            }

            // list, check
            //

            HashMap<String, String> reqmap01 = new HashMap<String, String>();
            reqmap01.put(ExpStepDataDB.EXP_ID, test_exp_id_01);

            Vector<ExpStepDataDB> records01 = dal.listExpSteps(reqmap01);

            assertTrue(records01.size() == numrecords);

            log.info("DbExpStepTest-deleteMultExpSteps-listExpSteps-OK");

            // remove, check
            //

            HashMap<String, String> reqmap02 = new HashMap<String, String>();
            reqmap02.put(ExpStepDataDB.EXP_ID, test_exp_id_01);

            dal.deleteMultExpSteps(reqmap02);

            log.info("DbExpStepTest-deleteMultExpSteps-deleteMultExpSteps-OK");

            // list, check
            //

            HashMap<String, String> reqmap03 = new HashMap<String, String>();
            reqmap03.put(ExpStepDataDB.EXP_ID, test_exp_id_01);

            Vector<ExpStepDataDB> records02 = dal.listExpSteps(reqmap03);

            assertTrue(records02.size() == 0);

            log.info("DbExpStepTest-deleteMutlExps-listSubms-OK");

            dal.deleteSubm(new SubmDataDB(expmap01));

            log.info("DbExpStepTest-deleteMutlExps-deleteSubm-OK");

        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        log.info("DbExpStepTest-deleteMultSubms-done");
    }
}
