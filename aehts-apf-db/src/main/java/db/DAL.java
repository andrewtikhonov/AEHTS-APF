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

import db.data.*;
import db.exception.AlreadyExistsException;
import db.exception.GeneralException;
import db.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 13/03/2012
 * Time: 16:30
 * To change this template use File | Settings | File Templates.
 */
public abstract class DAL implements DALInterface {

    private static final Logger log = LoggerFactory.getLogger(DAL.class);

    // keeping open connections can very soon
    // empty the connection pool, hence need to
    // close them timely.
    //
    private long MAXINACTIVITY = 8 * 1000;


	private Connection _connection = null;

    private DALConnectionProvider _connectionProvider;

	//
    // A B S T R A C T
    //

    abstract void lockInternal(String tabname, Statement stmt) throws SQLException;

	abstract void unlockInternal(Statement stmt) throws SQLException;

	abstract String sysdateFunctionName();

	abstract boolean isNoConnectionError(SQLException sqle);

	abstract boolean isConstraintViolationError(SQLException sqle);

    
    public void lock(String tabname) throws GeneralException {
        Statement stmt = null;
        try {
            stmt = createSqlStatement();
            lockInternal(tabname, stmt);
        }
        catch (SQLException sqle) {
            if (isNoConnectionError(sqle) && canReconnect()) {
                lock(tabname);
            } else {
                throw new GeneralException("", sqle);
            }
        }
        finally {
            closeStatement(stmt);
        }
    }

    public void unlock() throws GeneralException {
        Statement stmt = null;
        try {
            stmt = createSqlStatement();
            unlockInternal(stmt);
        }
        catch (SQLException sqle) {
            if (isNoConnectionError(sqle) && canReconnect()) {
                unlock();
            } else {
                throw new GeneralException("", sqle);
            }
        } finally {
            closeStatement(stmt);
        }
    }
    
    //
    // M O N I T O R
    //

    private DALMonitor monitor = new DALMonitor(new MonitorDBLayerRunnable());

    class MonitorDBLayerRunnable implements Runnable {
        public void run(){
            monitorInactivity();
        }
    }

    public void monitorInactivity() {
        if (_connection != null) {
            try {
                _connection.close();
            }
            catch (Exception ex) {
                log.error("Error!", ex);
            }
            finally {
                _connection = null;
            }
        }
    }


    //
    // T R A C E
    //

    public boolean wantSqlStatements() {
        String trace = System.getProperty("want.sql.statements");
        return (trace != null && trace.equalsIgnoreCase("true"));

        //return true;
    }

    /*
    @Override
    public void finalize() throws Throwable {
        log.info("finalizing");
        super.finalize();
    }
    */

    private void setConnectionProvider(DALConnectionProvider connectionProvider) {
        this._connectionProvider = connectionProvider;
    }

    public DAL (DALConnectionProvider provider) {

        setConnectionProvider(provider);

        try {
            checkConnection();
        }
        catch (Exception ex) {
            log.error("Error!", ex);
        }

	}


    public static DAL getLayer(String dbtype, DALConnectionProvider connProvider) throws Exception {
        String className = "db.DAL" +
                ("" + dbtype.charAt(0)).toUpperCase() + dbtype.substring(1);

        DAL result = (DAL) Class.forName(className)
                .getConstructor(new Class[] { DALConnectionProvider.class })
                .newInstance(new Object[] { connProvider });

        return result;
    }

    public boolean canReconnect() {
        if (_connectionProvider == null) {
            return false;
        }

        log.info("trying to reconnect");

        Statement stmt = null;
        try {
            stmt = createSqlStatement();
            stmt.executeQuery("select STEP_ID from STEP");
            log.info("reconnection aborted, connection was up");
            return false;
        } catch (SQLException sqle) {
        } finally {
            try {
                closeStatement(stmt);
            }
            catch (Exception ex) {
            }
        }

        Connection connection = null;
        try {
            connection = _connectionProvider.newConnection();
            stmt = connection.createStatement();
            stmt.executeQuery("select STEP_ID from STEP");
        }
        catch (SQLException sqle) {
            log.info("reconnection failed");
            return false;
        }
        finally {
            try {
                closeStatement(stmt);
            }
            catch (Exception ex) {
            }
        }
        _connection = connection;

        log.info("reconnection succeeded");
        return true;
    }


	public void checkConnection() throws SQLException {
        monitor.setWatchdog(MAXINACTIVITY);

        if (_connection == null) {
            if (_connectionProvider == null) {
                log.error("connection provider is null");
            }
            else {
                try {
                    _connection=_connectionProvider.newConnection();
                    _connection.setAutoCommit(false);
                }
                catch (Exception e) {
                    log.error("Error!", e);
                }
            }
        }

        if (_connection == null) {
            log.error("_connection is null after checkConnection()");
        }
	}


    private Statement createSqlStatement() throws SQLException {
        checkConnection();
        return _connection.createStatement();
    }

    private void closeStatement(Statement stmt) throws GeneralException {
        if (stmt != null) {
            try {
                stmt.close();
            }
            catch (Exception e) {
                throw new GeneralException("", e);
            }
        }
    }

    private static String wrap(String value) {
        value = value.replaceAll("'", "''");
        return ("'" + value + "'");
    }


    public void executeStatement(String statement) throws GeneralException {
        if (wantSqlStatements()) log.info(statement);

        Statement stmt = null;
        try {
            stmt = createSqlStatement();
            stmt.execute(statement);
        }
        catch (SQLException sqle) {
            if (isNoConnectionError(sqle) && canReconnect()) {
                executeStatement(statement);
            }
            else {
                throw new GeneralException("", sqle);
            }
        }
        finally {
            try {
                if (stmt != null) {
                    _connection.commit();
                }
            }
            catch (Exception e) {
                throw new GeneralException("", e);
            }

            closeStatement(stmt);
        }
    }

    /*
    public void executeStatementLocked(String statement) throws GeneralException {

        if (wantSqlStatements()) log.info(statement);

        Statement stmt = null;
        try {
            stmt = createSqlStatement();
            lock(stmt);
            stmt.execute(statement);
        }
        catch (SQLException sqle) {
            if (isNoConnectionError(sqle) && canReconnect()) {
                executeStatementLocked(statement);
            } else {
                throw new GeneralException("", sqle);
            }
        }
        finally {
            try {
                if (stmt != null) {
                    unlock(stmt);
                    _connection.commit();
                }
            }
            catch (Exception e) {
                throw new GeneralException("", e);
            }
            closeStatement(stmt);
        }
    }
    */

    public void commit() throws SQLException {
        _connection.commit();
    }

    //
    // I N S E R T
    //

    public void insertRecordInternal(String checkRecordStatement,
                                     String insertRecordStatement,
                                     String removeRecordStatement) throws AlreadyExistsException, GeneralException {
        Statement stmt = null;
        ResultSet rset = null;
        try {
            stmt = createSqlStatement();
            //lock(stmt);

            if (wantSqlStatements()) log.info(checkRecordStatement);

            rset = stmt.executeQuery(checkRecordStatement);
            rset.next();
            if (rset.getInt(1) > 0) {
                if (removeRecordStatement != null) {
                    if (wantSqlStatements()) log.info(checkRecordStatement);

                    stmt.execute(removeRecordStatement);
                } else {
                    throw new AlreadyExistsException();
                }
            }
        }
        catch (AlreadyExistsException aee) {
            try {
                if (stmt != null) {
                    //unlock(stmt);
                    _connection.commit();
                }
            } catch (Exception e) {
                throw new GeneralException("", e);
            }
            throw aee;
        } catch (SQLException sqle) {
            if (isNoConnectionError(sqle) && canReconnect()) {
                insertRecordInternal(checkRecordStatement,
                        insertRecordStatement,
                        removeRecordStatement);
            } else {
                throw new GeneralException("", sqle);
            }
        } finally {
            closeStatement(stmt);
        }

        stmt = null;

        try {
            stmt = createSqlStatement();

            if (wantSqlStatements()) log.info(insertRecordStatement);

            stmt.execute(insertRecordStatement);

        }
        catch (SQLException sqle) {
            log.error("Error!", sqle);
            throw new GeneralException("", sqle);
        }
        finally {
            try {
                if (stmt != null) {
                    //unlock(stmt);
                    _connection.commit();
                }
            }
            catch (Exception e) {
                throw new GeneralException("", e);
            }
            closeStatement(stmt);
        }
    }

    public void insertRecord(String tableName, HashMap<String, Object> record)
            throws GeneralException, AlreadyExistsException {

        String recordFields = "";
        String recordValues = "";
        String separator = "";
        String comma = ",";

        for (String key : record.keySet()) {

            recordFields += (separator + key);
            recordValues += (separator + "?");

            separator = comma;
        }

        String statement = "INSERT INTO " + tableName + " ("+recordFields+") VALUES ("+recordValues+")";

        if (wantSqlStatements()) log.info(statement);

        PreparedStatement pstmt = null;

        try {
            checkConnection();
            pstmt = _connection.prepareStatement(statement);

            int index = 1;
            for (String key : record.keySet()) {

                Object obj = record.get(key);

                if (obj instanceof Integer) {
                    pstmt.setInt(index, (Integer) obj);
                } else if (obj instanceof Timestamp) {
                    pstmt.setTimestamp(index, (Timestamp) obj);
                } else if (obj instanceof String) {
                    pstmt.setString(index, (String) obj);
                } else {
                    pstmt.setString(index, (String) obj);
                }

                index++;
            }

            pstmt.executeUpdate();
            _connection.commit();
        } catch (SQLException sqle) {
            if (isNoConnectionError(sqle) && canReconnect()) {
                executeStatement(statement);
            }
            else {
                throw new GeneralException("", sqle);
            }
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                }
                catch (Exception e) {
                    throw new GeneralException("", e);
                }
            }
        }
    }

    //
    // G E T   T A B L E
    //

    public Vector<HashMap<String, Object>> getTableData(String tableName) throws GeneralException {
        return getTableData(tableName, null);
    }

    public Vector<HashMap<String, Object>> getTableData(String tableName, String condition) throws GeneralException {
        Vector<HashMap<String, Object>> result = new Vector<HashMap<String, Object>>();
        Statement stmt = null;
        ResultSet rset = null;
        try {
            stmt = createSqlStatement();

            String statement = "select * from " + tableName +
                    (condition == null || condition.equals("") ? "" : " where " + condition);

            if (wantSqlStatements()) log.info(statement);

            rset = stmt.executeQuery(statement);

            while (rset.next()) {
                HashMap<String, Object> hm = new HashMap<String, Object>();
                for (int i = 1; i <= rset.getMetaData().getColumnCount(); ++i) {

                    Object obj = rset.getObject(i);
                    hm.put(rset.getMetaData().getColumnName(i), obj);
                }
                result.add(hm);
            }
        } catch (SQLException sqle) {
            if (isNoConnectionError(sqle) && canReconnect()) {
                return getTableData(tableName, condition);
            } else {
                throw new GeneralException("", sqle);
            }
        } finally {
            closeStatement(stmt);
        }
        return result;
    }

    //
    // S U B M I S S I O N
    //
    //

    public SubmDataDB createSubmAutoname(SubmDataDB subm) throws GeneralException {

        Statement stmt = null;
        ResultSet rset = null;
        try {
            stmt = createSqlStatement();
            lock(SubmSQL.TABNAME);
            //lock(stmt);

            if (wantSqlStatements()) log.info(SeqSQL.selectSeqNextValue());

            rset = stmt.executeQuery(SeqSQL.selectSeqNextValue());
            rset.next();

            int seqvalue = rset.getInt(1);

            // autoname
            subm.getMap().put(SubmDataDB.SUBM_ID,
                    SubmDataDB.AUTOPREFIX + seqvalue);

            String insertRecordStatement = SubmSQL.
                    addSubmStatement(subm, sysdateFunctionName());

            if (wantSqlStatements()) log.info( insertRecordStatement );

            stmt.execute(insertRecordStatement);

        } catch (SQLException sqle) {
            if (isNoConnectionError(sqle) && canReconnect()) {
                createSubmAutoname(subm);

            } else {
                throw new GeneralException("", sqle);
            }
        } finally {
            try {
                if (stmt != null) {
                    //unlock(stmt);
                    _connection.commit();
                }
            }
            catch (Exception e) {
                throw new GeneralException("", e);
            }

            unlock();

            closeStatement(stmt);
        }

        return subm;
    }
    
    public void createSubm(SubmDataDB subm) throws AlreadyExistsException, GeneralException {
        insertRecordInternal(SubmSQL.checkExistsStatement(subm),
                SubmSQL.addSubmStatement(subm, sysdateFunctionName()),
                null);
    }

    public void updateSubm(SubmDataDB subm) throws GeneralException {
        executeStatement(SubmSQL.updateStatement(subm));
    }

    public void deleteSubm(SubmDataDB subm) throws GeneralException {
        executeStatement(SubmSQL.deleteSubmStatement(subm));
    }

    public Vector<SubmDataDB> getSubmData(String condition) throws GeneralException {
        Vector<HashMap<String, Object>> tabledata = getTableData(SubmSQL.TABNAME, condition);
        Vector<SubmDataDB> result = new Vector<SubmDataDB>();
        for (HashMap<String, Object> hm : tabledata) {
            result.add(new SubmDataDB( hm ));
        }
        return result;
    }

    public SubmDataDB getSubm(SubmDataDB subm) throws GeneralException {
        Vector<SubmDataDB> table = getSubmData(SubmSQL.keyStatement(subm));

        for (SubmDataDB item : table) {
            return item;
        }
        return null;
    }

    public void updateSubmNotified(SubmDataDB subm) throws GeneralException {
        executeStatement(SubmSQL.updateNotifiedStatement(subm));
    }

    public void updateSubmCopied(SubmDataDB subm) throws GeneralException {
        executeStatement(SubmSQL.updateCopiedStatement(subm));
    }

    public void updateSubmFileTime(SubmDataDB subm) throws GeneralException {
        executeStatement(SubmSQL.updateFileTimeStatement(subm));
    }

    public void updateSubmStatus(SubmDataDB subm) throws GeneralException {
        executeStatement(SubmSQL.updateStatusStatement(subm));
    }

    public void updateSubmStartTime(SubmDataDB subm) throws GeneralException {
        executeStatement(SubmSQL.updateStartTimeStatement(subm, sysdateFunctionName()));
    }

    public void updateSubmFinishTime(SubmDataDB subm) throws GeneralException {
        executeStatement(SubmSQL.updateFinishTimeStatement(subm, sysdateFunctionName()));
    }

    public void updateSubmMaster(SubmDataDB subm) throws GeneralException {
        executeStatement(SubmSQL.updateMasterStatement(subm));
    }

    public void updateSubmRelver(SubmDataDB subm) throws GeneralException {
        executeStatement(SubmSQL.updateRelverStatement(subm));
    }

    public void updateSubmComment(SubmDataDB subm) throws GeneralException {
        executeStatement(SubmSQL.updateCommentStatement(subm));
    }

    public void updateSubmMetadata(SubmDataDB subm) throws GeneralException {
        executeStatement(SubmSQL.updateMetadataStatement(subm));
    }

    public Vector<SubmDataDB> listSubms(HashMap<String, String> req) throws GeneralException {
        return getSubmData(BaseSQL.makeANDArgs(req));
    }

    public void deleteMultSubms(HashMap<String, String> req) throws GeneralException {
        executeStatement(BaseSQL.genericDeleteStatement(SubmSQL.TABNAME, BaseSQL.makeANDArgs(req)));
    }

    public void chnageStatusForManySubms(String fromStatus, String toStatus, String statusText) throws GeneralException {
        executeStatement(SubmSQL.changeStatusStatement(fromStatus, toStatus, statusText));
    }



    //
    // R U N S
    //
    //

    public void createRun(RunDataDB run) throws AlreadyExistsException, GeneralException {
        insertRecordInternal(RunSQL.checkExistsStatement(run),
                RunSQL.addRunStatement(run),
                null);
    }

    public void updateRunStatus(RunDataDB run) throws GeneralException {
        executeStatement(RunSQL.updateStatusStatement(run));
    }

    public void updateRunFileTime(RunDataDB run) throws GeneralException {
        executeStatement(RunSQL.updateFileTimeStatement(run));
    }

    public void deleteRun(RunDataDB run) throws GeneralException {
        executeStatement(RunSQL.deleteRunStatement(run));
    }

    public Vector<RunDataDB> getRunData(String condition) throws GeneralException {
        Vector<HashMap<String, Object>> tabledata = getTableData(RunSQL.TABNAME, condition);
        Vector<RunDataDB> result = new Vector<RunDataDB>();
        for (HashMap<String, Object> hm : tabledata) {
            result.add(new RunDataDB( hm ));
        }
        return result;
    }


    public RunDataDB getRun(RunDataDB run) throws GeneralException {
        Vector<RunDataDB> table = getRunData(RunSQL.keyStatement(run));

        for (RunDataDB item : table) {
            return item;
        }
        return null;
    }

    public Vector<RunDataDB> listRuns(HashMap<String, String> req) throws GeneralException {
        return getRunData(BaseSQL.makeANDArgs(req));
    }

    public void deleteMultRuns(HashMap<String, String> req) throws GeneralException {
        executeStatement(BaseSQL.genericDeleteStatement(RunSQL.TABNAME, BaseSQL.makeANDArgs(req)));
    }

    //
    // E X P   S T E P S
    //
    //

    public void createExpStep(ExpStepDataDB expStep) throws GeneralException {
        try {
            insertRecordInternal(ExpStepSQL.checkExistsStatement(expStep),
                    ExpStepSQL.addExpStepStatement(expStep),
                    null);
        } catch (AlreadyExistsException aee) {
            updateExpStepStatus(expStep);
        }
    }


    public void updateExpStepStatus(ExpStepDataDB expstep) throws GeneralException {
        executeStatement(ExpStepSQL.updateStatusStatement(expstep));
    }

    public void deleteExpStep(ExpStepDataDB expstep) throws GeneralException {
        executeStatement(ExpStepSQL.deleteExpStepStatement(expstep));
    }

    public Vector<ExpStepDataDB> getExpStepData(String condition) throws GeneralException {
        Vector<HashMap<String, Object>> tabledata = getTableData(ExpStepSQL.TABNAME, condition);
        Vector<ExpStepDataDB> result = new Vector<ExpStepDataDB>();
        for (HashMap<String, Object> hm : tabledata) {
            result.add(new ExpStepDataDB( hm ));
        }
        return result;
    }

    public ExpStepDataDB getExpStep(ExpStepDataDB expStep) throws GeneralException {
        Vector<ExpStepDataDB> table = getExpStepData(ExpStepSQL.keyStatement(expStep));

        for (ExpStepDataDB item : table) {
            return item;
        }
        return null;
    }

    public Vector<ExpStepDataDB> listExpSteps(HashMap<String, String> req) throws GeneralException {
        return getExpStepData(BaseSQL.makeANDArgs(req));
    }

    public void deleteMultExpSteps(HashMap<String, String> req) throws GeneralException {
        executeStatement(BaseSQL.genericDeleteStatement(ExpStepSQL.TABNAME, BaseSQL.makeANDArgs(req)));
    }

    //
    // R U N   S T E P S
    //
    //

    public void createRunStep(RunStepDataDB runstep) throws GeneralException {
        try {
            insertRecordInternal(RunStepSQL.checkExistsStatement(runstep),
                    RunStepSQL.addRunStepStatement(runstep),
                    null);
        } catch (AlreadyExistsException aee) {
            updateRunStepStatus(runstep);
        }
    }

    public void updateRunStepStatus(RunStepDataDB runstep) throws GeneralException {
        executeStatement(RunStepSQL.updateStatusStatement(runstep));
    }

    public void deleteRunStep(RunStepDataDB runstep) throws GeneralException {
        executeStatement(RunStepSQL.deleteRunStepStatement(runstep));
    }

    public Vector<RunStepDataDB> getRunStepData(String condition) throws GeneralException {
        Vector<HashMap<String, Object>> tabledata = getTableData(RunStepSQL.TABNAME, condition);
        Vector<RunStepDataDB> result = new Vector<RunStepDataDB>();
        for (HashMap<String, Object> hm : tabledata) {
            result.add(new RunStepDataDB( hm ));
        }
        return result;
    }

    public RunStepDataDB getRunStep(RunStepDataDB runstep) throws GeneralException {
        Vector<RunStepDataDB> table = getRunStepData(RunStepSQL.keyStatement(runstep));

        for (RunStepDataDB item : table) {
            return item;
        }
        return null;
    }

    public Vector<RunStepDataDB> listRunSteps(HashMap<String, String> req) throws GeneralException {
        return getRunStepData(BaseSQL.makeANDArgs(req));
    }

    public void deleteMultRunSteps(HashMap<String, String> req) throws GeneralException {
        executeStatement(BaseSQL.genericDeleteStatement(RunStepSQL.TABNAME, BaseSQL.makeANDArgs(req)));
    }

    //
    // S T E P S
    //
    //

    public void createStep(StepDataDB step) throws AlreadyExistsException, GeneralException {
        insertRecordInternal(StepSQL.checkExistsStatement(step),
                StepSQL.addStepStatement(step),
                null);
    }

    public void updateStepDescription(StepDataDB step) throws GeneralException {
        executeStatement(StepSQL.updateDescriptionStatement(step));
    }

    public void deleteStep(StepDataDB step) throws GeneralException {
        executeStatement(StepSQL.deleteStepStatement(step));
    }

    public Vector<StepDataDB> getStepData(String condition) throws GeneralException {
        Vector<HashMap<String, Object>> tabledata = getTableData(StepSQL.TABNAME, condition);
        Vector<StepDataDB> result = new Vector<StepDataDB>();
        for (HashMap<String, Object> hm : tabledata) {
            result.add(new StepDataDB( hm ));
        }
        return result;
    }

    public StepDataDB getStep(StepDataDB step) throws GeneralException {
        Vector<StepDataDB> table = getStepData(StepSQL.keyStatement(step));

        for (StepDataDB item : table) {
            return item;
        }
        return null;
    }

    public Vector<StepDataDB> listSteps(HashMap<String, String> req) throws GeneralException {
        return getStepData(BaseSQL.makeANDArgs(req));
    }

    //
    // S E T T I N G S
    //
    //

    // options
    public void createSetting(SettingDataDB option) throws AlreadyExistsException, GeneralException {
        insertRecordInternal(SettingsSQL.checkExistsStatement(option),
                SettingsSQL.addSettingStatement(option),
                null);
    }

    public void deleteSetting(SettingDataDB option) throws GeneralException {
        executeStatement(SettingsSQL.deleteSettingStatement(option));
    }

    public Vector<SettingDataDB> getSettingData(String condition) throws GeneralException {
        Vector<HashMap<String, Object>> tabledata = getTableData(SettingsSQL.TABNAME, condition);
        Vector<SettingDataDB> result = new Vector<SettingDataDB>();
        for (HashMap<String, Object> hm : tabledata) {
            result.add(new SettingDataDB( hm ));
        }
        return result;
    }

    public SettingDataDB getSetting(String optionname) throws GeneralException {
        Vector<SettingDataDB> table = getSettingData(SettingsSQL.keyStatement(optionname));

        for (SettingDataDB item : table) {
            return item;
        }
        return null;
    }

    public void updateSetting(SettingDataDB option) throws GeneralException {
        executeStatement(SettingsSQL.updateValueStatement(option));

    }

    //
    //   U T I L S
    //

	public static String replaceCode(String s) {
		int p1 = 0;
		while ((p1 = s.indexOf("<%=")) != -1) {
			int p2 = s.indexOf("%>", p1 + 3);
			String expression = s.substring(p1 + 3, p2);
			String className = expression.substring(0, expression.lastIndexOf('.'));
			String functionName = expression.substring(expression.lastIndexOf('.') + 1, expression.lastIndexOf("()"));
			String replaceWith = "ERROR";
			try {
				replaceWith = (String) Class.forName(className)
                        .getMethod(functionName, (Class[]) null)
                        .invoke(null, (Object[]) null);
			} catch (Exception e) {
                log.error("Error!", e);
			}
			s = s.substring(0, p1) + replaceWith + s.substring(p2 + 2);
		}
		return s;
	}

	public void applyDBScript(InputStream scriptInputStream) throws GeneralException {
		Statement stmt = null;
		ResultSet rset = null;
		try {
			stmt = createSqlStatement();

			BufferedReader br = new BufferedReader(new InputStreamReader(scriptInputStream));
			String line = null;
			StringBuffer sbuffer = new StringBuffer();
			try {
				while ((line = br.readLine()) != null) {
					sbuffer.append(line.trim());
					sbuffer.append(" ");
				}
			} catch (Exception e) {
                log.error("Error!", e);
			}

			StringTokenizer st = new StringTokenizer(replaceCode(sbuffer.toString()), ";");
			while (st.hasMoreElements()) {
				String statmentStr = ((String) st.nextElement()).trim();
				if (statmentStr.equals(""))
					continue;

                log.info("<" + statmentStr + ">");

				try {
					if (statmentStr.trim().equalsIgnoreCase("commit")) {
						_connection.commit();
					} else {
						stmt.execute(statmentStr);
					}
                    log.info("OK");
				} catch (SQLException sqle) {
					if (statmentStr.toUpperCase().startsWith("DROP")) {
                        log.info("NOK / " + statmentStr + " Failed ");
					} else {
                        log.error("Error!", sqle);
					}
				}

			}
		} catch (SQLException sqle) {

			if (isNoConnectionError(sqle) && canReconnect()) {
				applyDBScript(scriptInputStream);
			} else {
				throw new GeneralException("", sqle);
			}

		} finally {
            closeStatement(stmt);
		}
	}

    //
    // DATE & TIME
    //
    public TimeDataDB getTimestamp() throws GeneralException {
        Vector<HashMap<String, Object>> sysdate =
                getTableData("(select sysdate \"TIMESTAMP\" from dual)", null);

        if (sysdate.size() > 0) {
            return new TimeDataDB( sysdate.elementAt(0) );
        } else {
            return null;
        }
    }

}
