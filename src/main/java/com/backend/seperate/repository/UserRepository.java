package com.backend.seperate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.seperate.jpa.entity.user.User;
import com.backend.seperate.jpa.entity.user.UserPk;

@Repository
public interface UserRepository extends JpaRepository<User,UserPk>{
}
