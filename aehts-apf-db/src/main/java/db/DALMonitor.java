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

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: 23/03/2012
 * Time: 14:00
 * To change this template use File | Settings | File Templates.
 */
public class DALMonitor implements Runnable {

    private Thread thread = null;
    private Runnable target = null;
    private volatile long wakeup = 0;

    public void run() {
        long delay = Math.max(wakeup - System.currentTimeMillis(), 0);
        do {
            try {
                Thread.sleep(delay);
                target.run();
                thread = null;
                return;
            }
            catch (InterruptedException ie) {
            }

        } while(delay > 0);
    }

    public DALMonitor(Runnable target) {
        this.target = target;
    }

    public void setWatchdog(long delay) {
        this.wakeup = delay + System.currentTimeMillis();

        if (thread != null) {
            thread.interrupt();
        } else {
            thread = new Thread(this);
            thread.start();
        }
    }
}
