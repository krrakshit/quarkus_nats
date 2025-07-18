package com.example.resource;

import com.example.model.NotificationMessage;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ServerEndpoint("/ws/notifications")
public class NotificationWebSocket {

    private static final Set<Session> sessions = new CopyOnWriteArraySet<>();
    private static final Map<Session, Long> lastSeen = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();
    private static final long HEARTBEAT_INTERVAL_MS = 30_000; //30 seconds
    private static final long TIMEOUT_MS = 90_000; // 90 seconds

    static {
        heartbeatExecutor.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            for (Session session : sessions) {
                // Send ping
                try {
                    if (session.isOpen()) {
                        session.getAsyncRemote().sendPing(java.nio.ByteBuffer.wrap(new byte[]{1}));
                    }
                } catch (Exception e) {
                    // If ping fails, close session
                    try { session.close(); } catch (Exception ignore) {}
                    sessions.remove(session);
                    lastSeen.remove(session);
                    System.out.println("websocket connection closed");
                }
                // Check for timeout
                Long last = lastSeen.get(session);
                if (last == null || now - last > TIMEOUT_MS) {
                    try { session.close(); } catch (Exception ignore) {}
                    sessions.remove(session);
                    lastSeen.remove(session);
                }
            }
        }, HEARTBEAT_INTERVAL_MS, HEARTBEAT_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        lastSeen.put(session, System.currentTimeMillis());
        System.out.println("Websocket connection added: " +session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        lastSeen.remove(session);
        // You can add a log here:
        System.out.println("WebSocket session closed: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        sessions.remove(session);
        lastSeen.remove(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // Update last seen on any message
        lastSeen.put(session, System.currentTimeMillis());
    }

    @OnMessage
    public void onPong(PongMessage pong, Session session) {
        // Update last seen on pong
        lastSeen.put(session, System.currentTimeMillis());
    }

    public static void broadcast(NotificationMessage notification) {
        String json = notification.toJsonString();
        for (Session session : sessions) {
            if (session.isOpen()) {
                session.getAsyncRemote().sendText(json);
            }
        }
    }
}