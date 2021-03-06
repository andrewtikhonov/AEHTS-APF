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
package http.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 29/03/2012
 * Time: 15:21
 * To change this template use File | Settings | File Templates.
 */
public class ServletUtil {
    private static Vector<String> orderP(String[] keys) {
        Arrays.sort(keys);
        Vector<String> result = new Vector<String>();
        for (int i = 0; i < keys.length; ++i)
            result.add((String) keys[i]);
        return result;
    }

    public static Vector<String> orderO(Collection<Object> c) {
        String[] keys = new String[c.size()];
        int i = 0;
        for (Object k : c)
            keys[i++] = (String) k;
        return orderP(keys);
    }

    public static Vector<String> orderS(Collection<String> c) {
        String[] keys = new String[c.size()];
        int i = 0;
        for (String k : c)
            keys[i++] = k;
        return orderP(keys);
    }

}
