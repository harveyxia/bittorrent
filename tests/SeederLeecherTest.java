package tests;

import metafile.MetaFile;
import org.junit.Test;

import java.io.*;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

import static org.junit.Assert.*;

/**
 * Created by marvin on 5/8/16.
 */
public class SeederLeecherTest {

    private static int TIMEOUT = 30;
    private static String TORRENT = "tests/data/song.mp3.torrent";
    private static String DATA = "tests/data/song.mp3";
    private static String DOWNLOAD = "tests/download";

    @Test
    public void testSingleSeederSingleLeecher() throws Exception {

        Process tracker = createTracker(1999);
        Thread.sleep(1000);
        Process seeder = createSeeder("seeder", 2000, 1999);
        Thread.sleep(1000);
        Process leecher = createLeecher("leecher", 2001);

        Timer timer = startTimeout(seeder, leecher, tracker);
        Thread t = matchLine(leecher, "datafile is complete");
        joinAll(t);
        timer.cancel();
        killAll(seeder, leecher, tracker);
    }

    @Test
    public void testSingleSeederMultipleLeechers() throws Exception {

        Process tracker = createTracker(2999);
        Thread.sleep(1000);
        Process seeder = createSeeder("seeder", 3000, 2999);
        Thread.sleep(1000);
        Process leecher1 = createLeecher("leecher1", 3001);
        Thread.sleep(1000);
        Process leecher2 = createLeecher("leecher2", 3002);
        Thread.sleep(1000);
        Process leecher3 = createLeecher("leecher3", 3003);

        Timer timer = startTimeout(seeder, leecher1, leecher2, leecher3, tracker);
        Thread t1 = matchLine(leecher1, "datafile is complete");
        Thread t2 = matchLine(leecher2, "datafile is complete");
        Thread t3 = matchLine(leecher3, "datafile is complete");
        joinAll(t1, t2, t3);
        timer.cancel();
        killAll(seeder, leecher1, leecher2, leecher3, tracker);
    }

    @Test
    public void testSingleSeederMultipleLeechersSeederDies() throws Exception {

        Process tracker = createTracker(3999);
        Thread.sleep(1000);
        Process seeder = createSeeder("seeder", 4000, 3999);
        Thread.sleep(1000);
        Process leecher1 = createLeecher("leecher1", 4001);
        Thread.sleep(1000);
        Process leecher2 = createLeecher("leecher2", 4002);

        Timer timer = startTimeout(seeder, leecher1, leecher2, tracker);
        Thread t1 = matchLineAndKill(leecher1, "datafile is complete", seeder);
        Thread t2 = matchLineAndKill(leecher2, "datafile is complete", seeder);
        joinAll(t1, t2);
        timer.cancel();

        Process leecher3 = createLeecher("leecher3", 4003);
        Timer newTimer = startTimeout(seeder, leecher1, leecher2, leecher3, tracker);
        Thread t3 = matchLineAndKill(leecher3, "receive PIECE", leecher1);
        joinAll(t3);
        Thread t4 = matchLine(leecher3, "datafile is complete");
        joinAll(t4);
        newTimer.cancel();

        killAll(seeder, leecher1, leecher2, leecher3, tracker);
    }

    private Process createTracker(int port) throws IOException {

        MetaFile.writeTorrent(new File(TORRENT), new File(DATA), "localhost", String.valueOf(port));
        ProcessBuilder trackerPB = new ProcessBuilder("java","tracker/Tracker",String.valueOf(port))
                .redirectErrorStream(true)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT);
        return trackerPB.start();
    }

    private Process createSeeder(String name, int port, int trackerPort) throws IOException {

        ProcessBuilder pb = new ProcessBuilder("java","-cp","lib/json-20160212.jar:.","core/Client", name, String.valueOf(port), DATA, "localhost", String.valueOf(trackerPort))
                .redirectErrorStream(true)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT);
        return pb.start();
    }

    private Process createLeecher(String name, int port) throws IOException {

        Path dir = Paths.get(DOWNLOAD, name);
        Files.createDirectories(dir);
        ProcessBuilder pb = new ProcessBuilder("java","-cp","lib/json-20160212.jar:.","core/Client", name, String.valueOf(port), TORRENT, dir.toString())
                .redirectErrorStream(true);
        return pb.start();
    }

    private void shutdownHook(Process... procs) {

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                killAll(procs);
            }
        }));
    }

    private Timer startTimeout(Process... procs) {

        shutdownHook(procs);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                killAll(procs);
                fail("Timed out");
            }
        }, TIMEOUT * 1000);
        return timer;
    }

    private Thread matchLine(Process p, String match) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try (
                        InputStream in = p.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(in))
                        ) {

                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                        if (line.contains(match)) {
                            System.out.println("Matched");
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        return t;
    }

    private Thread matchLineAndKill(Process p, String match, Process toKill) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream in = p.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                        if (line.contains(match)) {
                            System.out.println("Matched");
                            if (toKill.isAlive()) {
                                System.out.println("Killed");
                                toKill.destroy();
                            }
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        return t;
    }

    private void joinAll(Thread... threads) throws InterruptedException {

        for (Thread t : threads) {
            t.join();
        }
    }

    private void killAll(Process... procs) {

        for (Process p : procs) {
            p.destroy();
        }
    }
}
