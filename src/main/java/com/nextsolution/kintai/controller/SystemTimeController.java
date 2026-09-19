package com.nextsolution.kintai.controller;

import com.nextsolution.kintai.service.DevClock;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
public class SystemTimeController {
    private final DevClock clock;
    public SystemTimeController(DevClock clock) { this.clock = clock; }

    @GetMapping("/time")
    public Map<String,Object> time() {
        return Map.of("now", clock.now(), "fixedEnabled", clock.isFixedEnabled());
    }

    @PostMapping("/dev-time")
    public Map<String,Object> setDevTime(@RequestParam String dateTime,
                                          @RequestParam(defaultValue="true") boolean enabled) {
        clock.set(LocalDateTime.parse(dateTime), enabled);
        return time();
    }
}
