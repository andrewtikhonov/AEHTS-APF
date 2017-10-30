package db.sql;

import db.data.RunStepDataDB;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 22/03/2012
 * Time: 15:13
 * To change this template use File | Settings | File Templates.
 */
public class RunStepSQL extends BaseSQL {

    public static final String TABNAME = "runstep";

    public static String keyStatement(String subm_id, String run_id, String step_id) {
        return RunStepDataDB.SUBM_ID + " = " + wrap(subm_id) + " and " +
               RunStepDataDB.RUN_ID + " = " + wrap(run_id) + " and " +
               RunStepDataDB.STEP_ID + " = " + wrap(step_id);
    }

    public static String keyStatement(RunStepDataDB runstep) {
        return keyStatement(runstep.getSubmissionId(), runstep.getRunId(), runstep.getStepId());
    }

    public static String addRunStepStatement(RunStepDataDB runstep) {
        HashMap<String, String> valuesmap = new HashMap<String, String>();

        valuesmap.put(RunStepDataDB.SUBM_ID, wrap(runstep.getSubmissionId()));
        valuesmap.put(RunStepDataDB.RUN_ID, wrap(runstep.getRunId()));
        valuesmap.put(RunStepDataDB.STEP_ID, wrap(runstep.getStepId()));
        valuesmap.put(RunStepDataDB.STATUS, wrap(runstep.getStatus()));
        valuesmap.put(RunStepDataDB.STATUS_TEXT, wrap(runstep.getStatusText()));

        return genericInsertStatement(TABNAME, valuesmap);
    }

    public static String checkExistsStatement(RunStepDataDB runstep) {
        return genericExistsStatement(TABNAME, keyStatement(runstep));
    }

    public static String deleteRunStepStatement(RunStepDataDB runstep) {
        return genericDeleteStatement(TABNAME, keyStatement(runstep));
    }

    public static String updateStatusStatement(RunStepDataDB runstep) {
        return genericUpdateStatement(TABNAME,
                RunStepDataDB.STATUS + " = " + wrap(runstep.getStatus())  + "," +
                RunStepDataDB.STATUS_TEXT + " = " + wrap(runstep.getStatusText()),
                keyStatement(runstep));
    }

}
