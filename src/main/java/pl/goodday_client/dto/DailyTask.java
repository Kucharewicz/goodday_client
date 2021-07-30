package pl.goodday_client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "task",
        "success",
        "date"
})

@Data
@NoArgsConstructor
public class DailyTask {
    private int id;
    private String task;
    private String success;
    private String date;
}