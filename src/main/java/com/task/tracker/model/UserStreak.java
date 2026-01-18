package com.task.tracker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "user_streaks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStreak {

    @Id
    private String userId;

    private int currentStreak;
    private int longestStreak;

    private LocalDate lastActiveDate;
    private LocalDate lastBrokenDate;

    /** How many forgiveness days already used in current streak */
    private int forgivenessUsed;

    /** How many forgiveness days are allowed per streak */
    private int forgivenessAllowed;

    /* ======================================================
       DOMAIN METHODS
       ====================================================== */

    /** First ever activity */
    public void start(LocalDate today, int forgivenessAllowed) {
        this.currentStreak = 1;
        this.longestStreak = 1;
        this.lastActiveDate = today;
        this.lastBrokenDate = null;
        this.forgivenessUsed = 0;
        this.forgivenessAllowed = forgivenessAllowed;
    }

    /** Normal consecutive day */
    public void increment(LocalDate today) {
        this.currentStreak++;
        this.longestStreak = Math.max(this.longestStreak, this.currentStreak);
        this.lastActiveDate = today;
    }

    /** Consume forgiveness for missed days */
    public void consumeForgiveness(int missedDays, LocalDate today) {
        this.forgivenessUsed += missedDays;
        this.lastActiveDate = today;
    }

    /** Reset streak completely */
    public void reset(LocalDate today, int forgivenessAllowed) {
        this.lastBrokenDate = today.minusDays(1);

        this.currentStreak = 1;
        this.forgivenessUsed = 0;
        this.forgivenessAllowed = forgivenessAllowed;

        this.lastActiveDate = today;
        this.longestStreak = Math.max(this.longestStreak, this.currentStreak);
    }

    /* ======================================================
       HELPERS
       ====================================================== */

    public boolean canUseForgiveness(int missedDays) {
        return forgivenessUsed + missedDays <= forgivenessAllowed;
    }

    public boolean isPerfectStreak() {
        return forgivenessUsed == 0;
    }
}
