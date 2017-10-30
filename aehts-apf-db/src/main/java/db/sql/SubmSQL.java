package db.sql;

import db.data.SubmDataDB;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 13/03/2012
 * Time: 16:32
 * To change this template use File | Settings | File Templates.
 */
public class SubmSQL extends BaseSQL {

    public static final String TABNAME = "submission";

    public static String keyStatement(String subm_id, String exp_id) {
        return SubmDataDB.SUBM_ID + " = " + wrap(subm_id) + " and " +
               SubmDataDB.EXP_ID + " = " + wrap(exp_id);
    }

    public static String keyStatement(SubmDataDB subm) {
        return keyStatement(subm.getSubmissionId(), subm.getExperimentId());
    }

    public static String addSubmStatement(SubmDataDB subm, String sysdate) {
        HashMap<String, String> valuesmap = new HashMap<String, String>();

        valuesmap.put(SubmDataDB.SUBM_ID, wrap(subm.getSubmissionId()));
        valuesmap.put(SubmDataDB.EXP_ID, wrap(subm.getExperimentId()));
        valuesmap.put(SubmDataDB.STATUS, wrap(subm.getStatus()));
        valuesmap.put(SubmDataDB.SUBMITTER, wrap(subm.getSubmitter()));
        valuesmap.put(SubmDataDB.USER_NOTES, wrap(subm.getUserNotes()));
        valuesmap.put(SubmDataDB.SUBMIT_TIME, sysdate);
        valuesmap.put(SubmDataDB.ENSEMBL_REL_VER, wrap(subm.getEnsemblRelVer()));
        valuesmap.put(SubmDataDB.OPTIONS, wrap(subm.getOptions()));
        valuesmap.put(SubmDataDB.METADATA, wrap(subm.getMetadata()));
        valuesmap.put(SubmDataDB.FILE_TIME, "0");
        valuesmap.put(SubmDataDB.NOTIFIED, "0");
        valuesmap.put(SubmDataDB.COPIED, "0");

        return genericInsertStatement(TABNAME, valuesmap);
    }

    public static String checkExistsStatement(SubmDataDB subm) {
        return genericExistsStatement(TABNAME, keyStatement(subm));
    }

    public static String deleteSubmStatement(SubmDataDB subm) {
        return genericDeleteStatement(TABNAME, keyStatement(subm));
    }

    public static String updateStatement(SubmDataDB subm) {
        return genericUpdateStatement(TABNAME,
                SubmDataDB.STATUS + " = " + wrap(subm.getStatus())  + "," +
                SubmDataDB.STATUS_TEXT + " = " + wrap(subm.getStatusText())  + "," +
                SubmDataDB.SUBMITTER + " = " + wrap(subm.getSubmitter())  + "," +
                SubmDataDB.USER_NOTES + " = " + wrap(subm.getUserNotes())  + "," +
                SubmDataDB.MASTER_NAME + " = " + wrap(subm.getMasterServerName())  + "," +
                SubmDataDB.ENSEMBL_REL_VER + " = " + wrap(subm.getEnsemblRelVer()),
                keyStatement(subm));
    }

    public static String updateNotifiedStatement(SubmDataDB subm) {
        return genericUpdateStatement(TABNAME, SubmDataDB.NOTIFIED + " = (" +
                SubmDataDB.NOTIFIED + " + 1)", keyStatement(subm));
    }

    public static String updateCopiedStatement(SubmDataDB subm) {
        return genericUpdateStatement(TABNAME, SubmDataDB.COPIED + " = (" +
                SubmDataDB.COPIED + " + 1)", keyStatement(subm));
    }

    public static String updateFileTimeStatement(SubmDataDB subm) {
        return genericUpdateStatement(TABNAME, SubmDataDB.FILE_TIME + " = " + subm.getFileTime(),
                keyStatement(subm));
    }

    public static String updateStatusStatement(SubmDataDB subm) {
        return genericUpdateStatement(TABNAME,
                SubmDataDB.STATUS + " = " + wrap(subm.getStatus()) + "," +
                SubmDataDB.STATUS_TEXT + " = " + wrap(subm.getStatusText()),
                keyStatement(subm));
    }

    public static String updateStartTimeStatement(SubmDataDB subm, String sysdate) {
        return genericUpdateStatement(TABNAME, SubmDataDB.START_TIME + " = " + sysdate,
                keyStatement(subm));
    }

    public static String updateFinishTimeStatement(SubmDataDB subm, String sysdate) {
        return genericUpdateStatement(TABNAME, SubmDataDB.FINISH_TIME + " = " + sysdate,
                keyStatement(subm));
    }

    public static String updateMasterStatement(SubmDataDB subm) {
        return genericUpdateStatement(TABNAME, SubmDataDB.MASTER_NAME + " = " + wrap(subm.getMasterServerName()),
                keyStatement(subm));
    }

    public static String updateRelverStatement(SubmDataDB subm) {
        return genericUpdateStatement(TABNAME, SubmDataDB.ENSEMBL_REL_VER + " = " + wrap(subm.getEnsemblRelVer()),
                keyStatement(subm));
    }

    public static String updateCommentStatement(SubmDataDB subm) {
        return genericUpdateStatement(TABNAME, SubmDataDB.USER_NOTES + " = " + wrap(subm.getUserNotes()),
                keyStatement(subm));
    }

    public static String updateMetadataStatement(SubmDataDB subm) {
        return genericUpdateStatement(TABNAME, SubmDataDB.METADATA + " = " + wrap(subm.getMetadata()),
                keyStatement(subm));
    }

    public static String changeStatusStatement(String fromStatus, String toStatus, String statusText) {
        return genericUpdateStatement(TABNAME,
                SubmDataDB.STATUS + " = " + wrap(toStatus) + "," +
                SubmDataDB.STATUS_TEXT + " = " + wrap(statusText),
                SubmDataDB.STATUS + " = " + wrap(fromStatus));
    }

}


