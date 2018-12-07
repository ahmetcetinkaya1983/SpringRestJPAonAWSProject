package uk.co.cetinkaya.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import uk.co.cetinkaya.shared.dto.UserDto;

public interface UserService extends UserDetailsService{

	UserDto createUser(UserDto user);
	UserDto getUser(String email);
	UserDto getUserByUserId(String userId);
	UserDto updateUser(String id, UserDto userDto);
	void deleteUser(String userId);
	List<UserDto> getUsers(int page, int limit);
	
}