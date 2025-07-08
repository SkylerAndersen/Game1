import DataManagement.FileHandler;

public class Application {
    private static Application singleton;
    private FileHandler fileHandler;
    private WindowManager windowManager;

    private Application () {
        fileHandler = FileHandler.get();
        windowManager = WindowManager.get();
    }

    public void start () {
        windowManager.openWindow();

    }

    public static Application get () {
        if (singleton == null)
            singleton = new Application();
        return singleton;
    }

    public FileHandler getFileHandler () {
        return fileHandler;
    }
}
