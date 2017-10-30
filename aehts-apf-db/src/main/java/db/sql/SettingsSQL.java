package db.sql;

import db.data.SettingDataDB;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 04/04/2012
 * Time: 17:01
 * To change this template use File | Settings | File Templates.
 */
public class SettingsSQL extends BaseSQL {

    public static final String TABNAME = "config";

    public static String keyStatement(String option_name) {
        return SettingDataDB.OPTION_NAME + " = " + wrap(option_name);
    }

    public static String keyStatement(SettingDataDB setting) {
        return keyStatement(setting.getOptionName());
    }

    public static String addSettingStatement(SettingDataDB setting) {
        HashMap<String, String> valuesmap = new HashMap<String, String>();

        valuesmap.put(SettingDataDB.OPTION_NAME, wrap(setting.getOptionName()));
        valuesmap.put(SettingDataDB.OPTION_VALUE, wrap(setting.getOptionValue()));

        return genericInsertStatement(TABNAME, valuesmap);
    }

    public static String checkExistsStatement(SettingDataDB setting) {
        return genericExistsStatement(TABNAME,
                keyStatement(setting));
    }

    public static String deleteSettingStatement(SettingDataDB setting) {
        return genericDeleteStatement(TABNAME,
                keyStatement(setting));
    }

    public static String updateValueStatement(SettingDataDB setting) {
        return genericUpdateStatement(TABNAME, SettingDataDB.OPTION_VALUE + " = " + wrap(setting.getOptionValue()),
                keyStatement(setting));
    }

}
