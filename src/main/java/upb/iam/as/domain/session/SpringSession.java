package upb.iam.as.domain.session;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;


@Table("spring_session")
@Getter
@Setter
public class SpringSession {
    private String primaryId;
    private String sessionId;
    private Long creationTime;
    private Long lastAccessTime;
    private Integer maxInactiveInterval;
    private Long expiryTime;
    private String principalName;

}
