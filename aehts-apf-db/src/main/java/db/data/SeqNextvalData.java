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
package db.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 30/03/2012
 * Time: 15:27
 * To change this template use File | Settings | File Templates.
 */
public class SeqNextvalData implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(SeqNextvalData.class);

    private static final long serialVersionUID = 1L;

    private HashMap<String, Object> map = new HashMap<String, Object>();

    public static String NEXTVAL   = "NEXTVAL";

    public SeqNextvalData( HashMap<String, Object> map ) {
        this.map = map;
    }

    public Integer getNextval() {
        Object obj = map.get(NEXTVAL);

        if (obj instanceof java.math.BigDecimal){
            return ((java.math.BigDecimal) obj).intValue();
        }
        return ((Integer) obj);
    }

	public String toString() {
		return "Nextval=" + getNextval().toString();
	}

}
