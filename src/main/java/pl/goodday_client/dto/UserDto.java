package pl.goodday_client.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.http.HttpHeaders;

@Data
public class UserDto {
    private String username;
    private String password;
    private String matchingPassword;
    @JsonIgnore
    private HttpHeaders headerWithCredentials;
}