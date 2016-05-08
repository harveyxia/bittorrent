package tests;

import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static org.junit.Assert.*;

/**
 * Created by marvin on 5/8/16.
 */
public class SeederLeecherTest {

    private static int TIMEOUT = 30;
    private static String TORRENT = "tests/test.torrent";
    private static String DATA = "tests/data";
    private static String DOWNLOAD = "tests/download";

    @Test
    public void testSingleSeederSingleLeecher() throws Exception {

        Process tracker = createTracker();
        Thread.sleep(1000);
        Process seeder = createPeer("seeder", 2000, true);
        Thread.sleep(1000);
        Process leecher = createPeer("leecher", 2001, false);

        shutdownHook(tracker, seeder, leecher);
        Timer timer = startTimeout();
        Thread t = matchLine(leecher, "datafile is complete");
        joinAll(timer, t);
    }

    @Test
    public void testSingleSeederMultipleLeechers() throws Exception {

        Process tracker = createTracker();
        Thread.sleep(1000);
        Process seeder = createPeer("seeder", 3000, true);
        Thread.sleep(1000);
        Process leecher1 = createPeer("leecher1", 3001, false);
        Thread.sleep(1000);
        Process leecher2 = createPeer("leecher2", 3002, false);
        Thread.sleep(1000);
        Process leecher3 = createPeer("leecher3", 3003, false);

        shutdownHook(tracker, seeder, leecher1, leecher2, leecher3);
        Timer timer = startTimeout();
        Thread t1 = matchLine(leecher1, "datafile is complete");
        Thread t2 = matchLine(leecher2, "datafile is complete");
        Thread t3 = matchLine(leecher3, "datafile is complete");
        joinAll(timer, t1, t2, t3);
    }

    @Test
    public void testSingleSeederMultipleLeechersSeederDies() throws Exception {

        Process tracker = createTracker();
        Thread.sleep(1000);
        Process seeder = createPeer("seeder", 4000, true);
        Thread.sleep(1000);
        Process leecher1 = createPeer("leecher1", 4001, false);
        Thread.sleep(1000);
        Process leecher2 = createPeer("leecher2", 4002, false);
        Thread.sleep(1000);
        Process leecher3 = createPeer("leecher3", 4003, false);

        shutdownHook(tracker, seeder, leecher1, leecher2, leecher3);
        Timer timer = startTimeout();
        Thread t1 = matchLineAndKill(leecher1, "datafile is complete", seeder);
        Thread t2 = matchLineAndKill(leecher2, "datafile is complete", seeder);
        Thread t3 = matchLineAndKill(leecher3, "datafile is complete", seeder);
        joinAll(timer, t1, t2, t3);
    }

    private Process createTracker() throws IOException {

        ProcessBuilder trackerPB = new ProcessBuilder("java","tracker/Tracker","6789").redirectErrorStream(true);
        return trackerPB.start();
    }

    private Process createPeer(String name, int port, boolean seeder) throws IOException {

        ProcessBuilder pb;
        if (seeder) {
            pb = new ProcessBuilder("java","-cp","lib/json-20160212.jar:.","core/Client", name, String.valueOf(port), TORRENT, DATA, "yeah");
        } else {
            Path dir = Paths.get(DOWNLOAD, name);
            Files.createDirectories(dir);
            pb = new ProcessBuilder("java","-cp","lib/json-20160212.jar:.","core/Client", name, String.valueOf(port), TORRENT, dir.toString());
        }
        pb.redirectErrorStream(true);
        return pb.start();
    }

    private void shutdownHook(Process... procs) {

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                for (Process p : procs) {
                    p.destroy();
                }
            }
        }));
    }

    private Timer startTimeout() {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
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
                        // System.out.println(line);
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
                try (
                        InputStream in = p.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(in))
                ) {

                    String line;
                    while ((line = br.readLine()) != null) {
                        // System.out.println(line);
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

    private void joinAll(Timer timer, Thread... threads) throws InterruptedException {

        for (Thread t : threads) {
            t.join();
        }
        timer.cancel();
    }
}
