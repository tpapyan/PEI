package com.example.appForPEI.service;

import java.io.IOException;
import java.util.List;

import com.example.appForPEI.entity.UserInfo;

public interface UserInfoService {
	
		List<UserInfo> listUsers();
		
		UserInfo getUserByName(String username);

	    void addUser(UserInfo userInfo) throws IOException;

	    void removeUser(Integer id);

	    void updateUser(UserInfo userInfo);

}
