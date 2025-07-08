import DataManagement.FileHandler;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Tester {
    private Application application;
    private int numTests;
    private int numTestsPassed;

    public Tester (Application application) {
        this.application = application;
    }

    public void run () {
//        numTestsPassed += testFileHandler();
        System.out.println("Passed " + numTestsPassed + " of " + numTests + " Tests.");
    }

    private int testFileHandler () {
        numTests++;

        // create file handler and read in test data
        FileHandler fileHandler = application.getFileHandler();
        byte[] audio = fileHandler.readAudio("audio.wav");
        BufferedImage image = fileHandler.readImage("image.png");

        // print output
        System.out.println(audio);
        System.out.println(image);

        // display image
        if (image == null)
            return 0;
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setSize(new Dimension(600,400));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JLabel imageLabel = new JLabel();
        imageLabel.setIcon(new ImageIcon(image));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        frame.add(imageLabel,BorderLayout.CENTER);
        frame.setVisible(true);

        // play audio
        try {
            AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
            SourceDataLine sourceDataLine = AudioSystem.getSourceDataLine(format);
            sourceDataLine.open(format);
            sourceDataLine.start();
            sourceDataLine.write(audio,0,audio.length);
        } catch (Exception e) {
            return 0;
        }

        // test read and write
        fileHandler.write("test-word","Hello, World!");
        String output = fileHandler.readString("test-word");
        if (output == null || output.isEmpty())
            return 0;

        return 1;
    }
}
