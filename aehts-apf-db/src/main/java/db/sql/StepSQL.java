package db.sql;

import db.data.StepDataDB;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 22/03/2012
 * Time: 15:35
 * To change this template use File | Settings | File Templates.
 */
public class StepSQL extends BaseSQL {

    public static final String TABNAME = "step";

    public static String keyStatement(String step_id) {
        return StepDataDB.STEP_ID + " = " + wrap(step_id);
    }

    public static String keyStatement(StepDataDB step) {
        return keyStatement(step.getStepId());
    }

    public static String addStepStatement(StepDataDB step) {
        HashMap<String, String> valuesmap = new HashMap<String, String>();

        valuesmap.put(StepDataDB.STEP_ID, wrap(step.getStepId()));
        valuesmap.put(StepDataDB.DESCRIPTION, wrap(step.getDescription()));

        return genericInsertStatement(TABNAME, valuesmap);
    }

    public static String checkExistsStatement(StepDataDB step) {
        return genericExistsStatement(TABNAME, keyStatement(step));
    }

    public static String deleteStepStatement(StepDataDB step) {
        return genericDeleteStatement(TABNAME, keyStatement(step));
    }

    public static String updateDescriptionStatement(StepDataDB step) {
        return genericUpdateStatement(TABNAME,
                StepDataDB.DESCRIPTION + " = " + wrap(step.getDescription()),
                keyStatement(step.getStepId()));
    }

}
