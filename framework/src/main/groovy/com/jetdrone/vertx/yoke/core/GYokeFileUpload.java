/**
 * Copyright 2011-2014 the original author or authors.
 */
package com.jetdrone.vertx.yoke.core;

import groovy.lang.Closure;

import java.nio.charset.Charset;

public class GYokeFileUpload extends YokeFileUpload {

    public GYokeFileUpload(YokeFileUpload fileUpload) {
        super(fileUpload.fileSystem, fileUpload.filename(), fileUpload.name(), fileUpload.contentType(), fileUpload.contentTransferEncoding(), fileUpload.charset(), fileUpload.size(), fileUpload.path());
    }

    /**
     * Returns the filename which was used when upload the file.
     */
    public String getFilename() {
        return filename();
    }

    /**
     * Returns the name of the attribute
     */
    public String getName() {
        return name();
    }

    /**
     * Returns the contentType for the upload
     */
    public String getContentType() {
        return contentType();
    }

    /**
     * Returns the contentTransferEncoding for the upload
     */
    public String getContentTransferEncoding() {
        return contentTransferEncoding();
    }

    /**
     * Returns the charset for the upload
     */
    public Charset getCharset() {
        return charset();
    }

    /**
     * Returns the size of the upload (in bytes)
     */
    public long getSize() {
        return size();
    }

    /**
     * Returns filesystem path location
     */
    public String getPath() {
        return path();
    }

    public void delete(final Closure closure) {
        fileSystem.delete(path(), result -> closure.call(result.cause()));
    }
}
