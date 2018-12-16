package uk.co.cetinkaya.service;

import java.util.List;

import uk.co.cetinkaya.shared.dto.AddressDto;

public interface AddressService {

	List<AddressDto> getAddresses(String userId);

	AddressDto getAddress(String addressId);


}
