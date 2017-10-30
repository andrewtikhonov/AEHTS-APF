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

import uk.ac.ebi.rcloud.server.RType.RChar;
import uk.ac.ebi.rcloud.server.RType.RList;
import uk.ac.ebi.rcloud.server.RType.RObject;

/**
 * Created by andrew on 07/11/14.
 */
public class RUtil {

    public static boolean inherits(RObject obj, String attr) {
        if (obj == null) {
            return false;
        }

        RList att = obj.getAttributes();
        if (att == null) {
            return false;
        }

        String[] names = att.getNames();
        for (int i=0;i<names.length;i++) {
            if ("class".equals(names[i])) {
                RObject cl = att.getValue()[i];
                if (cl != null && cl instanceof RChar) {
                    RChar clCh = (RChar) cl;
                    for (String s : clCh.getValue()) {
                        if (attr.equals(s)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public static String getString(RObject obj) {
        if (obj != null && obj instanceof RChar) {
            String[] val = ((RChar)obj).getValue();
            if (val.length > 0) {
                return val[0];
            }
        }
        return null;
    }

}
