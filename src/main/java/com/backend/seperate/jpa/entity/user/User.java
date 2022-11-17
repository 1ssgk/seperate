package com.backend.seperate.jpa.entity.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.backend.seperate.configuration.BaseTimeEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor @Builder
@Entity
@Table(name ="user")
@IdClass(UserPk.class)
public class User extends BaseTimeEntity{

    @Id
    @Column(name="seq")
    private Long seq;

    @Id
    @Column(name="email")
    private String email;
    
}
