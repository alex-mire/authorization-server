package upb.iam.as.web.group.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class GroupMinimalDto {
    private UUID id;
    private String name;
}
