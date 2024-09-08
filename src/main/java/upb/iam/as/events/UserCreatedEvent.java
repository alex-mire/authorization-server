package upb.iam.as.events;

import org.springframework.context.ApplicationEvent;
import upb.iam.as.domain.useremail.UserEmail;

import java.util.UUID;

public class UserCreatedEvent extends ApplicationEvent {
    private static final long serialVersionUID = -6782340918273645289L;

    private final UserEmail userEmail;
    private String temporaryPassword;

    public UserCreatedEvent(UserEmail userEmail) {
        super(userEmail);
        this.userEmail = userEmail;
    }

    public UserCreatedEvent(UserEmail userEmail, String temporaryPassword) {
        super(userEmail);
        this.userEmail = userEmail;
        this.temporaryPassword = temporaryPassword;
    }

    public UUID getUser() {
        return userEmail.userId();
    }

    public String getEmail() {
        return userEmail.email();
    }

    public String getCreatedBy() {
        return userEmail.createdBy();
    }
    public String getPassword() {return temporaryPassword;}
}
