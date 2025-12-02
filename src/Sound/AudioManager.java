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
        byte[] backgroundMusicOriginal = fileHandler.readAudio("soundtrack2.wav");
        backgroundMusic = new byte[backgroundMusicOriginal.length/2];
        for (int i = 0; i < backgroundMusic.length; i+=2) {
            byte byte1 = backgroundMusicOriginal[i*2];
            byte byte2 = backgroundMusicOriginal[i*2+1];
            byte byte3 = backgroundMusicOriginal[i*2+2];
            byte byte4 = backgroundMusicOriginal[i*2+3];
            int sample1 = ((byte1 << 8) & 0x0000FF00) | ((int)(byte2) & 0x000000FF);
            int sample2 = ((byte3 << 8) & 0x0000FF00) | ((int)(byte4) & 0x000000FF);
            int unifiedSample = (sample1+sample2)/2;
            byte newSample1 = (byte) ((unifiedSample >> 8) & 0x000000FF);
            byte newSample2 = (byte) (unifiedSample & 0x000000FF);
            backgroundMusic[i] = newSample1;
            backgroundMusic[i+1] = newSample2;
        }
    }

    public void play () {
        audioThread = new Thread(this::audioTask,"audioThread");
        audioThread.start();
    }

    private void audioTask () {
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
            sourceDataLine.drain();
            sourceDataLine.stop();
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
