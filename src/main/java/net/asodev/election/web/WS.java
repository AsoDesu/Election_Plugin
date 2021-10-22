package net.asodev.election.web;

import net.asodev.election.Main;
import net.asodev.election.MainInstance;
import net.asodev.election.manager.VotingManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@WebSocket
public class WS {

    private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();

    @OnWebSocketConnect
    public void connect(Session session) throws IOException {
        sessions.add(session);
        if (MainInstance.getPlugin().getVotingManager().isVoting()) {
            session.getRemote().sendString(MainInstance.getPlugin().getVotingManager().getWsMsg());
        }
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        sessions.remove(session);
    }

    @OnWebSocketMessage
    public void message(Session session, String message) {

    }

    public static void broadcast(String msg) {
        sessions.forEach(s -> {
            try { s.getRemote().sendString(msg);
            } catch (IOException ignored) { }
        });
    }

}
