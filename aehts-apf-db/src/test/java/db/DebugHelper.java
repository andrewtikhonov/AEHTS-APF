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
package db;

import org.slf4j.Logger;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 28/03/2012
 * Time: 16:51
 * To change this template use File | Settings | File Templates.
 */
public class DebugHelper {
    private ArrayList<String> debuglist = new ArrayList<String>(); 
    private Logger log;
    private boolean enableLoggin = true;

    public DebugHelper(Logger log) {
        this.log = log;
    }
    
    public void debugEnter(String val) {
        if (enableLoggin) {
            debuglist.add(val);
            log.info(debugAssemble("enter"));
        }
    }
    
    public void debugExit() {
        if (enableLoggin) {
            log.info(debugAssemble("exit"));
            if (debuglist.size() > 0) {
                debuglist.remove(debuglist.size() - 1);
            }
        }
    }
    
    public void debugLog(String str) {
        if (enableLoggin) {
            log.info(debugAssemble(str));
        }
    }

    private String debugAssemble(String str) {
        StringBuilder b = new StringBuilder();

        b.append("DebugHelper::: ");

        for(String item: debuglist) {
            b.append(item);
            b.append("-");
        }

        b.append(str);
        return b.toString();
    }
    
}
