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
import processor.handler.SubProcessor4Checking;
import processor.handler.SubProcessor4Submitted;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 28/10/2013
 * Time: 13:12
 * To change this template use File | Settings | File Templates.
 */
public class Checker {

    private final static Logger log = LoggerFactory.getLogger(Checker.class);

    public void runChecker() throws Exception {

        //
        // check stuck in CHECKING
        //
        new SubProcessor4Checking().processCheckingSubmissions();

        //
        // check NEW submissions
        //
        new SubProcessor4Submitted().processNewSubmissions();

    }

    public static void main( String[] args ) throws Exception {


        try {
            new Checker().runChecker();
        } catch (Exception ex) {
            log.error("Error!", ex);
        }

        log.info("returned from runChecker");
	}

}
