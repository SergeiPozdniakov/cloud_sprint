package com.clouds.cloud_sprint.model;

import org.springframework.stereotype.Component;

@Component
public class FileUploadProgressListener {

    private long bytesRead = 0;
    private long contentLength = 0;
    private int percentComplete = 0;

    public void update(long bytesRead, long contentLength) {
        this.bytesRead = bytesRead;
        this.contentLength = contentLength;
        this.percentComplete = (int) ((bytesRead * 100) / contentLength);
    }

    public int getPercentComplete() {
        return percentComplete;
    }

    public long getBytesRead() {
        return bytesRead;
    }

    public void reset() {
        this.bytesRead = 0;
        this.contentLength = 0;
        this.percentComplete = 0;
    }
}
