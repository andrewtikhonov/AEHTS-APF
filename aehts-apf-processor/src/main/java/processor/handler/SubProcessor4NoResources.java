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
 * Created by andrew on 07/11/14.
 */
public class SubProcessor4NoResources {

    private final static Logger log = LoggerFactory.getLogger(SubProcessor4Delivering.class);

    public void processNoResourcesSubmissions() throws Exception {

        log.info("Processing 'Cluster Not Allocated' Submissions");

        // get register DAL
        //
        DAL dal = DALDefaults.getDAL();

        // reset all CLUSTER_NOT_ALLOCATED to PROCESSING_REQUESTED
        //
        dal.chnageStatusForManySubms(DataConst.CLUSTER_NOT_ALLOCATED,
                DataConst.PROCESSING_REQUESTED, "Recomputing those failed at resource allocation");
    }

}


