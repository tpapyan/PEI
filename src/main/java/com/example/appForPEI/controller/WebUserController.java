package com.example.appForPEI.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.appForPEI.entity.UserInfo;
import com.example.appForPEI.service.StorageService;
import com.example.appForPEI.service.UserInfoService;

@Controller
@RequestMapping("/")
public class WebUserController {

	@Autowired
	private StorageService storageService;

	@Autowired
	private UserInfoService userInfoService;

	@GetMapping({"/","/login"})
	public ModelAndView login() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("login");
		return modelAndView;
	}

	@RequestMapping("/secure/files")
	public String listUploadedFiles(Model model) {
		
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserInfo currentUser = userInfoService.getUserByName(userDetails.getUsername());

		model.addAttribute("email", currentUser.getEmail());
		model.addAttribute("datelastlogin", currentUser.getDatelastlogin());
		
		if(currentUser.getRole().equals("ROLE_ADMIN")) {
			model.addAttribute("files",
					storageService.loadAll().map(path -> path.getFileName().toString()).collect(Collectors.toList()));
		}else {
			model.addAttribute("id", currentUser.getId());		
			model.addAttribute("files",
					storageService.loadAllByUser(currentUser.getId()).map(path -> path.getFileName().toString().substring(2)).collect(Collectors.toList()));
		}
		currentUser.setDatelastlogin(LocalDateTime.now());
		userInfoService.updateUser(currentUser);
		return "list";
	}

	@GetMapping("/secure/user/add")
	public String userForm(Model model) {
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		UserInfo currentUser = userInfoService.getUserByName(userDetails.getUsername());

		model.addAttribute("email", currentUser.getEmail());
		model.addAttribute("datelastlogin", currentUser.getDatelastlogin());
		model.addAttribute("user", new UserInfo());
		return "adduser";
	}

	@PostMapping("/secure/user/add")
	public String addUser(@ModelAttribute("user") UserInfo userInfo) throws IOException {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		userInfo.setPassword(bCryptPasswordEncoder.encode(userInfo.getPassword()));
//		userInfo.setPassword(BCrypt.hashpw(userInfo.getPassword(),BCrypt.gensalt()));
		userInfoService.addUser(userInfo);
		return "redirect:/secure/files";
	}

	@GetMapping("/error")
	public ModelAndView error() {
		ModelAndView modelAndView = new ModelAndView();
		String errorMessage = "You are not authorized for the requested data.";
		modelAndView.addObject("errorMsg", errorMessage);
		modelAndView.setViewName("error");
		return modelAndView;
	}
}