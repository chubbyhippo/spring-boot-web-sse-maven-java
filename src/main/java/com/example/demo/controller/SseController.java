package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@CrossOrigin("*")
@RestController
public class SseController {
    final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    private SseEmitter sseEmitter;

    @RequestMapping("/subscribe")
    public SseEmitter subscribe() throws IOException {
        var sseEmitter = new SseEmitter(5000L);
        sseEmitter.send(SseEmitter.event().name("eventName").data("data"));
        sseEmitter.onCompletion(() -> emitters.remove(sseEmitter));
        emitters.add(sseEmitter);
        return sseEmitter;
    }

    @RequestMapping("/publish/{data}")
    public void publish(@PathVariable String data) {
        emitters.forEach(sseEmitter -> {
            try {
                sseEmitter.send(SseEmitter.event().name("eventName").data(data));
            } catch (IOException e) {
                emitters.remove(sseEmitter);
            }
        });
    }

    @RequestMapping("/complete")
    public void complete(){
        emitters.forEach(ResponseBodyEmitter::complete);
    }
}
