package com.example.gamesession.service;

import com.example.gamesession.model.SessionEvent;
import com.example.gamesession.model.SessionResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SessionEventPublisher {

    private final Map<String, List<SseEmitter>> emittersBySession = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String sessionId, SessionResponse currentSession) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emittersBySession.computeIfAbsent(sessionId, ignored -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(sessionId, emitter));
        emitter.onTimeout(() -> removeEmitter(sessionId, emitter));
        emitter.onError(throwable -> removeEmitter(sessionId, emitter));

        sendToEmitter(sessionId, emitter, SessionEvent.from("SNAPSHOT", currentSession, null));
        return emitter;
    }

    public void publish(String sessionId, SessionEvent event) {
        for (SseEmitter emitter : emittersBySession.getOrDefault(sessionId, List.of())) {
            sendToEmitter(sessionId, emitter, event);
        }
    }

    public void complete(String sessionId) {
        List<SseEmitter> emitters = emittersBySession.remove(sessionId);
        if (emitters == null) {
            return;
        }

        for (SseEmitter emitter : emitters) {
            emitter.complete();
        }
    }

    private void sendToEmitter(String sessionId, SseEmitter emitter, SessionEvent event) {
        try {
            emitter.send(SseEmitter.event().data(event));
        } catch (IOException | IllegalStateException exception) {
            removeEmitter(sessionId, emitter);
        }
    }

    private void removeEmitter(String sessionId, SseEmitter emitter) {
        List<SseEmitter> emitters = emittersBySession.get(sessionId);
        if (emitters == null) {
            return;
        }

        emitters.remove(emitter);
        if (emitters.isEmpty()) {
            emittersBySession.remove(sessionId);
        }
    }
}
