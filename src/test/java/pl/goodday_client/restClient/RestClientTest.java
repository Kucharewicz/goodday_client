package pl.goodday_client.restClient;

import pl.goodday_client.dto.UserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RestClientTest {

    @Autowired
    private RestClient restClient;

    @Test
    @DisplayName("Should pass when server respond with status code 2xx")
    public void testUserLoginCorrectness(){
        String username = "admin";
        String password = "admin";
        UserDto loggedUser = restClient.login(username,password);
        Assertions.assertNotNull(loggedUser);
    }

}