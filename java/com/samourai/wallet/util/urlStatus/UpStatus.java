package com.samourai.wallet.util.urlStatus;

public class UpStatus {
    String id;
    private boolean up;
    private long lastCheck;
    private long expiration;
    private long since;
    private Exception downReason;

    public UpStatus(String id, boolean up, long expirationDelay, Exception downReason) {
        long now = System.currentTimeMillis();
        this.id = id;
        this.up = up;
        this.lastCheck = now;
        this.expiration = now+expirationDelay;
        this.since = now;
        this.downReason = downReason;
    }

    public void setStatus(boolean up, long expirationDelay, Exception downReason) {
        long now = System.currentTimeMillis();
        if (up != this.up) {
            this.up = up;
            this.since = now;
        }
        this.lastCheck = now;
        this.expiration = now+expirationDelay;
        this.downReason = downReason;
    }

    public String getId() {
        return id;
    }

    public boolean isExpired() {
        return expiration < System.currentTimeMillis();
    }

    public boolean isUp() {
        return up;
    }

    public long getLastCheck() {
        return lastCheck;
    }

    public long getExpiration() {
        return expiration;
    }

    public long getSince() {
        return since;
    }

    public Exception getDownReason() {
        return downReason;
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", up=" + up +
                ", lastCheck=" + lastCheck +
                ", expiration=" + expiration +
                ", since=" + since +
                ", downReason=" + downReason;
    }
}
