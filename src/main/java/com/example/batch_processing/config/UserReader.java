package com.example.batch_processing.config;

import com.example.batch_processing.entity.User;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class UserReader implements ItemReader<User> {

    @Override
    public User read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        return null;
    }
}
