package pl.goodday_client.restClient;

import pl.goodday_client.dto.DailyTask;
import pl.goodday_client.dto.GoldenThought;
import pl.goodday_client.dto.UserDto;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class RestClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${motivation.server.host}")
    private String serverHost;

    public UserDto login(String username, String password) {
        String url = String.format("%s/login", serverHost);
        HttpHeaders headerWithUserData = createHeaderWithUserData(username, password);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headerWithUserData), String.class);
        HttpStatus statusCode = responseEntity.getStatusCode();
        if (statusCode.is2xxSuccessful()) {
            UserDto userDto = new UserDto();
            userDto.setHeaderWithCredentials(headerWithUserData);
            return userDto;
        }
        return null;
    }

    public HttpStatus register(UserDto userData) {
        HttpEntity<UserDto> userDtoHttpEntity = new HttpEntity<>(userData);
        ResponseEntity<String> responseEntity = restTemplate.exchange(String.format("%s/register", serverHost), HttpMethod.POST, userDtoHttpEntity, String.class);
        return responseEntity.getStatusCode();
    }

    public List<DailyTask> getAllTasksForUser(UserDto userDto) {
        HttpHeaders headerWithCredentials = userDto.getHeaderWithCredentials();
        String url = String.format("%s/findAllTasksForUser", serverHost);
        ResponseEntity<DailyTask[]> exchange = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headerWithCredentials), DailyTask[].class);
        return Arrays.stream(Objects.requireNonNull(exchange.getBody())).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public GoldenThought getGoldenThought() {
        String url = String.format("%s/goldenThought", serverHost);
        return restTemplate.getForObject(url, GoldenThought.class);
    }

    private HttpHeaders createHeaderWithUserData(String username, String password) {
        String basicAuthString = username + ":" + password;
        byte[] encodedAuthData = Base64.encodeBase64(basicAuthString.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuthData);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", authHeader);
        return httpHeaders;
    }

    public ResponseEntity<Integer> saveTask(UserDto userDto, DailyTask dailyTask) {
        HttpHeaders headerWithCredentials = userDto.getHeaderWithCredentials();
        String url = String.format("%s/addTask", serverHost);
        return restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(dailyTask, headerWithCredentials), Integer.class);
    }

    public DailyTask findTaskById(UserDto userDto, int id) {
        HttpHeaders headerWithCredentials = userDto.getHeaderWithCredentials();
        String url = String.format("%s/getTask/%d", serverHost,id);
        return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headerWithCredentials), DailyTask.class).getBody();
    }

    public ResponseEntity<Boolean> deleteTask(UserDto userDto, int id) {
        HttpHeaders headerWithCredentials = userDto.getHeaderWithCredentials();
        String url = String.format("%s/deleteTask/%d", serverHost,id);
        return restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headerWithCredentials), Boolean.class);
    }
}
