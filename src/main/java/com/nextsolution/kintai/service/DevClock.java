package com.nextsolution.kintai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.time.*;

@Component
public class DevClock {
    private final ZoneId zoneId = ZoneId.of("Asia/Tokyo");
    private volatile boolean fixedEnabled;
    private volatile LocalDateTime fixedDateTime;

    public DevClock(@Value("${app.time.fixed-enabled:true}") boolean fixedEnabled,
                    @Value("${app.time.fixed:2026-07-31T17:30:00}") String fixed) {
        this.fixedEnabled = fixedEnabled;
        this.fixedDateTime = LocalDateTime.parse(fixed);
    }
    public LocalDateTime now() { return fixedEnabled ? fixedDateTime : LocalDateTime.now(zoneId); }
    public LocalDate today() { return now().toLocalDate(); }
    public boolean isFixedEnabled() { return fixedEnabled; }
    public LocalDateTime getFixedDateTime() { return fixedDateTime; }
    public synchronized void set(LocalDateTime value, boolean enabled) {
        this.fixedDateTime = value; this.fixedEnabled = enabled;
    }
}
