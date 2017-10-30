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
package db.exception;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 13/03/2012
 * Time: 16:37
 * To change this template use File | Settings | File Templates.
 */
public class AlreadyExistsException extends Exception {
    public AlreadyExistsException() {
   	}

   	public AlreadyExistsException(String s) {
   		super(s);
   	}

   	public AlreadyExistsException(String s, Throwable cause) {
   		super(s, cause);
   	}

}
