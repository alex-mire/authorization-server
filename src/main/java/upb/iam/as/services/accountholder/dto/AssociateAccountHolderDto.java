package upb.iam.as.services.accountholder.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssociateAccountHolderDto {
    private String userId;
    private String accountHolderId;
}
