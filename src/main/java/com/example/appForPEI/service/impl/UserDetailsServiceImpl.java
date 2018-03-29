package com.example.appForPEI.service.impl;

import com.example.appForPEI.entity.UserInfo;
import com.example.appForPEI.repository.UserInfoRepository;

import java.util.Arrays;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {
	UserInfoRepository usersInfo;

	public UserDetailsServiceImpl(UserInfoRepository usersInfo) {
		this.usersInfo = usersInfo;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDetails loadedUser;

		try {
			UserInfo client = usersInfo.findByUsername(username);
			GrantedAuthority authority = new SimpleGrantedAuthority(client.getRole());
			loadedUser = new org.springframework.security.core.userdetails.User(client.getUsername(),
					client.getPassword(), Arrays.asList(authority));
		} catch (Exception repositoryProblem) {
			throw new InternalAuthenticationServiceException(repositoryProblem.getMessage(), repositoryProblem);
		}

		UserDetails userDetails = (UserDetails) loadedUser;
		return userDetails;
	}
}
