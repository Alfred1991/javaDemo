package org.xiaofengcanyue.concurrency;

import com.google.common.util.concurrent.*;

/**
 * Service的生命周期：
 * Service.State.NEW -> Service.State.STARTING -> Service.State.RUNNING
 * -> Service.State.STOPPING -> Service.State.TERMINATED
 * 当Service在starting、running、stopping状态发生错误时会进入Service.State.FAILED状态。
 *
 * Service提供两种方式来等待service transitions to completes:
 *   1、异步方式addListener()
 *   2、同步方式awaitRunning()，awaitTerminated()
 */
public class AboutService {

    /**
     * 常用抽象service
     */
    public static void aboutAbstractService(){
        new AbstractIdleService(){

            @Override
            protected void startUp() throws Exception {

            }

            @Override
            protected void shutDown() throws Exception {

            }
        };

        new AbstractExecutionThreadService(){

            @Override
            protected void run() throws Exception {

            }
        };

        new AbstractScheduledService(){
            @Override
            protected void runOneIteration() throws Exception {

            }

            @Override
            protected Scheduler scheduler() {
                return null;
            }
        };

        new AbstractService(){
            @Override
            protected void doStart() {

            }

            @Override
            protected void doStop() {

            }
        };
    }

    /**
     * ServiceManager可同时管理多个service
     */
    public static void aboutServiceManager(){
        ServiceManager sm = new ServiceManager(null);
        sm.startAsync();
        sm.stopAsync();
        sm.addListener(null);
        sm.awaitHealthy();
        sm.awaitStopped();
        sm.isHealthy();
        sm.servicesByState();
        sm.startupTimes();
    }
}
