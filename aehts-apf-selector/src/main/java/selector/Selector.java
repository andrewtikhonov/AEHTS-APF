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
package selector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.rcloud.rpf.RemoteLogListener;
import uk.ac.ebi.rcloud.rpf.ServantProviderFactory;
import uk.ac.ebi.rcloud.server.RServices;
import uk.ac.ebi.rcloud.server.callback.RAction;
import uk.ac.ebi.rcloud.server.callback.RActionConst;
import uk.ac.ebi.rcloud.server.callback.RActionListener;
import uk.ac.ebi.rcloud.server.callback.RActionType;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 13/03/2012
 * Time: 15:08
 * To change this template use File | Settings | File Templates.
 */
public class Selector {

    private static final Logger log = LoggerFactory.getLogger(Selector.class);

    private ServantProviderFactory spFactory = null;

    private ServerRActionListener actionListener = null;
    private ServerLogListener logListener = null;

    public Selector(String scriptfilename) {

        try {
            actionListener = new ServerRActionListener();
        } catch (RemoteException re) {
            log.error("ServerRActionListener could not be created!", re);
        }

        try {
            logListener = new ServerLogListener();
        } catch (RemoteException re) {
            log.error("ServerLogListener could not be created!", re);
        }

        String poolname = System.getProperty("poolname");

        if (poolname == null || poolname.length() == 0) {
            poolname = "defaultpool";
        }

        String servername = System.getProperty("servername");

        Vector<String> script = new Vector<String>();

        if (scriptfilename != null) {
            try {
                BufferedReader input = new BufferedReader(new FileReader(new File(scriptfilename)));
                try {
                    String line = null;

                    while ((line = input.readLine()) != null) {
                        script.add(line);
                    }

                } finally {
                    try {
                        input.close();
                    } catch (IOException ioe) {
                    }
                }

            } catch (FileNotFoundException fnfe) {
                log.error("Error!", fnfe);
            } catch (IOException ioe) {
                log.error("Error!", ioe);
            }
        } else {
            //script.add("z0dfd");
            script.add("z0 <- rnorm(100)");
            //script.add("gc()");
            script.add("z0");
            script.add("rm(z0)");

            //script.add("z0 <- rnorm(1000000)");
            //script.add("gc()");
            //script.add("z1 <- rep(z0, 1000)");
            //script.add("gc()");
            //script.add("z2 <- rep(z1, 10)");
            //script.add("gc()");
            //script.add("rm(z0)");
            //script.add("rm(z1)");
            //script.add("rm(z2)");

            /*
            script.add("message('test script')");
            script.add("f1 <- function(){for(i in 1:10) {Sys.sleep(1); message('i=',i);}}");
            script.add("f1()");
            script.add("rm(f1)");
            script.add("message('finished')");
            */
        }

        log.info("getting " + servername + " from:" + poolname);

        RServices rs = getRServices(poolname, servername);

        if (rs != null) {
            try {
                log.info("executing script..");

                for(String cmd : script) {
                    rs.consoleSubmit(cmd);

                    try {
                        Thread.sleep(200);
                    } catch(InterruptedException ie){
                    }
                }

            } catch (RemoteException re) {
                log.error("Error!", re);
            } finally {
                freeRServices(rs);
            }
        } else {
            log.info("No R Servers Available");
        }

    }

    class ServerRActionListener extends UnicastRemoteObject implements RActionListener {
        final private Logger log = LoggerFactory.getLogger(getClass());

        public ServerRActionListener() throws RemoteException {
            super();
        }
        public void notify(RAction action) throws RemoteException {
            log.info("notify-action="+action.toString());

            if (action.getActionName().equals(RActionType.CONSOLE)) {
                log.info(action.getAttributes().get(RActionConst.OUTPUT).toString());
            }
        }
    }

    class ServerLogListener extends UnicastRemoteObject implements RemoteLogListener {
        final private Logger log = LoggerFactory.getLogger(getClass());

        public ServerLogListener() throws RemoteException {
            super();
        }

        public void write(String text) throws RemoteException {
            log.info("log-" + text);
        }
    }

    private RServices getRServices(String poolname, String servername) {
        RServices r  = null;

        spFactory = ServantProviderFactory.getFactory();

        try {
            if (servername == null) {
                r = (RServices) spFactory.getServantProvider().borrowServantProxyNoWait(poolname);
            } else {
                r = (RServices) spFactory.getServantProvider().getRegistry().lookup(servername);
            }

        } catch (Exception ex) {
            log.error("Error!", ex);
        }

        log.info("r=" + r);

        if (r != null) {
            try {
                if (actionListener != null) {
                    log.info("adding actionListener");
                    r.addRConsoleActionListener(actionListener);
                }

                if (logListener != null) {
                    log.info("adding logListener");
                    r.addLogListener(logListener);
                }

            } catch(Exception ex) {
                freeRServices(r);
            }
        }

        return r;
    }

    private void freeRServices(RServices r) {
        // free R services
        try {
            // unbind listeners
            if (logListener != null) {
                r.removeLogListener(logListener);
            }
            if (actionListener != null) {
                r.removeRConsoleActionListener(actionListener);
            }

            // return server to pool
            spFactory.getServantProvider().returnServantProxy(r);
        } catch (Exception e) {
            log.error("Error!", e);
        }
    }

    private static void setSystemProperty(String name, String value) {
        String val = System.getProperty(name);

        if (val == null || val.equals("")){
            System.setProperty(name, value);
        }
    }

	public static void main(String args[]) {
        String arg0 = null;

        if (args.length == 1) {
            arg0 = args[0];
        } else {
            log.info("usage: RunRJob <script.R>\n");

        }

        new Selector(arg0);

		System.exit(0);

	}


}
