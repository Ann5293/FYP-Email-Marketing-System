package com.ms.email.marketing.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CommonUtil {

    private final String NEXT_LINE = "\n";
    @Value("${common.exception.isStrict:false}")
    private boolean isStrict;
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .enable(SerializationFeature.INDENT_OUTPUT);


    public String filterNullString(String input) {
        return StringUtils.isEmpty(input) ? "" : input;
    }

    public String printPrettyJson(Object data) throws JsonProcessingException {
        return NEXT_LINE + mapper.writeValueAsString(data);
    }
    public String printString(Object data) {
        try{
            return new ObjectMapper().writeValueAsString(data);
        }catch (Exception e){
            e.printStackTrace();
        }
        return (String) data;
    }
    public String printPrettyJsonElseNull(Object data){
        try{
            return NEXT_LINE + mapper.writeValueAsString(data);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }
        return data == null ? null : (String) data;
    }
//    public Object jsonToObject (String jsonData, Class obj) throws JsonProcessingException {
//        return new ObjectMapper().readValue(jsonData, obj.getClass());
//    }

    public <T> Object jsonStringToObjectElseNull(String jsonData, Class<T> obj){
        try {
            ObjectMapper mapper1 = new ObjectMapper();
            mapper1.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper1.readValue(jsonData, obj);
            //return mapper.readValue(jsonData, obj);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
