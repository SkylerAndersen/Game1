import DataManagement.MinHeap;

public class AsynchronousDispatch {
    private Thread dispatchThread;
    private int minWaitTime;
    private MinHeap<Integer> queuedTasks;
    public AsynchronousDispatch () {
        dispatchThread = new Thread() {
            @Override
            public void run () {

            }
        };
        dispatchThread.start();
    }
}
