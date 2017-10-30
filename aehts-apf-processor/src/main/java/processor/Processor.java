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
package processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processor.handler.*;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 13/03/2012
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class Processor {

    private final static Logger log = LoggerFactory.getLogger(Processor.class);

    public void runProcessor() throws Exception {

        //
        // CHECKED_SUITABLE submissions
        //
        new SubProcessor4Checked().processCheckedSubmissions();

        // RUNNING submissions
        //
        new SubProcessor4Computing().processComputingSubmissions();

        // computed submissions
        //
        new SubProcessor4Computed().processProcessedSubmissions();

        // delivering submissions
        //
        new SubProcessor4Delivering().processDeliveringSubmissions();


        // NO_CLUSTER submissions
        //
        new SubProcessor4NoResources().processNoResourcesSubmissions();

        /*

        // no data submissions
        //
        //new SubProcessor4NoData().processNoDataSubmissions();

        // failed submissions
        //
        //new SubProcessor4Failed().processFailedSubmissions();

        // deleted submissions
        //
        //processDeletedSubmissions();

        */

    }

    public static void main( String[] args ) throws Exception {


        try {
            new Processor().runProcessor();
        } catch (Exception ex) {
            log.error("Error!", ex);
        }

        log.info("returned from runProcessor");
	}

}
