package com.example.batch_processing.config;

import com.example.batch_processing.entity.User;
import lombok.NonNull;
import org.springframework.batch.item.ItemProcessor;

public class UserProcessor implements ItemProcessor<User, User> {

    @Override
    public User process(@NonNull User user) {
        System.out.println(user.toString());
        return user;
    }
}
