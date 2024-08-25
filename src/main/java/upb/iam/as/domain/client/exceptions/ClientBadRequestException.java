package upb.iam.as.domain.client.exceptions;

public class ClientBadRequestException extends RuntimeException {
    public ClientBadRequestException(String message) {
        super(message);
    }
}
