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
package processor.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by andrew on 04/03/2014.
 */
public class ProcessWatchDog implements Runnable {

    private final static Logger log = LoggerFactory.getLogger(ProcessWatchDog.class);

    public static final Integer RESET     = new Integer(1);
    public static final Integer SHUTDOWN  = new Integer(2);

    private long timout_in_ms             = 2000;
    private Thread watchDogThread         = null;
    private Runnable watchDogCallback     = null;
    private ArrayBlockingQueue<Integer> q = new ArrayBlockingQueue<Integer>(5);

    public ProcessWatchDog(long timout_in_ms, Runnable watchDogCallback) {
        this.timout_in_ms = timout_in_ms;
        this.watchDogCallback = watchDogCallback;
    }

    public void stop() {
        q.offer(SHUTDOWN);
        watchDogThread = null;
    }

    public void start() {
        if (watchDogThread == null) {
            watchDogThread = new Thread(this);
            watchDogThread.start();
        }
    }

    public void reset() {
        q.offer(RESET);
    }

    public void run() {
        int cnt = 10;
        while(cnt > 0) {
            try {
                Integer c = q.poll(timout_in_ms, TimeUnit.MILLISECONDS);

                if (c == null) {
                    // time's up
                    // invoke the callback
                    //
                    watchDogCallback.run();
                } else if (c == RESET) {
                    // do nothing
                    //
                } else if (c == SHUTDOWN) {
                    // stop
                    //
                    return;
                } else {

                    // in case something is wrong
                    // the thread will stop when cnt is 0
                    log.error("Unexpected c: " + c);

                    cnt--;
                }

            } catch (InterruptedException ie) {
                // allow stopping via .interrupt()
                //
                return;
            }
        }
    }

}
