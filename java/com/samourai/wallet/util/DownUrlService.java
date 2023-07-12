package com.samourai.wallet.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DownUrlService {
  private static final Logger log = LoggerFactory.getLogger(DownUrlService.class);
  private static DownUrlService instance;

  private Map<String,Long> downtimeByUrl = new LinkedHashMap<>();

  public static DownUrlService getInstance() {
    if (instance == null) {
      instance = new DownUrlService();
    }
    return instance;
  }

  private DownUrlService() {
    this.downtimeByUrl = new LinkedHashMap<>();
  }

  public boolean isUp(String url) {
    Long downtime = downtimeByUrl.get(url);
    if (downtime != null) {
      if (downtime > System.currentTimeMillis()) {
        // down
        if (log.isDebugEnabled()) {
          log.debug("url is down til " + new Date(downtime) + ": " + url);
          return false;
        }
      } else {
        // expired
        clear(url);
      }
    }
    return true;
  }

  public Collection<String> filterUp(Collection<String> urls) {
    return urls.stream().filter(url -> isUp(url)).collect(Collectors.toList());
  }

  public void setDown(String url, long downDelayMs) {
    long downtime = System.currentTimeMillis()+downDelayMs;
    downtimeByUrl.put(url, downtime);
  }

  public void clear(String url) {
    downtimeByUrl.remove(url);
  }

  public void clear(Collection<String> urls) {
    urls.stream().forEach(url -> clear(url));
  }
}
