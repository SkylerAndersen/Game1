public class Main {
    public static void main (String[] args) {
        Application application = Application.get();
        Tester tester = new Tester(application);
        tester.run();
//        application.start();
    }
}
