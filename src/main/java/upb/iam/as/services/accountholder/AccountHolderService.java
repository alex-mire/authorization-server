package upb.iam.as.services.accountholder;

import org.apache.hc.client5.http.utils.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import upb.iam.as.domain.user.exception.UserBadRequestException;
import upb.iam.as.services.accountholder.dto.AccountHolderDto;
import upb.iam.as.services.accountholder.dto.AssociateAccountHolderDto;
import upb.iam.as.web.user.dto.UserAccountHolderDto;

import java.util.List;
import java.util.UUID;

@Service
public class AccountHolderService {

    @Value("${payment-service.baseurl}")
    private String paymentServiceBaseUrl;

    public List<AccountHolderDto> getAccountHolder() {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .basicAuthentication("job", "password")
                .build();

        String url = paymentServiceBaseUrl + "/account-holder/all";
        try {
            return restTemplate.exchange(url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<AccountHolderDto>>() {
                    }).getBody();
        } catch (RestClientException e) {
            // Handle all errors: connection issues, 4xx, 5xx, etc.
            System.err.println("Error during REST call: " + e.getMessage());
            e.printStackTrace();
            throw new UserBadRequestException(e.getMessage());
        }
    }

    public String associateAccountHolder(String accountHolderId, String userId) {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .basicAuthentication(
                        "job", "password"
                ).build();

        String url = paymentServiceBaseUrl + "/account-holder/associate";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        AssociateAccountHolderDto dto = new AssociateAccountHolderDto();
        dto.setAccountHolderId(accountHolderId);
        dto.setUserId(userId);
        HttpEntity<AssociateAccountHolderDto> requestEntity = new HttpEntity<>(dto, headers);

        // Send the POST request
        try {
            return restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            ).getBody();
        } catch (RestClientException e) {
            // Handle all errors: connection issues, 4xx, 5xx, etc.
            System.err.println("Error during REST call: " + e.getMessage());
            e.printStackTrace();
            throw new UserBadRequestException(e.getMessage());
        }
    }

    public UserAccountHolderDto getaccountHolderByUserId(UUID id) {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .basicAuthentication(
                        "job", "password"
                ).build();

        String url = paymentServiceBaseUrl + "/account-holder/user/" + id.toString();
        try {
            return restTemplate.exchange(url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<UserAccountHolderDto>() {
                    }).getBody();
        } catch (RestClientException e) {
            // Handle all errors: connection issues, 4xx, 5xx, etc.
            System.err.println("Error during REST call: " + e.getMessage());
            e.printStackTrace();
            return new UserAccountHolderDto();
        }
    }

    public String deassociateAccountHolder(String accountHolderId, String userId) {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .basicAuthentication(
                        "job", "password"
                ).build();

        String url = paymentServiceBaseUrl + "/account-holder/deassociate";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        AssociateAccountHolderDto dto = new AssociateAccountHolderDto();
        dto.setAccountHolderId(accountHolderId);
        dto.setUserId(userId);
        HttpEntity<AssociateAccountHolderDto> requestEntity = new HttpEntity<>(dto, headers);

        // Send the POST request
        try {
            return restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            ).getBody();
        } catch (RestClientException e) {
            // Handle all errors: connection issues, 4xx, 5xx, etc.
            System.err.println("Error during REST call: " + e.getMessage());
            e.printStackTrace();
            throw new UserBadRequestException(e.getMessage());
        }
    }

}
