package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

public class JsonService
{
    public static Map<String, Object> loads(String content) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> recordMap;
        recordMap = mapper.readValue(
                content,
                new TypeReference<Map<String, Object>>()
                {
                }
        );
        return recordMap;
    }

    public static String dumps(Map<String, Object> map) throws JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(map);
    }
}
