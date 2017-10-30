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
package processor.handler;

import db.DAL;
import db.DALDefaults;
import db.DataConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 30/10/2013
 * Time: 14:28
 * To change this template use File | Settings | File Templates.
 */
public class SubProcessor4Checking {

    private final static Logger log = LoggerFactory.getLogger(SubProcessor4Checking.class);

    public void processCheckingSubmissions() throws Exception {

        log.info("Processing Checking Submissions");

        // get register DAL
        //
        DAL dal = DALDefaults.getDAL();

        // reset all CHECKING to PROCESSING_REQUESTED
        //
        dal.chnageStatusForManySubms(DataConst.CHECKING,
                DataConst.PROCESSING_REQUESTED,
                "");
    }

}
