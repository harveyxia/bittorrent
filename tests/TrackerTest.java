package tests;

import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import tracker.TrackerRequest.Event;
import tracker.*;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test tracker functionality.
 */
public class TrackerTest {

  private static final int TRACKER_PORT = 8888;
  private static final int CLIENT_PORT_ONE = 7777;
  private static final int CLIENT_PORT_TWO = 6666;
  private static final int CLIENT_PORT_THREE = 5555;
  private static final String FILE_NAME = "dummy.txt";

  // private InetAddress localHost;

  @Test
  public void testTracker() throws Exception {
    final InetAddress LOCAL_HOST = InetAddress.getLocalHost();

    Tracker tracker = new Tracker(TRACKER_PORT);
    new Thread(tracker).start();

    TrackerClient trackerClientOne = new TrackerClient(
      new InetSocketAddress(LOCAL_HOST, CLIENT_PORT_ONE),
      new InetSocketAddress(LOCAL_HOST, TRACKER_PORT), FILE_NAME);

    TrackerResponse resp;

    // requested file doesn't exist
    resp = trackerClientOne.update(Event.STARTED);
    assertEquals(2, resp.getInterval());
    assertEquals(0, resp.getSeeders());
    assertEquals(0, resp.getLeechers());
    assertEquals(0, resp.getPeers().size());

    resp = trackerClientOne.update(Event.STOPPED);
    assertEquals(-1, resp.getInterval());

    resp = trackerClientOne.update(Event.PING);
    assertEquals(-1, resp.getInterval());

    // submitting a new file
    resp = trackerClientOne.update(Event.COMPLETED);
    assertEquals(2, resp.getInterval());
    assertEquals(1, resp.getSeeders());
    assertEquals(0, resp.getLeechers());
    assertEquals(1, resp.getPeers().size());

    tracker.shutdown();
  }
}