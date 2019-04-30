package com.krypton.snetwork.service.common;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Set;

@Service
public class Tools {
	
	/**
     * parses json from string to hash map
     * @param json		String with keys and values
     * @return parsed hashmap from string
     */
    public HashMap<String, String> stringToHashMap(String json) {
        // hash map parsed from string
        HashMap<String, String> parsedHashMap = new HashMap<>();
        // remove curly brackets
        json = json.substring(1, json.length()-1);
        // split string to create key/value pairs
        String[] keyValuePairs = json.split(",");
        // iterate pairs
        for (String pair : keyValuePairs) {
            // split pair in key and value
            String[] entry = pair.split(":");
            // put key and value to parsed hashmap
            parsedHashMap.put(
                    entry[0].replace('"',' ').trim(),
                    entry[1].replace('"',' ').trim()
            );
        }
        return parsedHashMap;
    }
    /**
     * get last element from set collection
     * @param elements 		set collection
     * @return last element
     */
    public <T> T getLastElement(Set<T> elements) {
        T lastElement = null;
        for (T element : elements) {
            lastElement = element;
        }
        return lastElement;
    }
}