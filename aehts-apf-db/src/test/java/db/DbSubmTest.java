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
import db.exception.AlreadyExistsException;
import db.exception.GeneralException;
import db.sql.BaseSQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;

import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 26/03/2012
 * Time: 13:28
 * To change this template use File | Settings | File Templates.
 */
public class DbSubmTest {

    private static final Logger log = LoggerFactory.getLogger(DbSubmTest.class);

    private DAL dal;
    private SubmDataDB testexp;

    private String test_subm_id = "TEST-SUBM-ID-00001";
    private String test_exp_id = "E-TEST-16190-0";
    private String test_status = "UNDEFINED2";
    private String test_status_text = "UNDEFINED2_TEXT";
    private String test_submitter = "Vasja Pupkin";
    private String test_user_notes = "Test Submission";
    private Integer test_notified = new Integer(0);
    private Integer test_copied = new Integer(0);

    public DbSubmTest() {
        try {
            dal = DALDefaults.getDAL();
        } catch (Exception ex) {
            log.error("Error!", ex);
        }

        assertTrue((dal != null));

        //log.info("DbSubmTest-DbSubmTest-DAL-created");
    }

    @Before
    public void prepare() throws InstantiationException {

        //log.info("DbSubmTest-prepare");

        HashMap<String, Object> expmap01 = new HashMap<String, Object>();

        expmap01.put(SubmDataDB.SUBM_ID,     test_subm_id);
        expmap01.put(SubmDataDB.EXP_ID,      test_exp_id);
        expmap01.put(SubmDataDB.STATUS,      test_status);
        expmap01.put(SubmDataDB.STATUS_TEXT, test_status_text);
        expmap01.put(SubmDataDB.SUBMITTER,   test_submitter);
        expmap01.put(SubmDataDB.USER_NOTES,  test_user_notes);
        expmap01.put(SubmDataDB.NOTIFIED,    test_notified);
        expmap01.put(SubmDataDB.COPIED,      test_copied);

        testexp = new SubmDataDB(expmap01);

        try {
            dal.createSubm(testexp);

            //log.info("DbSubmTest-prepare-createSubm-OK");
        } catch (AlreadyExistsException aee) {
            log.info("Info!", aee);
        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }
    }

    @After
    public void cleanup() {
        //log.info("DbSubmTest-cleanup");

        try {
            dal.deleteSubm(testexp);
        } catch (GeneralException ge) {
            log.error("Error!", ge);
            assertTrue(false);
        }
    }

    @Test
    public void testDBAlreadyExists() {

        log.info("DbSubmTest-testDBAlreadyExists");

        try {
            dal.createSubm(testexp);

            // assert if returned
            // without exception
            assertTrue(false);

        } catch (AlreadyExistsException aee) {
            // OK
            log.info("DbSubmTest-testDBAlreadyExists-OK");

        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        log.info("DbSubmTest-testDBAlreadyExists-done");
    }

    @Test
    public void testDBgetSubm() {

        log.info("DbSubmTest-getSubm");
        
        try {
            HashMap<String, Object> expmap02 = new HashMap<String, Object>();
    
            expmap02.put(SubmDataDB.SUBM_ID, test_subm_id);
            expmap02.put(SubmDataDB.EXP_ID,  test_exp_id);
    
            SubmDataDB exp02 = new SubmDataDB(expmap02);

            SubmDataDB exp03 = dal.getSubm(exp02);

            assertTrue(exp03 != null);
            assertEquals(exp03.getSubmissionId(), testexp.getSubmissionId());
            assertEquals(exp03.getExperimentId(), testexp.getExperimentId());

            log.info("DbSubmTest-getSubm-OK");

        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        log.info("DbSubmTest-getSubm-done");
    }

    @Test
    public void testDBlistSubms() {

        log.info("DbSubmTest-listSubms");

        try {
            HashMap<String, String> reqmap = new HashMap<String, String>();
            reqmap.put(SubmDataDB.EXP_ID, test_exp_id);
            
            Vector<SubmDataDB> records = dal.listSubms(reqmap);
            
            log.info("DbSubmTest-listSubms-OK");

            assertTrue(records.size() > 0);

        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        log.info("DbSubmTest-listSubms-done");
    }

    @Test
    public void testDBgetSubmData() {

        log.info("DbSubmTest-getSubmData");

        try {
            Vector<SubmDataDB> records =
                    dal.getSubmData(SubmDataDB.EXP_ID + " = " + BaseSQL.wrap(test_exp_id));
            
            assertTrue(records.size() > 0);

            log.info("DbSubmTest-getSubmData-OK");

        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        log.info("DbSubmTest-getSubmData-done");
    }

    @Test
    public void testDBupdateSubm() {

        log.info("DbSubmTest-updateSubm");

        try {
            String test_status_2 = "test_status_2";
            String test_master_2 = "test_master_2";
            String test_ens_ver_2 = "test_ens_ver_2";
            String test_comment_2 = "test_comment_2";

            testexp.getMap().put(SubmDataDB.STATUS, test_status_2);
            testexp.getMap().put(SubmDataDB.MASTER_NAME, test_master_2);
            testexp.getMap().put(SubmDataDB.ENSEMBL_REL_VER, test_ens_ver_2);
            testexp.getMap().put(SubmDataDB.USER_NOTES, test_comment_2);

            dal.updateSubm(testexp);

            log.info("DbSubmTest-updateSubm-updateSubm-OK");

            HashMap<String, String> reqmap = new HashMap<String, String>();
            reqmap.put(SubmDataDB.SUBM_ID, test_subm_id);
            reqmap.put(SubmDataDB.EXP_ID, test_exp_id);

            Vector<SubmDataDB> records = dal.listSubms(reqmap);

            log.info("DbSubmTest-updateSubm-listSubms-OK");

            assertTrue(records.size() > 0);

            for (SubmDataDB item: records) {
                assertEquals(item.getSubmissionId(), testexp.getSubmissionId());
                assertEquals(item.getExperimentId(), testexp.getExperimentId());
                assertEquals(item.getEnsemblRelVer(), test_ens_ver_2);
                assertEquals(item.getMasterServerName(), test_master_2);
                assertEquals(item.getUserNotes(), test_comment_2);
                assertEquals(item.getStatus(), test_status_2);
            }

            log.info("DbSubmTest-updateSubm-OK");

        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        log.info("DbSubmTest-updateSubm-done");
    }

    @Test
    public void testDBupdateSubmNotified() {

        log.info("DbSubmTest-update");

        try {
            // delay to allow difference between
            // submit time and finish time
            //

            String test_status_3 = "test_status_3";
            String test_status_text_3 = "test_status_text_3";
            String test_master_3 = "test_master_3";
            String test_ens_ver_3 = "test_ens_ver_3";
            String test_comment_3 = "test_comment_3";
            long test_file_time_3 = new File("/").lastModified();

            testexp.getMap().put(SubmDataDB.STATUS, test_status_3);
            testexp.getMap().put(SubmDataDB.STATUS_TEXT, test_status_text_3);
            testexp.getMap().put(SubmDataDB.MASTER_NAME, test_master_3);
            testexp.getMap().put(SubmDataDB.ENSEMBL_REL_VER, test_ens_ver_3);
            testexp.getMap().put(SubmDataDB.USER_NOTES, test_comment_3);
            testexp.getMap().put(SubmDataDB.FILE_TIME, test_file_time_3);

            dal.updateSubmStatus(testexp);
            dal.updateSubmMaster(testexp);
            dal.updateSubmRelver(testexp);
            dal.updateSubmNotified(testexp);
            dal.updateSubmCopied(testexp);
            dal.updateSubmFileTime(testexp);
            dal.updateSubmComment(testexp);
            dal.updateSubmMetadata(testexp);
            dal.chnageStatusForManySubms("NO_STATUS_DUMMY", "NO_STATUS_DUMMY_2", "");

            Thread.sleep(1000);

            dal.updateSubmStartTime(testexp);

            Thread.sleep(1000);

            dal.updateSubmFinishTime(testexp);

            log.info("DbSubmTest-update-updateSubmStatus-OK");

            HashMap<String, Object> reqmap = new HashMap<String, Object>();
            reqmap.put(SubmDataDB.SUBM_ID, test_subm_id);
            reqmap.put(SubmDataDB.EXP_ID, test_exp_id);

            SubmDataDB exp = dal.getSubm(new SubmDataDB(reqmap));

            assertTrue(exp != null);

            log.info("DbSubmTest-update-getSubm-OK");

            assertEquals(exp.getSubmissionId(), testexp.getSubmissionId());

            log.info("DbSubmTest-update-getSubm-getSubmissionId-OK");

            assertEquals(exp.getExperimentId(), testexp.getExperimentId());

            log.info("DbSubmTest-update-getSubm-getExpId-OK");

            assertEquals(exp.getEnsemblRelVer(), test_ens_ver_3);

            log.info("DbSubmTest-update-getSubm-getEnsemblRelVer-OK");

            assertEquals(exp.getStatus(), test_status_3);

            log.info("DbSubmTest-update-getSubm-getStatus-OK");

            assertEquals(exp.getUserNotes(), test_comment_3);

            log.info("DbSubmTest-update-getSubm-getUserNotes-OK");

            assertEquals(exp.getMasterServerName(), test_master_3);

            log.info("DbSubmTest-update-getSubm-getMasterServerName-OK");

            assertTrue(exp.getNotified().intValue() > testexp.getNotified().intValue());

            log.info("DbSubmTest-update-getSubm-exp.getNotified() > testexp.getNotified()-OK");

            assertTrue(exp.getCopied().intValue() > testexp.getCopied().intValue());

            log.info("DbSubmTest-update-getSubm-exp.getCopied() > testexp.getCopied()-OK");

            assertTrue(exp.getStartTime().getTime() > exp.getSubmitTime().getTime());

            log.info("DbSubmTest-update-getSubm-exp.getStartTime() > exp.getSubmitTime()-OK");

            assertTrue(exp.getFinishTime().getTime() > exp.getStartTime().getTime());

            log.info("DbSubmTest-update-getSubm-exp.getFinishTime() > exp.getStartTime()-OK");

            assertTrue(exp.getFileTime() == testexp.getFileTime());

            log.info("DbSubmTest-update-exp.getFileTime() == testexp.getFileTime()-OK");

            log.info("DbSubmTest-update-OK");

        } catch (InterruptedException ie) {
            // OK
        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        log.info("DbSubmTest-update-done");
    }

    @Test
    public void testDBdeleteMutlSubms() {

        log.info("DbSubmTest-deleteMutlSubms");

        String test_submitter_01 = "Vasja Pupkin Vasilievich";

        int numrecords = 5;

        try {
            for(int i = 1;i <= numrecords;i++) {
                HashMap<String, Object> expmap01 = new HashMap<String, Object>();
        
                expmap01.put(SubmDataDB.SUBM_ID, test_subm_id + "_0" + i);
                expmap01.put(SubmDataDB.EXP_ID,  test_exp_id +  "_0" + i);
                expmap01.put(SubmDataDB.STATUS,  test_status);
                expmap01.put(SubmDataDB.SUBMITTER,  test_submitter_01);

                try {
                    dal.createSubm(new SubmDataDB(expmap01));
        
                    log.info("DbSubmTest-deleteMutlSubms-createSubm-OK");
                } catch (AlreadyExistsException aee) {
                    log.info("Info!", aee);
                } catch (GeneralException ge) {
                    log.error("Error!", ge);
        
                    assertTrue(false);
                }
            }

            // list, check
            //

            HashMap<String, String> reqmap01 = new HashMap<String, String>();
            reqmap01.put(SubmDataDB.SUBMITTER, test_submitter_01);

            Vector<SubmDataDB> records01 = dal.listSubms(reqmap01);

            assertTrue(records01.size() == numrecords);

            log.info("DbSubmTest-deleteMutlSubms-listSubms-OK");

            // remove, check
            //

            HashMap<String, String> reqmap02 = new HashMap<String, String>();
            reqmap02.put(SubmDataDB.SUBMITTER, test_submitter_01);

            dal.deleteMultSubms(reqmap02);

            log.info("DbSubmTest-deleteMutlSubms-deleteMultSubms-OK");

            // list, check
            //

            HashMap<String, String> reqmap03 = new HashMap<String, String>();
            reqmap03.put(SubmDataDB.SUBMITTER, test_submitter_01);

            Vector<SubmDataDB> records02 = dal.listSubms(reqmap03);

            assertTrue(records02.size() == 0);

            log.info("DbSubmTest-deleteMutlSubms-listSubms-OK");

        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        log.info("DbSubmTest-deleteMultSubms-done");
    }
    
    @Test
    public void testDBcreateSubmAutoname() {


        log.info("DbSubmTest-testDBcreateSubmAutoname");

        String test_submitter_01 = "Vasja Pupkin Vasilievich";

        int numrecords = 5;

        try {
            for(int i = 1;i <= numrecords;i++) {
                HashMap<String, Object> expmap01 = new HashMap<String, Object>();

                expmap01.put(SubmDataDB.EXP_ID,      test_exp_id +  "_0" + i);
                expmap01.put(SubmDataDB.STATUS,      test_status);
                expmap01.put(SubmDataDB.STATUS_TEXT, test_status_text);
                expmap01.put(SubmDataDB.SUBMITTER,   test_submitter_01);

                try {
                    dal.createSubmAutoname(new SubmDataDB(expmap01));

                    log.info("DbSubmTest-testDBcreateSubmAutoname-createSubm-OK");
                } catch (GeneralException ge) {
                    log.error("Error!", ge);

                    assertTrue(false);
                }
            }

            // list, check
            //

            HashMap<String, String> reqmap01 = new HashMap<String, String>();
            reqmap01.put(SubmDataDB.SUBMITTER, test_submitter_01);

            Vector<SubmDataDB> records01 = dal.listSubms(reqmap01);

            assertTrue(records01.size() == numrecords);

            log.info("DbSubmTest-testDBcreateSubmAutoname-listSubms-OK");

            for(SubmDataDB item: records01) {
                log.info("DbSubmTest-testDBcreateSubmAutoname-Subm:{}", item.getSubmissionId());
            }

            // remove, check
            //

            HashMap<String, String> reqmap02 = new HashMap<String, String>();
            reqmap02.put(SubmDataDB.SUBMITTER, test_submitter_01);

            dal.deleteMultSubms(reqmap02);

            log.info("DbSubmTest-testDBcreateSubmAutoname-deleteMultSubms-OK");

            // list, check
            //

            HashMap<String, String> reqmap03 = new HashMap<String, String>();
            reqmap03.put(SubmDataDB.SUBMITTER, test_submitter_01);

            Vector<SubmDataDB> records02 = dal.listSubms(reqmap03);

            assertTrue(records02.size() == 0);

            log.info("DbSubmTest-testDBcreateSubmAutoname-listSubms-OK");

        } catch (GeneralException ge) {
            log.error("Error!", ge);

            assertTrue(false);
        }

        log.info("DbSubmTest-testDBcreateSubmAutoname-done");
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