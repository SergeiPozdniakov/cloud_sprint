package com.clouds.cloud_sprint.controller;

import com.clouds.cloud_sprint.model.FileUploadProgressListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/progress")
public class ProgressController {
    @Autowired
    private FileUploadProgressListener progressListener;

    @GetMapping(value = "/upload", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter trackUploadProgress() {
        SseEmitter emitter = new SseEmitter();
        new Thread(() -> {
            try {
                while (progressListener.getPercentComplete() < 100) {
                    emitter.send(SseEmitter.event()
                            .data(progressListener.getPercentComplete())
                            .name("progress"));
                    Thread.sleep(1000);
                }
                emitter.send(SseEmitter.event()
                        .data(100)
                        .name("complete"));
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();
        return emitter;
    }

}
