package upb.iam.as.web.session.dto;

import jakarta.mail.Session;
import lombok.*;
import upb.iam.as.domain.session.SpringSession;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
@Getter
@Setter
public class SessionDto {
    private String primaryId;
    private String sessionId;
    private String creationTime;
    private String lastAccessTime;
    private Integer maxInactiveInterval;
    private String expiryTime;
    private String principalName;

    public SessionDto(SpringSession session) {
        this.primaryId = session.getPrimaryId();
        this.sessionId = session.getSessionId();
        this.creationTime = convertToLocalDateTime(session.getCreationTime());
        this.lastAccessTime = convertToLocalDateTime(session.getLastAccessTime());
        this.maxInactiveInterval = session.getMaxInactiveInterval();
        this.expiryTime = convertToLocalDateTime(session.getExpiryTime());
        this.principalName = session.getPrincipalName();
    }

    private static String convertToLocalDateTime(Long time) {
        Instant instant = Instant.ofEpochMilli(time);
        LocalDateTime localDateTime =
                LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = localDateTime.format(formatter);
        return formattedDateTime;
    }
}
