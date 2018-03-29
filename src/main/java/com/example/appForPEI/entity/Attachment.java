package com.example.appForPEI.entity;

import javax.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "attachment")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private UserInfo user;
    
    private String fileNameClient;
    
    private String fileNameServer;

}
