package tests;

import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

import tracker.TrackerRequest.Event;
import tracker.*;
import core.Peer;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test tracker functionality.
 */
public class TrackerTest {

  private static final int TRACKER_PORT = 8888;
  private static final String FILE_NAME = "a";

  // private InetAddress localHost;

  @Test
  public void testNewFile() throws Exception {
    int client_port = 8000;
    int tracker_port = TRACKER_PORT + 0;
    final InetAddress LOCAL_HOST = InetAddress.getLocalHost();

    Tracker tracker = new Tracker(tracker_port);
    new Thread(tracker).start();

    TrackerClient trackerClientOne = new TrackerClient(
      new InetSocketAddress(LOCAL_HOST, client_port++),
      new InetSocketAddress(LOCAL_HOST, tracker_port), FILE_NAME);

    TrackerResponse resp;

    // requested file doesn't exist
    resp = trackerClientOne.update(Event.STARTED);
    assertEquals("STARTED interval", 2, resp.getInterval());
    assertEquals("STARTED seeders", 0, resp.getSeeders());
    assertEquals("STARTED leechers", 0, resp.getLeechers());
    assertEquals("STARTED peer list", 0, resp.getPeers().size());

    resp = trackerClientOne.update(Event.STOPPED);
    assertEquals("STOPPED interval", -1, resp.getInterval());

    resp = trackerClientOne.update(Event.PING);
    assertEquals("PING interval", -1, resp.getInterval());

    // submitting a new file
    resp = trackerClientOne.update(Event.COMPLETED);
    assertEquals("COMPLETED interval", 2, resp.getInterval());
    assertEquals("COMPLETED seeders", 1, resp.getSeeders());
    assertEquals("COMPLETED leechers", 0, resp.getLeechers());
    assertEquals("COMPLETED peer list", 1, resp.getPeers().size());

    // remove from peer list
    resp = trackerClientOne.update(Event.STOPPED);
    assertNull("STOPPED", resp);

    tracker.shutdown();
  }

  @Test
  public void testPeerList() throws Exception {
    int base_client_port = 7000;
    int client_port = base_client_port;
    int tracker_port = TRACKER_PORT + 1;
    final InetAddress LOCAL_HOST = InetAddress.getLocalHost();

    Tracker tracker = new Tracker(tracker_port);
    new Thread(tracker).start();

    TrackerClient trackerClientOne = new TrackerClient(
      new InetSocketAddress(LOCAL_HOST, client_port++),
      new InetSocketAddress(LOCAL_HOST, tracker_port), FILE_NAME);

    TrackerClient trackerClientTwo = new TrackerClient(
      new InetSocketAddress(LOCAL_HOST, client_port++),
      new InetSocketAddress(LOCAL_HOST, tracker_port), FILE_NAME);

    TrackerClient trackerClientThree = new TrackerClient(
      new InetSocketAddress(LOCAL_HOST, client_port++),
      new InetSocketAddress(LOCAL_HOST, tracker_port), FILE_NAME);

    TrackerResponse resp;

    // submitting a new file
    resp = trackerClientOne.update(Event.COMPLETED);

    // register another leecher
    resp = trackerClientTwo.update(Event.STARTED);
    assertEquals("STARTED1 interval", 2, resp.getInterval());
    assertEquals("STARTED1 seeders", 2, resp.getSeeders());
    assertEquals("STARTED1 leechers", 0, resp.getLeechers());
    assertEquals("STARTED1 peer list", 2, resp.getPeers().size());

    // Try pinging
    resp = trackerClientOne.update(Event.PING);
    assertEquals("PING1 interval", 2, resp.getInterval());
    assertEquals("PING1 seeders", 2, resp.getSeeders());
    assertEquals("PING1 leechers", 0, resp.getLeechers());
    assertEquals("PING1 peer list", 2, resp.getPeers().size());

    // register another leecher
    resp = trackerClientThree.update(Event.STARTED);
    assertEquals("STARTED2 interval", 2, resp.getInterval());
    assertEquals("STARTED2 seeders", 3, resp.getSeeders());
    assertEquals("STARTED2 leechers", 0, resp.getLeechers());
    assertEquals("STARTED2 peer list", 3, resp.getPeers().size());

    // Ping again
    resp = trackerClientTwo.update(Event.PING);
    assertEquals("PING2 interval", 2, resp.getInterval());
    assertEquals("PING2 seeders", 3, resp.getSeeders());
    assertEquals("PING2 leechers", 0, resp.getLeechers());
    assertEquals("PING2 peer list", 3, resp.getPeers().size());

    // check to see if peers are logged correctly
    List<Peer> peers = resp.getPeers();
    for (int i = 0; i < peers.size(); i++) {
      Peer peer = peers.get(i);
      assertEquals("PEER" + i, LOCAL_HOST, peer.getIp());
      assertEquals("PEER" + i, base_client_port + i, peer.getPort());
    }

    // close one
    resp = trackerClientOne.update(Event.STOPPED);
    assertNull("STOPPED1", resp);

    // Ping again
    resp = trackerClientTwo.update(Event.PING);
    assertEquals("PING3 interval", 2, resp.getInterval());
    assertEquals("PING3 seeders", 2, resp.getSeeders());
    assertEquals("PING3 leechers", 0, resp.getLeechers());
    assertEquals("PING3 peer list", 2, resp.getPeers().size());

    // check to see if peer 1 deleted properly
    peers = resp.getPeers();
    for (int i = 0; i < peers.size(); i++) {
      Peer peer = peers.get(i);
      assertEquals("PEER" + i + 1, LOCAL_HOST, peer.getIp());
      assertEquals("PEER" + i + 1, base_client_port + i + 1, peer.getPort());
    }

    // close three
    resp = trackerClientThree.update(Event.STOPPED);
    assertNull("STOPPED2", resp);

    // Ping again
    resp = trackerClientTwo.update(Event.PING);
    assertEquals("PING2 interval", 2, resp.getInterval());
    assertEquals("PING2 seeders", 1, resp.getSeeders());
    assertEquals("PING2 leechers", 0, resp.getLeechers());
    assertEquals("PING2 peer list", 1, resp.getPeers().size());

    // check to see if peer 3 deleted properly
    Peer peer = resp.getPeers().get(0);
    assertEquals("PEER2", LOCAL_HOST, peer.getIp());
    assertEquals("PEER2", base_client_port + 1, peer.getPort());

    resp = trackerClientTwo.update(Event.COMPLETED);
    assertNull("COMPLETED1", resp);

    tracker.shutdown();
  }

  @Test
  public void testTimeOut() throws Exception {
    int base_client_port = 6000;
    int client_port = base_client_port;
    int tracker_port = TRACKER_PORT + 2;
    final InetAddress LOCAL_HOST = InetAddress.getLocalHost();

    Tracker tracker = new Tracker(tracker_port);
    new Thread(tracker).start();

    TrackerClient trackerClientOne = new TrackerClient(
      new InetSocketAddress(LOCAL_HOST, client_port++),
      new InetSocketAddress(LOCAL_HOST, tracker_port), FILE_NAME);

    TrackerClient trackerClientTwo = new TrackerClient(
      new InetSocketAddress(LOCAL_HOST, client_port++),
      new InetSocketAddress(LOCAL_HOST, tracker_port), FILE_NAME);

    TrackerClient trackerClientThree = new TrackerClient(
      new InetSocketAddress(LOCAL_HOST, client_port++),
      new InetSocketAddress(LOCAL_HOST, tracker_port), FILE_NAME);

    TrackerResponse resp;

    // submitting a new file
    resp = trackerClientOne.update(Event.COMPLETED);

    // register leechers
    resp = trackerClientTwo.update(Event.STARTED);
    resp = trackerClientThree.update(Event.STARTED);

    Thread.sleep(1000);

    // Ping
    resp = trackerClientOne.update(Event.PING);
    assertEquals("PING1 interval", 2, resp.getInterval());
    assertEquals("PING1 seeders", 3, resp.getSeeders());
    assertEquals("PING1 leechers", 0, resp.getLeechers());
    assertEquals("PING1 peer list", 3, resp.getPeers().size());

    resp = trackerClientTwo.update(Event.PING);
    assertEquals("PING2 interval", 2, resp.getInterval());
    assertEquals("PING2 seeders", 3, resp.getSeeders());
    assertEquals("PING2 leechers", 0, resp.getLeechers());
    assertEquals("PING2 peer list", 3, resp.getPeers().size());

    Thread.sleep(1000);

    // Peer 3 should have timed out
    resp = trackerClientOne.update(Event.PING);
    assertEquals("PING3 interval", 2, resp.getInterval());
    assertEquals("PING3 seeders", 2, resp.getSeeders());
    assertEquals("PING3 leechers", 0, resp.getLeechers());
    assertEquals("PING3 peer list", 2, resp.getPeers().size());

    // check to see if peer 3 deleted properly
    List<Peer> peers = resp.getPeers();
    for (int i = 0; i < peers.size(); i++) {
      Peer peer = peers.get(i);
      assertEquals("PEER" + i, LOCAL_HOST, peer.getIp());
      assertEquals("PEER" + i, base_client_port + i, peer.getPort());
    }

    // Bring peer 3 back
    resp = trackerClientThree.update(Event.PING);
    assertEquals("PING4 interval", 2, resp.getInterval());
    assertEquals("PING4 seeders", 3, resp.getSeeders());
    assertEquals("PING4 leechers", 0, resp.getLeechers());
    assertEquals("PING4 peer list", 3, resp.getPeers().size());

    // let everyone timeout
    Thread.sleep(2000);

    // Bring peer 1 back
    resp = trackerClientThree.update(Event.PING);
    assertEquals("PING5 interval", 2, resp.getInterval());
    assertEquals("PING5 seeders", 1, resp.getSeeders());
    assertEquals("PING5 leechers", 0, resp.getLeechers());
    assertEquals("PING5 peer list", 1, resp.getPeers().size());

    tracker.shutdown();
  }

}