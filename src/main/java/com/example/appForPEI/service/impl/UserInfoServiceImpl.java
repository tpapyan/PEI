package com.example.appForPEI.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.appForPEI.entity.UserInfo;
import com.example.appForPEI.repository.UserInfoRepository;
import com.example.appForPEI.service.UserInfoService;

@Service
public class UserInfoServiceImpl implements UserInfoService {

	@Autowired
	private UserInfoRepository userInfoRepository;

	@Override
	public List<UserInfo> listUsers() {
		// TODO Auto-generated method stub
		return userInfoRepository.findAll();
	}

	@Override
	public UserInfo getUserByName(String username) {
		// TODO Auto-generated method stub
		return userInfoRepository.findByUsername(username);
	}

	@Override
	public void addUser(UserInfo userInfo) throws IOException {
		userInfoRepository.save(userInfo);

	}

	@Override
	public void removeUser(Integer id) {
		userInfoRepository.delete(id);

	}

	@Override
	public void updateUser(UserInfo userInfo) {
		userInfoRepository.save(userInfo);

	}

}
