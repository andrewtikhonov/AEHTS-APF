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

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 24/04/2012
 * Time: 14:17
 * To change this template use File | Settings | File Templates.
 */
public class DataConst {


    // processing states
    //
    public static final String CHECKING              = "CHECKING";
    public static final String COMPUTING             = "COMPUTING";
    public static final String DELIVERING            = "DELIVERING";

    // result states
    //
    public static final String CHECKED_SUITABLE      = "CHECKED_SUITABLE";
    public static final String CHECKED_NOT_SUITABLE  = "CHECKED_NOT_SUITABLE";
    public static final String CHECKED_NO_REFERENCE  = "CHECKED_NO_REFERENCE";

    public static final String COMPUTED              = "COMPUTED";
    public static final String COMPUTING_FAILED      = "COMPUTING_FAILED";

    public static final String DELIVERED             = "DELIVERED";
    public static final String DELIVERY_FAILED       = "DELIVERY_FAILED";
    public static final String DATA_NOT_AVAILABLE    = "DATA_NOT_AVAILABLE";

    //
    public static final String CLUSTER_NOT_ALLOCATED = "ALLOCATING_CLUSTER_NOT_COMPLETED";

    // additional states
    //
    public static final String UNDEFINED             = "UNDEFINED";
    public static final String WARNING               = "WARNING";
    public static final String OK                    = "OK";

    // management states
    //
    public static final String PROCESSING_REQUESTED  = "PROCESSING_REQUESTED";
    public static final String DELETION_REQUESTED    = "DELETION_REQUESTED";

    public static final String EXP_STATUS            = "EXP_STATUS";
    public static final String RUN_STATUS            = "RUN_STATUS";

    public static final String EXP_STATUS_END        = "_NOT_COMPLETED";

}
