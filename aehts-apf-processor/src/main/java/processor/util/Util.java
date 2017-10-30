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
package processor.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.CollectionUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 04/04/2012
 * Time: 14:48
 * To change this template use File | Settings | File Templates.
 */
public class Util {

    private final static Logger log = LoggerFactory.getLogger(Util.class);
    
    public static String readFileIntoString(String scriptfilename) {
        
        Vector<String> buffer = readFileIntoVector(scriptfilename);
        StringBuilder builder = new StringBuilder();

        for (String line: buffer) {
            builder.append(line);
            builder.append("\n");
        }
        
        return builder.toString();
    }

    
    public static Vector<String> readFileIntoVector(String scriptfilename) {
        Vector<String> buffer = new Vector<String>();

        try {
            BufferedReader input = new BufferedReader(new FileReader(scriptfilename));
            try {
                String line = null;

                while ((line = input.readLine()) != null) {
                    buffer.add(line);
                }

            } finally {
                try {
                    input.close();
                } catch (IOException ioe) {
                }
            }

        } catch (FileNotFoundException fnfe) {
            log.error("Error!", fnfe);
        } catch (IOException ioe) {
            log.error("Error!", ioe);
        }

        return buffer;
    }
    
    public static HashMap<String, String> makeStringMap(Object... objs) {
        return CollectionUtil.makeMap(objs);
    }

    public static HashMap<String, Object> makeObjectMap(Object... objs) {
        return CollectionUtil.makeMap(objs);
    }

}
