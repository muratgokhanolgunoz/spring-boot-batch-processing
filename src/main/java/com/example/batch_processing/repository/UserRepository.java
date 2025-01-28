package com.example.batch_processing.repository;

import com.example.batch_processing.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
