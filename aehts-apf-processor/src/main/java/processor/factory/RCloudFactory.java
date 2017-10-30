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
package processor.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.rcloud.rpf.RemoteLogListener;
import uk.ac.ebi.rcloud.rpf.ServantProviderFactory;
import uk.ac.ebi.rcloud.server.RServices;
import uk.ac.ebi.rcloud.server.callback.RAction;
import uk.ac.ebi.rcloud.server.callback.RActionConst;
import uk.ac.ebi.rcloud.server.callback.RActionListener;
import uk.ac.ebi.rcloud.server.callback.RActionType;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 04/04/2012
 * Time: 14:41
 * To change this template use File | Settings | File Templates.
 */
public class RCloudFactory {

    private final static Logger log = LoggerFactory.getLogger(RCloudFactory.class);

    private boolean listenEvents = true;

    private ServantProviderFactory spFactory = null;
    private ServerRActionListener actionListener = null;
    private ServerLogListener logListener = null;
    private RServices rServices = null;

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

    public RCloudFactory(boolean listenEvents){
        this.listenEvents = listenEvents;
    }

    public RCloudFactory(){
        this(false);
    }

    public RServices locateRServices(String servername) {
        if (rServices != null) {
            return rServices;
        } else {
            spFactory = ServantProviderFactory.getFactory();

            try {
                rServices = (RServices) spFactory.getServantProvider().getRegistry().lookup(servername);

                log.info("rServices = " + rServices);

                if (rServices != null) {
                    registerListeners();
                }

                return rServices;

            } catch (NotBoundException nbe) {
                log.info("NotBoundException, {} not found!", servername);
            } catch (Exception ex) {
                log.error("Error!", ex);
            }

            return null;
        }
    }

    public RServices getRServices(String poolname) {

        log.info("Allocating an R Server from " + poolname);

        if (rServices != null) {
            return rServices;
        } else {
            spFactory = ServantProviderFactory.getFactory();

            try {
                rServices = (RServices) spFactory.getServantProvider().borrowServantProxyNoWait(poolname);
                /*
                if (servername == null) {
                } else {
                    r = (RServices) spFactory.getServantProvider().getRegistry().lookup(servername);
                }
                */

            } catch (Exception ex) {
                log.error("Error!", ex);
            }

            log.info("rServices = " + rServices);

            if (rServices != null) {
                registerListeners();
            }

            return rServices;
        }
    }

    public void registerListeners() {
        if (listenEvents) {
            try {
                actionListener = new ServerRActionListener();

                log.info("adding actionListener");

                rServices.addRConsoleActionListener(actionListener);

            } catch (RemoteException re) {
                log.error("Failed to create ServerRActionListener!", re);
                log.info("Events from R will not be collected");

                // cleanup
                try {
                    UnicastRemoteObject.unexportObject(actionListener, true);
                } catch (Exception ex) {
                }

                actionListener = null;
            }

            try {
                logListener = new ServerLogListener();

                log.info("adding logListener");

                rServices.addLogListener(logListener);

            } catch (RemoteException re) {
                log.error("Failed to create ServerLogListener!", re);
                log.info("Logs from R will not be collected");

                // cleanup
                try {
                    UnicastRemoteObject.unexportObject(logListener, true);
                } catch (Exception ex) {
                }

                logListener = null;
            }
        }
    }

    public void disconnectListeners() throws RemoteException {
        // unbind listeners
        if (logListener != null) {
            rServices.removeLogListener(logListener);
            log.info("removed logListener");

            // cleanup
            try {
                UnicastRemoteObject.unexportObject(actionListener, true);
                log.info("unexported logListener");
            } catch (Exception ex) {
            }
        }

        if (actionListener != null) {
            rServices.removeRConsoleActionListener(actionListener);
            log.info("removed actionListener");

            // cleanup
            try {
                UnicastRemoteObject.unexportObject(logListener, true);
                log.info("unexported actionListener");
            } catch (Exception ex) {
            }
        }
    }

    public void freeRServices() {
        // free R services
        try {
            // disconnect
            disconnectListeners();

            // return server to pool
            spFactory.getServantProvider().returnServantProxy(rServices);

            rServices = null;
        } catch (Exception e) {
            log.error("Error!", e);
        }
    }

    public void disconnectFromRServices() {
        // free R services
        try {
            // disconnect
            disconnectListeners();

            rServices = null;
        } catch (Exception e) {
            log.error("Error!", e);
        }
    }

}
