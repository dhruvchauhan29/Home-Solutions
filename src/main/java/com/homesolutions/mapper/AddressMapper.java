package com.homesolutions.mapper;

import com.homesolutions.dto.AddressRequest;
import com.homesolutions.dto.AddressResponse;
import com.homesolutions.entity.Address;
import com.homesolutions.entity.User;

public class AddressMapper {

    private AddressMapper() {
    }

    public static AddressResponse toResponse(Address address) {
        if (address == null) {
            return null;
        }

        return AddressResponse.builder()
                .id(address.getId())
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .zipCode(address.getZipCode())
                .landmark(address.getLandmark())
                .isDefault(address.getIsDefault())
                .createdAt(address.getCreatedAt())
                .build();
    }

    public static Address toEntity(AddressRequest request, User user) {
        if (request == null) {
            return null;
        }

        return Address.builder()
                .user(user)
                .street(request.getStreet())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .landmark(request.getLandmark())
                .isDefault(request.getIsDefault() != null ? request.getIsDefault() : false)
                .build();
    }

    public static void updateEntityFromRequest(Address address, AddressRequest request) {
        if (address == null || request == null) {
            return;
        }

        if (request.getStreet() != null) {
            address.setStreet(request.getStreet());
        }
        if (request.getCity() != null) {
            address.setCity(request.getCity());
        }
        if (request.getState() != null) {
            address.setState(request.getState());
        }
        if (request.getZipCode() != null) {
            address.setZipCode(request.getZipCode());
        }
        if (request.getLandmark() != null) {
            address.setLandmark(request.getLandmark());
        }
        if (request.getIsDefault() != null) {
            address.setIsDefault(request.getIsDefault());
        }
    }
}
