package com.pointwest.prop.email.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "prop.email.dispatch")
public class EmailProperties {

    /** How often the dispatcher polls for PENDING rows, in milliseconds. */
    private long fixedDelayMs = 30_000;

    /** Max rows pulled per dispatch cycle. */
    private int batchSize = 20;

    /** Attempts before a row is marked FAILED_PERMANENT. */
    private int maxRetries = 3;

    public long getFixedDelayMs() {
        return fixedDelayMs;
    }

    public void setFixedDelayMs(long fixedDelayMs) {
        this.fixedDelayMs = fixedDelayMs;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }
}
