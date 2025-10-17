package Sound;

import DataManagement.FileHandler;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public class AudioManager {
    private static AudioManager singleton;
    private final FileHandler fileHandler = FileHandler.get();
    private byte[] backgroundMusic;
    private Thread audioThread;
    private AudioManager () {
        backgroundMusic = fileHandler.readAudio("soundtrack.wav");
        audioThread = new Thread(this::audioTask,"audioThread");
        audioThread.start();
    }

    public void audioTask () {
        while (true) {
            playBackgroundMusic();
        }
    }

    private void playBackgroundMusic () {
        try {
            AudioFormat format = new AudioFormat(44100, 16, 1, true,
                    false);
            SourceDataLine sourceDataLine = AudioSystem.getSourceDataLine(format);
            sourceDataLine.open(format);
            sourceDataLine.start();
            sourceDataLine.write(backgroundMusic,0,backgroundMusic.length);
            sourceDataLine.flush();
            sourceDataLine.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static AudioManager get () {
        if (singleton == null)
            singleton = new AudioManager();

        return singleton;
    }
}
