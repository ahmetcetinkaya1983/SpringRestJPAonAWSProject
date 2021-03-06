package uk.co.cetinkaya.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import uk.co.cetinkaya.io.entity.UserEntity;
import uk.co.cetinkaya.io.repository.UserRepository;
import uk.co.cetinkaya.shared.Utils;
import uk.co.cetinkaya.shared.dto.AddressDto;
import uk.co.cetinkaya.shared.dto.UserDto;

class UserServiceImplTest {

	@InjectMocks
	UserServiceImpl userService;

	@Mock
	UserRepository userRepository;

	@Mock
	Utils utils;

	@Mock
	BCryptPasswordEncoder bCryptPasswordEncoder;

	String userId = "opşpişaspeuoecsapjed";
	String encryptedPassword = "fsduıosrgı349uısdf";

	UserEntity userEntity;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setFirstName("Ahmet");
		userEntity.setUserId(userId);
		userEntity.setEncryptedPassword(encryptedPassword);
	}

	@Test
	void testGetUser() {

		userEntity = new UserEntity();
		userEntity.setId(1L);
		userEntity.setFirstName("Ahmet");
		userEntity.setUserId(userId);
		userEntity.setEncryptedPassword(encryptedPassword);

		when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

		UserDto userDto = userService.getUser("test@test.com");
		assertNotNull(userDto);

		assertEquals("Ahmet", userDto.getFirstName());
	}

	@Test
	final void testGetUser_UsernameNotFoundException() {

		when(userRepository.findByEmail(anyString())).thenReturn(null);

		assertThrows(UsernameNotFoundException.class, () -> {
			userService.getUser("test@test.com");
		}

		);
	}

	@Test
	final void testCreateUser() {

		when(userRepository.findByEmail(anyString())).thenReturn(null);
		when(utils.generateAddressId(anyInt())).thenReturn("hjwefıhwefhoıw");
		when(utils.generateUserId(anyInt())).thenReturn(userId);
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
		when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

		AddressDto addressDto = new AddressDto();
		addressDto.setType("shipping");

		List<AddressDto> addresses = new ArrayList<>();
		addresses.add(addressDto);

		UserDto userDto = new UserDto();
		userDto.setAddresses(addresses);
		UserDto storedUserDetails = userService.createUser(userDto);

		assertNotNull(storedUserDetails);
		assertEquals(userEntity.getFirstName(), storedUserDetails.getFirstName());

	}

}
