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
package processor.data;

import db.DataConst;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 24/04/2012
 * Time: 14:35
 * To change this template use File | Settings | File Templates.
 */
public class StatusInfo {

    private static final HashMap<String, StatusRecord> priorityMap = initPriorityMap();
    private static final StatusRecord not_completed_status = new StatusRecord(1, true);
    private static final StatusRecord unknown_status       = new StatusRecord(0, true);

    private static HashMap<String, StatusRecord> initPriorityMap(){
        HashMap<String, StatusRecord> m0 = new HashMap<String, StatusRecord>();

        // high priority                                      priority
        //                                                    |   showstopper
        //                                                    |   |
        m0.put(DataConst.COMPUTING_FAILED,   new StatusRecord(2, true));    // not really used
        m0.put(DataConst.DATA_NOT_AVAILABLE, new StatusRecord(2, true));

        // medium-high priority
        //
        m0.put(DataConst.WARNING,            new StatusRecord(3, false));

        // medium-normal priority
        //
        m0.put(DataConst.OK,                 new StatusRecord(4, true));
        m0.put(DataConst.COMPUTED,           new StatusRecord(4, true));

        // low priority
        //
        m0.put(DataConst.COMPUTING,          new StatusRecord(10, true));
        m0.put(DataConst.UNDEFINED,          new StatusRecord(10, true));
        m0.put(DataConst.PROCESSING_REQUESTED, new StatusRecord(10, true));
        m0.put(DataConst.DELETION_REQUESTED,   new StatusRecord(10, true));

        return m0;
    }

    public static StatusRecord getInfo(String status) {

        if (priorityMap.containsKey(status)) {
            return priorityMap.get(status);
        }

        if (status.endsWith(DataConst.EXP_STATUS_END)) { // this one is used primarily
            // experiment status
            //
            return not_completed_status;
        }

        // unknown status,
        // show it first to bring attention
        return unknown_status;
    }

    public static Integer getPriority(String status) {
        return getInfo(status).priority;
    }

    public static boolean isShowstopper(String status) {
        return getInfo(status).showstopper;
    }
}
