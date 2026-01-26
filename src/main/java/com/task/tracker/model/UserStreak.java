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

    /** Forgiveness usage */
    private int forgivenessUsed;
    private int forgivenessAllowed;

    /** NEW: forgiveness decision state */
    private boolean forgivenessPending;
    private int pendingMissedDays;

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

        this.forgivenessPending = false;
        this.pendingMissedDays = 0;
    }

    /** Normal consecutive day */
    public void increment(LocalDate today) {
        this.currentStreak++;
        this.longestStreak = Math.max(this.longestStreak, this.currentStreak);
        this.lastActiveDate = today;
    }

    /** Mark forgiveness as pending (WAIT for user decision) */
    public void markForgivenessPending(int missedDays) {
        this.forgivenessPending = true;
        this.pendingMissedDays = missedDays;
    }

    /** User ACCEPTS forgiveness */
    public void consumeForgiveness(LocalDate today) {
        this.forgivenessUsed += this.pendingMissedDays;
        this.lastActiveDate = today;

        clearPendingForgiveness();
    }

    /** User DECLINES forgiveness → streak breaks */
    public void reset(LocalDate today, int forgivenessAllowed) {
        this.lastBrokenDate = today.minusDays(1);

        // preserve longest streak BEFORE reset
        this.longestStreak = Math.max(this.longestStreak, this.currentStreak);

        this.currentStreak = 1;
        this.forgivenessUsed = 0;
        this.forgivenessAllowed = forgivenessAllowed;

        this.lastActiveDate = today;

        clearPendingForgiveness();
    }


    /* ======================================================
       HELPERS
       ====================================================== */

    public boolean canUseForgiveness(int missedDays) {
        return forgivenessUsed + missedDays <= forgivenessAllowed;
    }

    public boolean isForgivenessDecisionRequired() {
        return forgivenessPending;
    }

    public boolean isPerfectStreak() {
        return forgivenessUsed == 0;
    }

    private void clearPendingForgiveness() {
        this.forgivenessPending = false;
        this.pendingMissedDays = 0;
    }
}