package com.homesolutions.mapper;

import com.homesolutions.dto.BookingRequest;
import com.homesolutions.dto.BookingResponse;
import com.homesolutions.entity.Address;
import com.homesolutions.entity.Booking;
import com.homesolutions.entity.Service;
import com.homesolutions.entity.User;

public class BookingMapper {

    private BookingMapper() {
    }

    public static BookingResponse toResponse(Booking booking) {
        if (booking == null) {
            return null;
        }

        User customer = booking.getCustomer();
        User expert = booking.getExpert();
        Service service = booking.getService();
        Address address = booking.getAddress();

        return BookingResponse.builder()
                .id(booking.getId())
                .customerId(customer != null ? customer.getId() : null)
                .customerName(customer != null ? customer.getFullName() : null)
                .service(ServiceMapper.toResponse(service))
                .address(AddressMapper.toResponse(address))
                .expertId(expert != null ? expert.getId() : null)
                .expertName(expert != null ? expert.getFullName() : null)
                .scheduledAt(booking.getScheduledAt())
                .durationMinutes(booking.getDurationMinutes())
                .totalPrice(booking.getTotalPrice())
                .couponCode(booking.getCouponCode())
                .discount(booking.getDiscount())
                .status(booking.getStatus() != null ? booking.getStatus().name() : null)
                .notes(booking.getNotes())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }

    public static Booking toEntity(BookingRequest request, User customer, Service service, Address address) {
        if (request == null) {
            return null;
        }

        return Booking.builder()
                .customer(customer)
                .service(service)
                .address(address)
                .scheduledAt(request.getScheduledAt())
                .durationMinutes(request.getDurationMinutes())
                .couponCode(request.getCouponCode())
                .notes(request.getNotes())
                .build();
    }

    public static void updateEntityFromRequest(Booking booking, BookingRequest request, 
                                               Service service, Address address) {
        if (booking == null || request == null) {
            return;
        }

        if (service != null) {
            booking.setService(service);
        }
        if (address != null) {
            booking.setAddress(address);
        }
        if (request.getScheduledAt() != null) {
            booking.setScheduledAt(request.getScheduledAt());
        }
        if (request.getDurationMinutes() != null) {
            booking.setDurationMinutes(request.getDurationMinutes());
        }
        if (request.getCouponCode() != null) {
            booking.setCouponCode(request.getCouponCode());
        }
        if (request.getNotes() != null) {
            booking.setNotes(request.getNotes());
        }
    }
}
