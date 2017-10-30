package db.sql;

import db.data.RunDataDB;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 22/03/2012
 * Time: 14:28
 * To change this template use File | Settings | File Templates.
 */
public class RunSQL extends BaseSQL {

    public static final String TABNAME = "run";

    public static String keyStatement(String subm_id, String run_id) {
        return RunDataDB.SUBM_ID + " = " + wrap(subm_id) + " and " +
               RunDataDB.RUN_ID + " = " + wrap(run_id);
    }

    public static String keyStatement(RunDataDB run) {
        return keyStatement(run.getSubmissionId(), run.getRunId());
    }

    public static String addRunStatement(RunDataDB run) {
        HashMap<String, String> valuesmap = new HashMap<String, String>();

        valuesmap.put(RunDataDB.SUBM_ID, wrap(run.getSubmissionId()));
        valuesmap.put(RunDataDB.RUN_ID, wrap(run.getRunId()));
        valuesmap.put(RunDataDB.EXP_ID, wrap(run.getExperimentId()));
        valuesmap.put(RunDataDB.STATUS, wrap(run.getStatus()));
        valuesmap.put(RunDataDB.STATUS_TEXT, wrap(run.getStatusText()));
        valuesmap.put(RunDataDB.FILE_TIME, "0");

        return genericInsertStatement(TABNAME, valuesmap);
    }

    public static String updateFileTimeStatement(RunDataDB run) {
        return genericUpdateStatement(TABNAME, RunDataDB.FILE_TIME + " = " + run.getFileTime(),
                keyStatement(run));
    }

    public static String checkExistsStatement(RunDataDB run) {
        return genericExistsStatement(TABNAME, keyStatement(run));
    }

    public static String deleteRunStatement(RunDataDB run) {
        return genericDeleteStatement(TABNAME, keyStatement(run));
    }

    public static String updateStatusStatement(RunDataDB run) {
        return genericUpdateStatement(TABNAME,
                RunDataDB.STATUS + " = " + wrap(run.getStatus()) + "," +
                RunDataDB.STATUS_TEXT + " = " + wrap(run.getStatusText()),
                keyStatement(run));
    }

}
