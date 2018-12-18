package uk.co.cetinkaya.ui.controlller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;//used for url addressing relatively not hardcoded

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uk.co.cetinkaya.io.exception.UserServiceException;
import uk.co.cetinkaya.service.AddressService;
import uk.co.cetinkaya.service.UserService;
import uk.co.cetinkaya.shared.dto.AddressDto;
import uk.co.cetinkaya.shared.dto.UserDto;
import uk.co.cetinkaya.ui.model.request.UserDetailsRequestModel;
import uk.co.cetinkaya.ui.model.response.AddressesRest;
import uk.co.cetinkaya.ui.model.response.ErrorMessages;
import uk.co.cetinkaya.ui.model.response.OperationStatusModel;
import uk.co.cetinkaya.ui.model.response.RequestOperationName;
import uk.co.cetinkaya.ui.model.response.RequestOperationStatus;
import uk.co.cetinkaya.ui.model.response.UserRest;

@RestController
@RequestMapping("/users") // http://localhost:8080/users
public class UserController {

	@Autowired
	UserService userService;

	@Autowired
	AddressService addressesService;

	@GetMapping(path = "/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest getUser(@PathVariable String id) {

		UserRest returnValue = new UserRest();

		UserDto userDto = userService.getUserByUserId(id);
		BeanUtils.copyProperties(userDto, returnValue);

		return returnValue;
	}

	@PostMapping(consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {

		UserRest returnValue = new UserRest();
		if (userDetails.getFirstName().isEmpty())
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

		// UserDto userDto = new UserDto();
		// BeanUtils.copyProperties(userDetails, userDto);

		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);

		UserDto createdUser = userService.createUser(userDto);
		returnValue = modelMapper.map(createdUser, UserRest.class);

		return returnValue;
	}

	@PutMapping(path = "/{id}", consumes = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_JSON_VALUE })
	public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {

		UserRest returnValue = new UserRest();
		if (userDetails.getFirstName().isEmpty())
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);

		UserDto createdUser = userService.updateUser(id, userDto);
		BeanUtils.copyProperties(createdUser, returnValue);

		return returnValue;
	}

	@DeleteMapping(path = "/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })

	public OperationStatusModel deleteUser(@PathVariable String id) {

		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.DELETE.name());

		userService.deleteUser(id);

		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		return returnValue;
	}

	// get all users using pagination
	@GetMapping(produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "limit", defaultValue = "20") int limit) {
		List<UserRest> returnValue = new ArrayList<>();

		if (page > 0)
			page -= 1; // user enters 1-2-3 for pages and function returns p0, p1,p2

		List<UserDto> users = userService.getUsers(page, limit);
		for (UserDto userDto : users) {
			UserRest userModel = new UserRest();
			BeanUtils.copyProperties(userDto, userModel);
			returnValue.add(userModel);
		}

		return returnValue;
	}

	// http://localhost:8080/users/dsadhuedaeıuf/addresses
	@GetMapping(path = "/{id}/addresses", produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE, "application/hal+json" })
	public Resources<AddressesRest> getUserAddresses(@PathVariable String id) {

		List<AddressesRest> addressesListRestModel = new ArrayList<>();

		List<AddressDto> addressesDto = addressesService.getAddresses(id);

		if (addressesDto != null && !addressesDto.isEmpty()) {

			java.lang.reflect.Type listType = new TypeToken<List<AddressesRest>>() {
			}.getType();
			addressesListRestModel = new ModelMapper().map(addressesDto, listType);
			
			for(AddressesRest addressRest: addressesListRestModel) {
				Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(id, addressRest.getAddressId())).withSelfRel();
				addressRest.add(addressLink);
				
				Link userLink = linkTo(methodOn(UserController.class).getUser(id)).withRel("user");
				addressRest.add(userLink);
			}
		}

		return new Resources<>(addressesListRestModel);
	}

	// http://localhost:8080/users/dsadhuedaeıuf/addresses/feuıwfwuıfwefuwe with
	// hateoas selfLink, userLink and all addresses link added
	@GetMapping(path = "/{userId}/addresses/{addressId}", produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE, "application/hal+json" })
	public Resource<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {

		AddressDto addressesDto = addressesService.getAddress(addressId);

		ModelMapper modelMapper = new ModelMapper();
		Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(userId, addressId)).withSelfRel();
		Link userLink = linkTo(UserController.class).slash(userId).withRel("user");
		Link addresses = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");// all
																												// addresses

		AddressesRest addressesRestModel = modelMapper.map(addressesDto, AddressesRest.class);

		addressesRestModel.add(addressLink);
		addressesRestModel.add(userLink);
		addressesRestModel.add(addresses);

		return new Resource<>(addressesRestModel);

	}

}
