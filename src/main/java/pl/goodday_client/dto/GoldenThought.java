package pl.goodday_client.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class GoldenThought {
    private String author;
    private String sentence;
}
