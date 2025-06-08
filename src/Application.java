import DataManagement.FileHandler;

public class Application {
    private static Application singleton;
    private FileHandler fileHandler;

    private Application () {
        fileHandler = FileHandler.get();
    }

    public void start () {

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
