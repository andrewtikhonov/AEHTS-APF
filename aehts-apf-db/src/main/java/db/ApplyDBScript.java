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
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 11/04/2012
 * Time: 12:05
 * To change this template use File | Settings | File Templates.
 */
public class ApplyDBScript {
    
    final private static Logger log = LoggerFactory.getLogger(ApplyDBScript.class);

    public static void main(String[] args) throws Exception {

        DAL dal = DALDefaults.getDAL();

        InputStream input = null;

        if (args.length == 1) {
            input = new FileInputStream(new File(args[0]));
            dal.applyDBScript(input);
            input.close();
        }
        else {
            input = ApplyDBScript.class.getResourceAsStream(System.getProperty("script"));
            dal.applyDBScript(input);
        }
    }
}
