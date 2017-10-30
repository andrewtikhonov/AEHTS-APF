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
package util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 04/04/2012
 * Time: 14:23
 * To change this template use File | Settings | File Templates.
 */
public class CollectionUtil {

    @SuppressWarnings("unchecked")
    public static <K, V> HashMap<K, V> makeMap(Object... objs) {
        HashMap<K, V> map = new HashMap<K, V>();
        for (int i = 0; i < (objs.length & -2); i += 2) {
            map.put((K) objs[i], (V) objs[i + 1]);
        }
        return map;
    }

}
