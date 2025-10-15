import DataManagement.MinHeap;

import java.util.concurrent.locks.LockSupport;

public class AsynchronousDispatch {
    private Thread dispatchThread;
    private MinHeap<Runnable> queuedTasks;
    private boolean threadRunning, initialWakeup;
    private long programEpoch;
    public AsynchronousDispatch () {
        programEpoch = System.currentTimeMillis();
        queuedTasks = new MinHeap<>();
        threadRunning = true;
        initialWakeup = false;

        dispatchThread = new Thread() {
            @Override
            public void run () {
                while (threadRunning) {
                    System.out.println("DispatchThread looped in: "+Thread.currentThread().getName());
                    executeTasks();
                    if (queuedTasks.size() == 0) {
                        System.out.println("Empty. Parking thread: "+Thread.currentThread().getName());
                        LockSupport.park();
                    } else {
                        long nextWakeupTime = (queuedTasks.peek().getVal1()+programEpoch);
                        System.out.println("Non-Empty. Parking thread: "+Thread.currentThread().getName());
                        System.out.println("Non-Empty. Parking thread until: "+nextWakeupTime);
                        LockSupport.parkUntil(nextWakeupTime);
                    }
                }
            }
        };
    }

    private synchronized void executeTasks () {
        System.out.println("Execute Tasks called from: "+Thread.currentThread().getName());
        while (queuedTasks.size() != 0 && (queuedTasks.peek().getVal1())+programEpoch <=
                System.currentTimeMillis()) {
            Runnable task = queuedTasks.pop().getVal2();
            task.run();
        }
    }

    public synchronized void schedule (int delay, Runnable task) {
        long time = System.currentTimeMillis();
        int timeSinceEpoch = (int)(time-programEpoch);
        System.out.println("Time is: "+time);
        System.out.println("Time since epoch is: "+timeSinceEpoch);
        System.out.println("Schedule method schedules task for: "+(timeSinceEpoch+delay));
        queuedTasks.push(timeSinceEpoch+delay,task);
        LockSupport.unpark(dispatchThread);
    }

    public synchronized void ensureShutdown () {
        if (!threadRunning && initialWakeup)
            return;

        threadRunning = false;
        LockSupport.unpark(dispatchThread);
        try {
            dispatchThread.join();
        } catch (Exception e) {
            throw new RuntimeException("Failed to ensure the shutdown.");
        }
    }

    public synchronized void ensureCreation () {
        if (threadRunning && initialWakeup)
            return;

        dispatchThread.start();
        while (!dispatchThread.isAlive()) {}
        initialWakeup = true;
    }

}
