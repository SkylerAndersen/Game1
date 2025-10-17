package ApplicationManagement;

import DataManagement.MinHeap;

public class AsynchronousDispatch {
    private final Thread dispatchThread;
    private final MinHeap<Runnable> queuedTasks = new MinHeap<>();
    private final Object lock = new Object();
    private final long programEpoch = System.currentTimeMillis();
    private boolean threadStarted;
    public AsynchronousDispatch () {
        threadStarted = false;
        dispatchThread = new Thread(this::dispatchThreadUpdateLoop,"customDispatchThread");
        synchronized (lock) {
            dispatchThread.start();
            while (!threadStarted) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    private void dispatchThreadUpdateLoop () {
        synchronized (lock) {
            threadStarted = true;
            lock.notifyAll();
        }
        while (true) {
//            System.out.println("DispatchThread looped in: "+Thread.currentThread().getName());
            Runnable task;
            synchronized (lock) {
                while (queuedTasks.size() == 0) {
//                    System.out.println("Empty. Parking thread: " + Thread.currentThread().getName());
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                while (true) {
                    long nextWakeupTime = (queuedTasks.peek().getVal1() + programEpoch);
                    long currTime = System.currentTimeMillis();
                    long timeLeft = nextWakeupTime-currTime;
                    if (timeLeft <= 0)
                        break;
                    try {
                        lock.wait(timeLeft);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
//                    System.out.println("Non-Empty. Parking thread: " + Thread.currentThread().getName());
//                    System.out.println("Non-Empty. Parking thread until: " + nextWakeupTime);
                }
                task = queuedTasks.pop().getVal2();
            }
            task.run();
        }
    }

    public void schedule (int delay, Runnable task) {
        long time = System.currentTimeMillis();
        int timeSinceEpoch = (int)(time-programEpoch);
//        System.out.println("Time is: "+time);
//        System.out.println("Time since epoch is: "+timeSinceEpoch);
//        System.out.println("Schedule method schedules task for: "+(timeSinceEpoch+delay));
        synchronized (lock) {
            queuedTasks.push(timeSinceEpoch+delay,task);
            lock.notify();
        }
    }
}
