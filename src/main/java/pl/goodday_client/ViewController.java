package pl.goodday_client;

import pl.goodday_client.restClient.RestClient;
import pl.goodday_client.dto.DailyTask;
import pl.goodday_client.dto.GoldenThought;
import pl.goodday_client.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Controller
@RequestMapping("/")
public class ViewController {

    private final RestClient restClient;
    private UserDto userDto;

    @Autowired
    public ViewController(RestClient restClient) {
        this.restClient = restClient;
        userDto = new UserDto();
    }

    @GetMapping()
    public String start(Model model) {
        GoldenThought goldenThought = restClient.getGoldenThought();
        model.addAttribute("goldenThought", goldenThought);
        model.addAttribute("user", userDto);
        return "index";
    }

    @RequestMapping(value = "login", produces = "application/json",
            method = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST})
    public String login(UserDto userData, Model model) {
        if (userData.getUsername() == null && userData.getPassword() == null) {
            model.addAttribute("userData", userData);
            return "loginForm";
        } else {
            try {
                UserDto loggedUser = restClient.login(userData.getUsername(), userData.getPassword());
                if (loggedUser != null) {
                    userDto = loggedUser;
                }
            } catch (HttpClientErrorException httpClientErrorException){
                model.addAttribute("userData", userData);
                model.addAttribute("loginFalse",true);
                return "loginForm";
            }
        }
        return "redirect:/";
    }

    @GetMapping("logout")
    public String logout() {
        userDto = new UserDto();
        return "redirect:/";
    }

    @RequestMapping(value = "createOrUpdateTask", method = {RequestMethod.POST, RequestMethod.GET})
    public String createOrUpdateTask(Model model, DailyTask dailyTask) {
        if (userDto.getHeaderWithCredentials() == null) {
            return "redirect:/login";
        }
        if (dailyTask.getTask() == null && dailyTask.getSuccess() == null) {
            model.addAttribute("task", new DailyTask());
            return "addTaskForm";
        } else {
            Integer body = restClient.saveTask(userDto, dailyTask).getBody();
            if (body != null) {
                return "redirect:/tasks";
            }
        }
        return "redirect:/error";
    }

    @GetMapping(value = "editTask/{id}")
    public String editTask(Model model, @PathVariable int id) {
        DailyTask taskToEdit = restClient.findTaskById(userDto, id);
        if (userDto.getHeaderWithCredentials() == null) {
            return "redirect:/login";
        }
        if (taskToEdit != null) {
            model.addAttribute( "task", taskToEdit);
            return "editTaskForm";
        }
        return "redirect:/error";
    }


    @GetMapping(value = "deleteTask/{id}")
    public String deleteTask(@PathVariable int id) {
        if (userDto.getHeaderWithCredentials() == null) {
            return "redirect:/login";
        }
        restClient.deleteTask(userDto, id);
        return "redirect:/tasks";
    }

    @RequestMapping(value = "register", produces = "application/json",
            method = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST})
    public String register(UserDto userData, Model model) {
        if (userData.getUsername() == null && userData.getPassword() == null && userData.getMatchingPassword() == null) {
            model.addAttribute("userData", userData);
            return "registerForm";
        } else {
            HttpStatus responseStatus = restClient.register(userData);
            if (responseStatus.is2xxSuccessful()) {
                return "redirect:/tasks";
            } else {
                return "redirect:/error";
            }
        }
    }

    @GetMapping(value = "tasks")
    public String showTasks(Model model) {
        if (userDto.getHeaderWithCredentials() == null) {
            return "redirect:/login";
        } else {
            List<DailyTask> tasks = restClient.getAllTasksForUser(userDto);
            model.addAttribute("tasks", tasks);
        }
        return "tasksTemplate";
    }
}
