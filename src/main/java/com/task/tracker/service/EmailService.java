package com.task.tracker.service;

import com.task.tracker.model.Task;
import com.task.tracker.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromAddress;

    public void sendStreakWarningEmail(User user, int currentStreak) {
        if (fromAddress == null || fromAddress.isBlank()) {
            log.warn("Mail not configured — skipping streak warning email for {}", user.getUserName());
            return;
        }
        String subject = "Don't break your streak!";
        String body = buildStreakWarningBody(user.getName(), currentStreak);
        send(user.getEmail(), subject, body);
    }

    public void sendDailyReminderEmail(User user, List<Task> tasks) {
        if (fromAddress == null || fromAddress.isBlank()) {
            log.warn("Mail not configured — skipping daily reminder email for {}", user.getUserName());
            return;
        }
        String subject = "Daily Habit Check-in";
        String body = buildDailyReminderBody(user.getName(), tasks);
        send(user.getEmail(), subject, body);
    }

    private void send(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email sent to {}: {}", to, subject);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    private String buildStreakWarningBody(String name, int currentStreak) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"></head>
                <body style="margin:0;padding:0;background:#0a0a14;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0" style="background:#0a0a14;padding:40px 16px;">
                    <tr><td align="center">
                      <table width="540" cellpadding="0" cellspacing="0" style="max-width:540px;width:100%%;">

                        <!-- HEADER -->
                        <tr>
                          <td style="background:linear-gradient(135deg,#0f0f1a 0%%,#1a1a2e 100%%);border-radius:20px 20px 0 0;border:1px solid #2a2a42;border-bottom:none;padding:28px 32px 24px;">
                            <table width="100%%" cellpadding="0" cellspacing="0">
                              <tr>
                                <td>
                                  <span style="font-size:11px;font-weight:700;letter-spacing:3px;color:#22c55e;text-transform:uppercase;">Habit Tracker</span>
                                  <h1 style="margin:8px 0 0;font-size:22px;font-weight:700;color:#ffffff;letter-spacing:-0.3px;">Streak Alert</h1>
                                </td>
                                <td align="right" style="font-size:36px;">🔥</td>
                              </tr>
                            </table>
                          </td>
                        </tr>

                        <!-- STREAK BADGE -->
                        <tr>
                          <td style="background:#111125;border-left:1px solid #2a2a42;border-right:1px solid #2a2a42;padding:0 32px;">
                            <div style="background:linear-gradient(135deg,#f59e0b18,#ef444418);border:1px solid #f59e0b40;border-radius:14px;padding:20px 24px;margin:24px 0 0;">
                              <p style="margin:0 0 4px;font-size:11px;font-weight:700;letter-spacing:2px;color:#f59e0b;text-transform:uppercase;">Current Streak</p>
                              <p style="margin:0;font-size:42px;font-weight:800;color:#ffffff;line-height:1;">%d <span style="font-size:20px;color:#9ca3af;font-weight:400;">days</span></p>
                            </div>
                          </td>
                        </tr>

                        <!-- BODY -->
                        <tr>
                          <td style="background:#111125;border-left:1px solid #2a2a42;border-right:1px solid #2a2a42;padding:24px 32px;">
                            <p style="margin:0 0 6px;font-size:16px;color:#f3f4f6;">Hi <strong style="color:#ffffff;">%s</strong>,</p>
                            <p style="margin:0 0 16px;font-size:15px;color:#9ca3af;line-height:1.7;">
                              You haven't logged any tasks yet today and your streak is at risk. Don't let your progress slip — just one log keeps it alive!
                            </p>
                            <table cellpadding="0" cellspacing="0" style="margin:8px 0 0;">
                              <tr>
                                <td style="background:linear-gradient(135deg,#16a34a,#22c55e);border-radius:10px;padding:1px;">
                                  <div style="background:linear-gradient(135deg,#16a34a,#22c55e);border-radius:9px;padding:12px 28px;">
                                    <span style="font-size:14px;font-weight:700;color:#ffffff;letter-spacing:0.3px;">Log a task now →</span>
                                  </div>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>

                        <!-- DIVIDER -->
                        <tr>
                          <td style="background:#111125;border-left:1px solid #2a2a42;border-right:1px solid #2a2a42;padding:0 32px;">
                            <div style="border-top:1px solid #1e1e30;"></div>
                          </td>
                        </tr>

                        <!-- TIP -->
                        <tr>
                          <td style="background:#111125;border-left:1px solid #2a2a42;border-right:1px solid #2a2a42;padding:20px 32px;">
                            <table cellpadding="0" cellspacing="0" width="100%%">
                              <tr>
                                <td style="background:#1a1a2e;border-radius:10px;padding:14px 16px;">
                                  <p style="margin:0 0 4px;font-size:11px;font-weight:700;letter-spacing:2px;color:#6366f1;text-transform:uppercase;">💡 Quick Tip</p>
                                  <p style="margin:0;font-size:13px;color:#9ca3af;line-height:1.6;">Consistency beats intensity. Even marking one small task as complete today counts!</p>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>

                        <!-- FOOTER -->
                        <tr>
                          <td style="background:#0d0d1f;border-radius:0 0 20px 20px;border:1px solid #2a2a42;border-top:1px solid #1e1e30;padding:20px 32px;">
                            <p style="margin:0 0 4px;font-size:12px;color:#4b5563;text-align:center;">
                              <strong style="color:#22c55e;">Habit Tracker</strong> — Build better habits, one day at a time
                            </p>
                            <p style="margin:0;font-size:11px;color:#374151;text-align:center;">
                              You're receiving this because you enabled streak warning emails in Settings.
                            </p>
                          </td>
                        </tr>

                      </table>
                    </td></tr>
                  </table>
                </body>
                </html>
                """.formatted(currentStreak, name);
    }

    private String buildDailyReminderBody(String name, List<Task> tasks) {
        // Build task rows HTML
        StringBuilder taskRows = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            String borderTop = i == 0 ? "" : "border-top:1px solid #1e1e30;";
            String typeLabel = "QUANTITATIVE".equals(t.getTaskType() != null ? t.getTaskType().name() : "")
                    ? "Quantitative"
                    : "Boolean";
            String typeBadgeColor = "QUANTITATIVE".equals(t.getTaskType() != null ? t.getTaskType().name() : "")
                    ? "#6366f1"
                    : "#22c55e";
            taskRows.append("""
                    <tr>
                      <td style="padding:14px 16px;%s">
                        <table width="100%%" cellpadding="0" cellspacing="0">
                          <tr>
                            <td>
                              <span style="font-size:14px;color:#f3f4f6;font-weight:600;">%s</span>
                              <span style="display:inline-block;margin-left:8px;font-size:10px;font-weight:700;letter-spacing:1px;color:%s;text-transform:uppercase;background:%s18;border:1px solid %s40;border-radius:4px;padding:1px 6px;">%s</span>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                    """.formatted(borderTop,
                    escapeHtml(t.getTitle()),
                    typeBadgeColor, typeBadgeColor, typeBadgeColor,
                    typeLabel));
        }

        String taskSection = tasks.isEmpty()
                ? "<p style=\"margin:0;font-size:14px;color:#6b7280;\">No specific tasks selected — log any task from your dashboard.</p>"
                : "<table width=\"100%%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background:#1a1a2e;border:1px solid #2a2a42;border-radius:10px;overflow:hidden;\">"
                  + taskRows
                  + "</table>";

        return """
                <!DOCTYPE html>
                <html lang="en">
                <head><meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"></head>
                <body style="margin:0;padding:0;background:#0a0a14;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0" style="background:#0a0a14;padding:40px 16px;">
                    <tr><td align="center">
                      <table width="540" cellpadding="0" cellspacing="0" style="max-width:540px;width:100%%;">

                        <!-- HEADER -->
                        <tr>
                          <td style="background:linear-gradient(135deg,#0f0f1a 0%%,#1a1a2e 100%%);border-radius:20px 20px 0 0;border:1px solid #2a2a42;border-bottom:none;padding:28px 32px 24px;">
                            <table width="100%%" cellpadding="0" cellspacing="0">
                              <tr>
                                <td>
                                  <span style="font-size:11px;font-weight:700;letter-spacing:3px;color:#22c55e;text-transform:uppercase;">Habit Tracker</span>
                                  <h1 style="margin:8px 0 0;font-size:22px;font-weight:700;color:#ffffff;letter-spacing:-0.3px;">Daily Check-in</h1>
                                </td>
                                <td align="right" style="font-size:36px;">📅</td>
                              </tr>
                            </table>
                          </td>
                        </tr>

                        <!-- GREETING BANNER -->
                        <tr>
                          <td style="background:#111125;border-left:1px solid #2a2a42;border-right:1px solid #2a2a42;padding:0 32px;">
                            <div style="background:linear-gradient(135deg,#22c55e18,#6366f118);border:1px solid #22c55e30;border-radius:14px;padding:20px 24px;margin:24px 0 0;">
                              <p style="margin:0 0 6px;font-size:16px;color:#f3f4f6;">Good day, <strong style="color:#ffffff;">%s</strong>! 👋</p>
                              <p style="margin:0;font-size:14px;color:#9ca3af;line-height:1.6;">Here are the tasks you wanted to be reminded about today.</p>
                            </div>
                          </td>
                        </tr>

                        <!-- TASK LIST -->
                        <tr>
                          <td style="background:#111125;border-left:1px solid #2a2a42;border-right:1px solid #2a2a42;padding:20px 32px;">
                            <p style="margin:0 0 12px;font-size:11px;font-weight:700;letter-spacing:2px;color:#9ca3af;text-transform:uppercase;">Today's Reminders</p>
                            %s
                          </td>
                        </tr>

                        <!-- CTA -->
                        <tr>
                          <td style="background:#111125;border-left:1px solid #2a2a42;border-right:1px solid #2a2a42;padding:4px 32px 24px;">
                            <table cellpadding="0" cellspacing="0" style="margin-top:16px;">
                              <tr>
                                <td style="background:linear-gradient(135deg,#16a34a,#22c55e);border-radius:10px;padding:12px 28px;">
                                  <span style="font-size:14px;font-weight:700;color:#ffffff;letter-spacing:0.3px;">Open Dashboard →</span>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>

                        <!-- DIVIDER -->
                        <tr>
                          <td style="background:#111125;border-left:1px solid #2a2a42;border-right:1px solid #2a2a42;padding:0 32px;">
                            <div style="border-top:1px solid #1e1e30;"></div>
                          </td>
                        </tr>

                        <!-- QUOTE -->
                        <tr>
                          <td style="background:#111125;border-left:1px solid #2a2a42;border-right:1px solid #2a2a42;padding:20px 32px;">
                            <table cellpadding="0" cellspacing="0" width="100%%">
                              <tr>
                                <td style="background:#1a1a2e;border-left:3px solid #22c55e;border-radius:0 10px 10px 0;padding:14px 16px;">
                                  <p style="margin:0;font-size:13px;color:#9ca3af;line-height:1.6;font-style:italic;">"We are what we repeatedly do. Excellence, then, is not an act, but a habit."</p>
                                  <p style="margin:6px 0 0;font-size:11px;color:#6b7280;">— Aristotle</p>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>

                        <!-- FOOTER -->
                        <tr>
                          <td style="background:#0d0d1f;border-radius:0 0 20px 20px;border:1px solid #2a2a42;border-top:1px solid #1e1e30;padding:20px 32px;">
                            <p style="margin:0 0 4px;font-size:12px;color:#4b5563;text-align:center;">
                              <strong style="color:#22c55e;">Habit Tracker</strong> — Build better habits, one day at a time
                            </p>
                            <p style="margin:0;font-size:11px;color:#374151;text-align:center;">
                              You're receiving this because you enabled daily reminder emails in Settings.
                            </p>
                          </td>
                        </tr>

                      </table>
                    </td></tr>
                  </table>
                </body>
                </html>
                """.formatted(name, taskSection);
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
