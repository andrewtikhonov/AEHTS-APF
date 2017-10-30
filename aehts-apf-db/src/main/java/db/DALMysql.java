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

import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.sql.Statement;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 26/03/2012
 * Time: 16:52
 * To change this template use File | Settings | File Templates.
 */
public class DALMysql extends DAL {
	public DALMysql (DALConnectionProvider provider) {
		super(provider);
	}

    protected void lockInternal(String tabname, Statement stmt) throws SQLException {
		stmt.execute("lock tables " + tabname + " write");
	}

	protected void unlockInternal(Statement stmt) throws SQLException {
		stmt.execute("unlock tables");
	}

	protected String sysdateFunctionName() {
		return "SYSDATE()";
	}

	@Override
	boolean isNoConnectionError(SQLException sqle) {
        return (sqle instanceof SQLRecoverableException);
	}

	@Override
	boolean isConstraintViolationError(SQLException sqle) {
		return true;
	}
}
