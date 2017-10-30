package db.sql;

import db.data.ExpStepDataDB;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 22/03/2012
 * Time: 15:19
 * To change this template use File | Settings | File Templates.
 */
public class ExpStepSQL extends BaseSQL {

    public static final String TABNAME = "expstep";

    public static String keyStatement(String subm_id, String exp_id, String step_id) {
        return ExpStepDataDB.SUBM_ID + " = " + wrap(subm_id) + " and " +
               ExpStepDataDB.EXP_ID + " = " + wrap(exp_id) + " and " +
                ExpStepDataDB.STEP_ID + " = " + wrap(step_id);
    }

    public static String keyStatement(ExpStepDataDB expstep) {
        return keyStatement(expstep.getSubmissionId(), expstep.getExperimentId(), expstep.getStepId());
    }

    public static String addExpStepStatement(ExpStepDataDB expstep) {
        HashMap<String, String> valuesmap = new HashMap<String, String>();

        valuesmap.put(ExpStepDataDB.SUBM_ID, wrap(expstep.getSubmissionId()));
        valuesmap.put(ExpStepDataDB.EXP_ID, wrap(expstep.getExperimentId()));
        valuesmap.put(ExpStepDataDB.STEP_ID, wrap(expstep.getStepId()));
        valuesmap.put(ExpStepDataDB.STATUS, wrap(expstep.getStatus()));
        valuesmap.put(ExpStepDataDB.STATUS_TEXT, wrap(expstep.getStatusText()));

        return genericInsertStatement(TABNAME, valuesmap);
    }

    public static String checkExistsStatement(ExpStepDataDB expstep) {
        return genericExistsStatement(TABNAME,
                keyStatement(expstep));
    }

    public static String deleteExpStepStatement(ExpStepDataDB expstep) {
        return genericDeleteStatement(TABNAME,
                keyStatement(expstep));
    }

    public static String updateStatusStatement(ExpStepDataDB expstep) {
        return genericUpdateStatement(TABNAME,
                ExpStepDataDB.STATUS + " = " + wrap(expstep.getStatus()) + "," +
                ExpStepDataDB.STATUS_TEXT + " = " + wrap(expstep.getStatusText()),
                keyStatement(expstep));
    }

}
