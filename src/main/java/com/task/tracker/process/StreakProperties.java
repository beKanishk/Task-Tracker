package com.task.tracker.process;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "streak")
@Getter
@Setter
public class StreakProperties {

    /**
     * Maximum number of days user can miss in one gap
     * Example:
     *   gap = 2 → yesterday + day before yesterday missed
     */
    private int maxGapDays;

    /**
     * How many forgiveness days are allowed per streak
     * 0 → hardcore
     * 1 → normal
     * 2+ → flexible / premium
     */
    private int forgivenessAllowed;

    private boolean allowForgiveness;
}
