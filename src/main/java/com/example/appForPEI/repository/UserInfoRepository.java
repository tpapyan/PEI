package com.example.appForPEI.repository;

import com.example.appForPEI.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {
	UserInfo findByUsername(String username);

}
