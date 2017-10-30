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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 30/03/2012
 * Time: 15:19
 * To change this template use File | Settings | File Templates.
 */
public class ListUtil {

    @SuppressWarnings("unchecked")
    public static <K> List<K> makeList(Object... objs) {
        List<K> list = new ArrayList<K> ();
        for (int i = 0; i < (objs.length); i++) {
            list.add((K) objs[i]);
        }
        return list;
    }
}
