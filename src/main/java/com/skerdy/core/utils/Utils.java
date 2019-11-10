package com.skerdy.core.utils;

import java.util.Arrays;
import java.util.HashMap;

public class Utils {

    public static HashMap<String, String> parseFilters(String input){
        HashMap<String, String> result = new HashMap<>();
        Arrays.stream(input.split("&")).forEach(x->{
            String[] split = x.split("=");
            if(split.length==2)
            result.put(split[0], split[1]);
        });
        return result;
    }
}
