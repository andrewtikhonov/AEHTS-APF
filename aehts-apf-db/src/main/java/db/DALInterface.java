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

import db.data.*;
import db.data.ExpStepDataDB;
import db.exception.AlreadyExistsException;
import db.exception.GeneralException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 13/03/2012
 * Time: 16:30
 * To change this template use File | Settings | File Templates.
 */
public interface DALInterface {

    // common
    public void lock(String tabname) throws GeneralException;
    public void unlock() throws GeneralException;

    public void commit() throws SQLException;
    public boolean canReconnect();
    public Vector<HashMap<String, Object>> getTableData(String tableName) throws GeneralException;
    public Vector<HashMap<String, Object>> getTableData(String tableName, String condition) throws GeneralException;

    // submissions
    public void createSubm(SubmDataDB subm) throws AlreadyExistsException, GeneralException;
    public SubmDataDB createSubmAutoname(SubmDataDB subm) throws GeneralException;
    public void updateSubm(SubmDataDB subm) throws GeneralException;
    public void deleteSubm(SubmDataDB subm) throws GeneralException;
    public void updateSubmNotified(SubmDataDB subm) throws GeneralException;
    public void updateSubmCopied(SubmDataDB subm) throws GeneralException;
    public void updateSubmFileTime(SubmDataDB subm) throws GeneralException;
    public void updateSubmStatus(SubmDataDB subm) throws GeneralException;
    public void updateSubmStartTime(SubmDataDB subm) throws GeneralException;
    public void updateSubmFinishTime(SubmDataDB subm) throws GeneralException;
    public void updateSubmMaster(SubmDataDB subm) throws GeneralException;
    public void updateSubmRelver(SubmDataDB subm) throws GeneralException;
    public void updateSubmComment(SubmDataDB subm) throws GeneralException;
    public void updateSubmMetadata(SubmDataDB subm) throws GeneralException;
    public SubmDataDB getSubm(SubmDataDB subm) throws GeneralException;
    public Vector<SubmDataDB> getSubmData(String condition) throws GeneralException;
    public Vector<SubmDataDB> listSubms(HashMap<String, String> req) throws GeneralException;
    public void deleteMultSubms(HashMap<String, String> req) throws GeneralException;
    public void chnageStatusForManySubms(String fromStatus, String toStatus, String statusText) throws GeneralException;

    // runs
    public void createRun(RunDataDB run) throws AlreadyExistsException, GeneralException;
    public void updateRunStatus(RunDataDB run) throws GeneralException;
    public void updateRunFileTime(RunDataDB run) throws GeneralException;
    public void deleteRun(RunDataDB run) throws GeneralException;
    public RunDataDB getRun(RunDataDB run) throws GeneralException;
    public Vector<RunDataDB> getRunData(String condition) throws GeneralException;
    public Vector<RunDataDB> listRuns(HashMap<String, String> req) throws GeneralException;
    public void deleteMultRuns(HashMap<String, String> req) throws GeneralException;

    // exp steps
    public void createExpStep(ExpStepDataDB expstep) throws GeneralException;
    public void updateExpStepStatus(ExpStepDataDB expstep) throws GeneralException;
    public void deleteExpStep(ExpStepDataDB expstep) throws GeneralException;
    public ExpStepDataDB getExpStep(ExpStepDataDB expstep) throws GeneralException;
    public Vector<ExpStepDataDB> getExpStepData(String condition) throws GeneralException;
    public Vector<ExpStepDataDB> listExpSteps(HashMap<String, String> req) throws GeneralException;
    public void deleteMultExpSteps(HashMap<String, String> req) throws GeneralException;

    // run steps
    public void createRunStep(RunStepDataDB runstep) throws GeneralException;
    public void updateRunStepStatus(RunStepDataDB runstep) throws GeneralException;
    public void deleteRunStep(RunStepDataDB runstep) throws GeneralException;
    public RunStepDataDB getRunStep(RunStepDataDB runstep) throws GeneralException;
    public Vector<RunStepDataDB> getRunStepData(String condition) throws GeneralException;
    public Vector<RunStepDataDB> listRunSteps(HashMap<String, String> req) throws GeneralException;
    public void deleteMultRunSteps(HashMap<String, String> req) throws GeneralException;

    // steps
    public void createStep(StepDataDB step) throws AlreadyExistsException, GeneralException;
    public void updateStepDescription(StepDataDB step) throws GeneralException;
    public void deleteStep(StepDataDB step) throws GeneralException;
    public StepDataDB getStep(StepDataDB step) throws GeneralException;
    public Vector<StepDataDB> getStepData(String condition) throws GeneralException;
    public Vector<StepDataDB> listSteps(HashMap<String, String> req) throws GeneralException;

    // options
    public void createSetting(SettingDataDB option) throws AlreadyExistsException, GeneralException;
    public void deleteSetting(SettingDataDB option) throws GeneralException;
    public Vector<SettingDataDB> getSettingData(String condition) throws GeneralException;
    public SettingDataDB getSetting(String optionname) throws GeneralException;
    public void updateSetting(SettingDataDB option) throws GeneralException;

}
