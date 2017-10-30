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

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 30/03/2012
 * Time: 15:15
 * To change this template use File | Settings | File Templates.
 */
public class StringUtil {

    public static String joinOn(String separator, List<String> list) {
        boolean first = true;
        StringBuilder str = new StringBuilder();
        for(String s: list) {
            if (first) {
                first = false;
            } else {
                str.append(separator);
            }
            str.append(s);
        }
        return str.toString();
    }

    public static String replaceAll(String input, String replaceWhat, String replaceWith) {
   		int p;
   		int bindex = 0;
   		while ((p = input.indexOf(replaceWhat, bindex)) != -1) {
   			input = input.substring(0, p) + replaceWith + input.substring(p + replaceWhat.length());
            bindex = p + replaceWith.length();
   		}
   		return input;
   	}

}
