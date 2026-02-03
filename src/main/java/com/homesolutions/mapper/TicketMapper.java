package com.homesolutions.mapper;

import com.homesolutions.dto.TicketRequest;
import com.homesolutions.dto.TicketResponse;
import com.homesolutions.entity.Booking;
import com.homesolutions.entity.Ticket;
import com.homesolutions.entity.User;

public class TicketMapper {

    private TicketMapper() {
    }

    public static TicketResponse toResponse(Ticket ticket) {
        if (ticket == null) {
            return null;
        }

        User user = ticket.getUser();
        Booking booking = ticket.getBooking();

        return TicketResponse.builder()
                .id(ticket.getId())
                .userId(user != null ? user.getId() : null)
                .userName(user != null ? user.getFullName() : null)
                .bookingId(booking != null ? booking.getId() : null)
                .subject(ticket.getSubject())
                .description(ticket.getDescription())
                .status(ticket.getStatus() != null ? ticket.getStatus().name() : null)
                .priority(ticket.getPriority() != null ? ticket.getPriority().name() : null)
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }

    public static Ticket toEntity(TicketRequest request, User user, Booking booking) {
        if (request == null) {
            return null;
        }

        Ticket.TicketPriority priority = Ticket.TicketPriority.MEDIUM;
        if (request.getPriority() != null) {
            try {
                priority = Ticket.TicketPriority.valueOf(request.getPriority().toUpperCase());
            } catch (IllegalArgumentException e) {
                priority = Ticket.TicketPriority.MEDIUM;
            }
        }

        return Ticket.builder()
                .user(user)
                .booking(booking)
                .subject(request.getSubject())
                .description(request.getDescription())
                .priority(priority)
                .build();
    }

    public static void updateEntityFromRequest(Ticket ticket, TicketRequest request) {
        if (ticket == null || request == null) {
            return;
        }

        if (request.getSubject() != null) {
            ticket.setSubject(request.getSubject());
        }
        if (request.getDescription() != null) {
            ticket.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            try {
                Ticket.TicketPriority priority = Ticket.TicketPriority.valueOf(request.getPriority().toUpperCase());
                ticket.setPriority(priority);
            } catch (IllegalArgumentException e) {
                // Keep existing priority if invalid
            }
        }
    }
}
