package db.sql;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 13/03/2012
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
public class BaseSQL {
    public static String sqlString(String value, String defaultValue) {
        return (value == null || value.length() == 0) ? defaultValue : wrap(value);
    }

    public static String wrap(String value) {
        if (value != null) {
            value = value.replaceAll("'", "''");
            return ("'" + value + "'");
        } else {
            return ("''");
        }
    }

    public static String genericExistsStatement(String tabname, String condition) {
        return "select count(*) from " + tabname + " where " + condition;
    }

    public static String genericDeleteStatement(String tabname, String condition) {
        return "delete from " + tabname + " where " + condition;
    }

    public static String genericUpdateStatement(String tabname, String values, String condition) {
        return "update " + tabname + " set " + values + " where " + condition;
    }

    public static String genericInsertStatement(String tabname, HashMap<String, String> map) {
        boolean first = true;
        
        StringBuilder keys = new StringBuilder();
        StringBuilder vals = new StringBuilder();

        for(Map.Entry<String, String> entry : map.entrySet()) {

            if (first) {
                first = false;
            } else {
                keys.append(",");
                vals.append(",");
            }

            keys.append(entry.getKey());
            vals.append(entry.getValue());
        }
        
        return "insert into " + tabname + " (" + keys.toString() + ") values (" + vals.toString() + ")";
    }

    public static String makeANDArgs(HashMap<String, String> map) {
        return makeArgs("and", map);
    }

    public static String makeORArgs(String logic, HashMap<String, String> map) {
        return makeArgs("or", map);
    }

    public static String makeArgs(String logic, HashMap<String, String> map) {
        
        boolean first = true;
        
        Iterator<Map.Entry<String, String>> i = map.entrySet().iterator();
        StringBuilder str = new StringBuilder(); 
    
        while(i.hasNext()) {
            Map.Entry<String, String> entry = i.next();
    
            if (first) {
                first = false;
            } else {
                str.append(" ");
                str.append(logic);
                str.append(" ");
            }
    
            str.append(entry.getKey());
            str.append(" = ");
            str.append(wrap(entry.getValue()));
        }
        
        return str.toString();
    }

}

