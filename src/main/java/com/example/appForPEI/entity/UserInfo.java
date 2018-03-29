package com.example.appForPEI.entity;

import javax.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "user")
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String email;

    private String username;
    
    private String password;
    
    private String role;
    
    private LocalDateTime datelastlogin;

    @OneToMany(mappedBy = "user")
    private List<Attachment> attachments;

}
