package com.backend.api.game.handler;

import com.backend.api.game.entity.Board;
import com.backend.api.game.entity.Player;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameHandler extends TextWebSocketHandler {

    private final Map<String, Player> players = new ConcurrentHashMap<>();

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger((GameHandler.class));

    private Board board = new Board();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String sessionId = session.getId();
        sessions.put(session.getId(), session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        String sessionId = session.getId();
        String payload = message.getPayload();
        JSONObject json = new JSONObject(payload);
        String type = json.getString("type");

        // Broadcast it to all other players
        switch (type) {
            case "player-start" -> {
                Player player = new Player(sessionId);
                this.players.put(sessionId, player);
                send(session, new JSONObject().put("type", "player-connected").put("id", sessionId));
            }

            case "player-move" -> {
                Player p = players.get(sessionId);
                if (p == null) return;

                double x = json.getDouble("x");
                double y = json.getDouble("y");
                p.setX(x);
                p.setY(y);

                if (isOutOfBounds(p)) {
                    removePlayer(sessionId);
                    send(session, new JSONObject().put("type", "player-dead"));
                } else {
                    broadcastPlayerUpdate(p);
                }
            }

            case "player-dead" -> {
                removePlayer(sessionId);
                broadcast(new JSONObject().put("type", "player-dead").put("id", sessionId));
            }
            case "request-existing-players" -> {
                send(session, new JSONObject().put("type", "existing-players").put("players", players.values()));
            }

        }
    }

    private void removePlayer(String sessionId) {
        players.remove(sessionId);
        sessions.remove(sessionId);
        broadcast(new JSONObject().put("type", "player-removed").put("id", sessionId));
    }

    private void broadcastPlayerUpdate(Player p) {
        JSONObject msg = new JSONObject()
                .put("type", "player-update")
                .put("id", p.getSessionId())
                .put("x", p.getX())
                .put("y", p.getY());

        broadcast(msg);
    }

    private boolean isOutOfBounds(Player p) {
        return p.getX() < this.board.getXmin() || p.getX() > this.board.getXmax() || p.getY() < this.board.getYmin() || p.getY() > this.board.getYmax();
    }

    private void broadcast(JSONObject msg) {
        sessions.values().forEach(s -> {
            try {
                if (s.isOpen()) {
                    synchronized (s) {
                        s.sendMessage(new TextMessage(msg.toString()));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void send(WebSocketSession session, JSONObject msg) {
        try {
            if (session.isOpen()) {
                synchronized (session) {
                    session.sendMessage(new TextMessage(msg.toString()));
                }
            }
        } catch (IOException e) {
            logger.info("failed to send message");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String sessionId = session.getId();
        this.removePlayer(sessionId);
    }
}
