package com.samourai.wallet.util.urlStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UpStatusPool {
  private static final Logger log = LoggerFactory.getLogger(UpStatusPool.class);

  private long retryDelayMs;
  private Map<String, UpStatus> upStatusById;

  public UpStatusPool(long retryDelayMs) {
    this.retryDelayMs = retryDelayMs;
    this.upStatusById = new LinkedHashMap<>();
  }

  public UpStatus getUrlStatus(String id) {
    UpStatus upStatus = upStatusById.get(id);
    if (upStatus != null && upStatus.isExpired()) {
      upStatus = null;
    }
    return upStatus;
  }

  public boolean isDown(String id) {
    UpStatus upStatus = getUrlStatus(id);
    if (upStatus != null) {
      if (!upStatus.isUp()) {
        return true; // down
      }
    }
    return false; // up
  }

  public Collection<String> filterNotDown(Collection<String> urls) {
    return urls.stream().filter(url -> !isDown(url)).collect(Collectors.toList());
  }

  protected void setStatus(String id, boolean up, Exception downReason) {
    UpStatus upStatus = upStatusById.get(id);
    boolean statusChanged = (upStatus == null && !up) || (upStatus != null && up != upStatus.isUp());
    if (upStatus != null) {
      // update existing status
      upStatus.setStatus(up, retryDelayMs, downReason);
    } else {
      // create new status
      upStatus = new UpStatus(id, up, retryDelayMs, downReason);
      upStatusById.put(id, upStatus);
    }
    if (statusChanged) {
      if (log.isDebugEnabled()) {
        log.debug("upStatus changed: "+upStatus);
      }
    }
  }

  public void setStatusUp(String url) {
    setStatus(url, true, null);
  }

  public void setStatusDown(String url, Exception downReason) {
    setStatus(url, false, downReason);
  }

  public void clear(String url) {
    upStatusById.remove(url);
  }

  public void clear(Collection<String> urls) {
    urls.stream().forEach(url -> clear(url));
  }

  public Collection<UpStatus> getList() {
    return upStatusById.values().stream().filter(upStatus -> !upStatus.isExpired()).collect(Collectors.toList());
  }
}
