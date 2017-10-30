package db.sql;

import db.data.ExpStepDataDB;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 30/03/2012
 * Time: 15:37
 * To change this template use File | Settings | File Templates.
 */
public class SeqSQL extends BaseSQL {

    public static final String TABNAME = "dual";

    public static String selectSeqNextValue() {
        return "select submission_seq.nextval from " + TABNAME;
    }

}
