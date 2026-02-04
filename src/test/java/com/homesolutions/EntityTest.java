package com.homesolutions;

import com.homesolutions.entity.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class EntityTest {

    @Test
    void testUserEntity() {
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_CUSTOMER");

        User user = User.builder()
                .id(1L)
                .phone("1234567890")
                .email("test@example.com")
                .fullName("Test User")
                .password("password")
                .roles(roles)
                .enabled(true)
                .build();

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getPhone()).isEqualTo("1234567890");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getFullName()).isEqualTo("Test User");
        assertThat(user.getPassword()).isEqualTo("password");
        assertThat(user.getRoles()).contains("ROLE_CUSTOMER");
        assertThat(user.getEnabled()).isTrue();
    }

    @Test
    void testCategoryEntity() {
        Category category = Category.builder()
                .id(1L)
                .name("Plumbing")
                .description("Plumbing services")
                .active(true)
                .build();

        assertThat(category.getId()).isEqualTo(1L);
        assertThat(category.getName()).isEqualTo("Plumbing");
        assertThat(category.getDescription()).isEqualTo("Plumbing services");
        assertThat(category.getActive()).isTrue();
    }

    @Test
    void testServiceEntity() {
        Category category = Category.builder().id(1L).name("Plumbing").build();

        Service service = Service.builder()
                .id(1L)
                .name("Pipe Repair")
                .description("Fix leaking pipes")
                .category(category)
                .basePrice(BigDecimal.valueOf(150.00))
                .extraHourlyRate(BigDecimal.valueOf(90.00))
                .active(true)
                .build();

        assertThat(service.getId()).isEqualTo(1L);
        assertThat(service.getName()).isEqualTo("Pipe Repair");
        assertThat(service.getDescription()).isEqualTo("Fix leaking pipes");
        assertThat(service.getCategory().getName()).isEqualTo("Plumbing");
        assertThat(service.getBasePrice()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
        assertThat(service.getExtraHourlyRate()).isEqualByComparingTo(BigDecimal.valueOf(90.00));
        assertThat(service.getActive()).isTrue();
    }

    @Test
    void testAddressEntity() {
        User user = User.builder().id(1L).phone("1234567890").build();

        Address address = Address.builder()
                .id(1L)
                .user(user)
                .street("123 Main St")
                .city("Test City")
                .state("TS")
                .zipCode("12345")
                .landmark("Near Park")
                .isDefault(true)
                .build();

        assertThat(address.getId()).isEqualTo(1L);
        assertThat(address.getUser().getId()).isEqualTo(1L);
        assertThat(address.getStreet()).isEqualTo("123 Main St");
        assertThat(address.getCity()).isEqualTo("Test City");
        assertThat(address.getState()).isEqualTo("TS");
        assertThat(address.getZipCode()).isEqualTo("12345");
        assertThat(address.getLandmark()).isEqualTo("Near Park");
        assertThat(address.getIsDefault()).isTrue();
    }

    @Test
    void testBookingEntity() {
        User customer = User.builder().id(1L).build();
        User expert = User.builder().id(2L).build();
        Category category = Category.builder().id(1L).build();
        Service service = Service.builder().id(1L).category(category).build();
        Address address = Address.builder().id(1L).build();

        Booking booking = Booking.builder()
                .id(1L)
                .customer(customer)
                .expert(expert)
                .service(service)
                .address(address)
                .scheduledAt(LocalDateTime.now())
                .durationMinutes(120)
                .totalPrice(BigDecimal.valueOf(150.00))
                .discount(BigDecimal.ZERO)
                .status(Booking.BookingStatus.CONFIRMED)
                .notes("Test booking")
                .build();

        assertThat(booking.getId()).isEqualTo(1L);
        assertThat(booking.getCustomer().getId()).isEqualTo(1L);
        assertThat(booking.getExpert().getId()).isEqualTo(2L);
        assertThat(booking.getService().getId()).isEqualTo(1L);
        assertThat(booking.getAddress().getId()).isEqualTo(1L);
        assertThat(booking.getDurationMinutes()).isEqualTo(120);
        assertThat(booking.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
        assertThat(booking.getStatus()).isEqualTo(Booking.BookingStatus.CONFIRMED);
        assertThat(booking.getNotes()).isEqualTo("Test booking");
    }

    @Test
    void testPaymentEntity() {
        Booking booking = Booking.builder().id(1L).build();

        Payment payment = Payment.builder()
                .id(1L)
                .booking(booking)
                .amount(BigDecimal.valueOf(150.00))
                .method(Payment.PaymentMethod.CARD)
                .status(Payment.PaymentStatus.SUCCEEDED)
                .transactionId("TXN123456")
                .build();

        assertThat(payment.getId()).isEqualTo(1L);
        assertThat(payment.getBooking().getId()).isEqualTo(1L);
        assertThat(payment.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
        assertThat(payment.getMethod()).isEqualTo(Payment.PaymentMethod.CARD);
        assertThat(payment.getStatus()).isEqualTo(Payment.PaymentStatus.SUCCEEDED);
        assertThat(payment.getTransactionId()).isEqualTo("TXN123456");
    }

    @Test
    void testRatingEntity() {
        User customer = User.builder().id(1L).build();
        User expert = User.builder().id(2L).build();
        Booking booking = Booking.builder().id(1L).build();

        Rating rating = Rating.builder()
                .id(1L)
                .customer(customer)
                .expert(expert)
                .booking(booking)
                .rating(5)
                .comment("Excellent service")
                .build();

        assertThat(rating.getId()).isEqualTo(1L);
        assertThat(rating.getCustomer().getId()).isEqualTo(1L);
        assertThat(rating.getExpert().getId()).isEqualTo(2L);
        assertThat(rating.getBooking().getId()).isEqualTo(1L);
        assertThat(rating.getRating()).isEqualTo(5);
        assertThat(rating.getComment()).isEqualTo("Excellent service");
    }

    @Test
    void testTicketEntity() {
        User user = User.builder().id(1L).build();
        Booking booking = Booking.builder().id(1L).build();

        Ticket ticket = Ticket.builder()
                .id(1L)
                .user(user)
                .booking(booking)
                .subject("Issue with booking")
                .description("Detailed description")
                .status(Ticket.TicketStatus.OPEN)
                .priority(Ticket.TicketPriority.HIGH)
                .build();

        assertThat(ticket.getId()).isEqualTo(1L);
        assertThat(ticket.getUser().getId()).isEqualTo(1L);
        assertThat(ticket.getBooking().getId()).isEqualTo(1L);
        assertThat(ticket.getSubject()).isEqualTo("Issue with booking");
        assertThat(ticket.getDescription()).isEqualTo("Detailed description");
        assertThat(ticket.getStatus()).isEqualTo(Ticket.TicketStatus.OPEN);
        assertThat(ticket.getPriority()).isEqualTo(Ticket.TicketPriority.HIGH);
    }

    @Test
    void testUserSetters() {
        User user = new User();
        user.setId(1L);
        user.setPhone("1234567890");
        user.setEmail("test@example.com");
        user.setFullName("Test User");
        user.setPassword("password");
        user.setEnabled(true);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getPhone()).isEqualTo("1234567890");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getFullName()).isEqualTo("Test User");
        assertThat(user.getPassword()).isEqualTo("password");
        assertThat(user.getEnabled()).isTrue();
    }

    @Test
    void testCategorySetters() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Plumbing");
        category.setDescription("Plumbing services");
        category.setActive(true);

        assertThat(category.getId()).isEqualTo(1L);
        assertThat(category.getName()).isEqualTo("Plumbing");
        assertThat(category.getDescription()).isEqualTo("Plumbing services");
        assertThat(category.getActive()).isTrue();
    }

    @Test
    void testAdminEntity() {
        Admin admin = Admin.builder()
                .id(1L)
                .fullName("Test Admin")
                .email("admin@example.com")
                .password("password")
                .enabled(true)
                .build();

        assertThat(admin.getId()).isEqualTo(1L);
        assertThat(admin.getFullName()).isEqualTo("Test Admin");
        assertThat(admin.getEmail()).isEqualTo("admin@example.com");
        assertThat(admin.getPassword()).isEqualTo("password");
        assertThat(admin.getEnabled()).isTrue();
    }

    @Test
    void testAdminSetters() {
        Admin admin = new Admin();
        admin.setId(1L);
        admin.setFullName("Test Admin");
        admin.setEmail("admin@example.com");
        admin.setPassword("password");
        admin.setEnabled(true);

        assertThat(admin.getId()).isEqualTo(1L);
        assertThat(admin.getFullName()).isEqualTo("Test Admin");
        assertThat(admin.getEmail()).isEqualTo("admin@example.com");
        assertThat(admin.getPassword()).isEqualTo("password");
        assertThat(admin.getEnabled()).isTrue();
    }

    @Test
    void testServiceSetters() {
        Category category = Category.builder().id(1L).build();
        Service service = new Service();
        service.setId(1L);
        service.setName("Pipe Repair");
        service.setDescription("Fix leaking pipes");
        service.setCategory(category);
        service.setBasePrice(BigDecimal.valueOf(150.00));
        service.setExtraHourlyRate(BigDecimal.valueOf(90.00));
        service.setActive(true);

        assertThat(service.getId()).isEqualTo(1L);
        assertThat(service.getName()).isEqualTo("Pipe Repair");
        assertThat(service.getDescription()).isEqualTo("Fix leaking pipes");
        assertThat(service.getCategory().getId()).isEqualTo(1L);
        assertThat(service.getBasePrice()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
        assertThat(service.getExtraHourlyRate()).isEqualByComparingTo(BigDecimal.valueOf(90.00));
        assertThat(service.getActive()).isTrue();
    }

    @Test
    void testAddressSetters() {
        User user = User.builder().id(1L).build();
        Address address = new Address();
        address.setId(1L);
        address.setUser(user);
        address.setStreet("123 Main St");
        address.setCity("Test City");
        address.setState("TS");
        address.setZipCode("12345");
        address.setLandmark("Near Park");
        address.setIsDefault(true);

        assertThat(address.getId()).isEqualTo(1L);
        assertThat(address.getUser().getId()).isEqualTo(1L);
        assertThat(address.getStreet()).isEqualTo("123 Main St");
        assertThat(address.getCity()).isEqualTo("Test City");
        assertThat(address.getState()).isEqualTo("TS");
        assertThat(address.getZipCode()).isEqualTo("12345");
        assertThat(address.getLandmark()).isEqualTo("Near Park");
        assertThat(address.getIsDefault()).isTrue();
    }

    @Test
    void testBookingSetters() {
        User customer = User.builder().id(1L).build();
        User expert = User.builder().id(2L).build();
        Category category = Category.builder().id(1L).build();
        Service service = Service.builder().id(1L).category(category).build();
        Address address = Address.builder().id(1L).build();

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setCustomer(customer);
        booking.setExpert(expert);
        booking.setService(service);
        booking.setAddress(address);
        booking.setScheduledAt(LocalDateTime.now());
        booking.setDurationMinutes(120);
        booking.setTotalPrice(BigDecimal.valueOf(150.00));
        booking.setDiscount(BigDecimal.ZERO);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setNotes("Test booking");

        assertThat(booking.getId()).isEqualTo(1L);
        assertThat(booking.getCustomer().getId()).isEqualTo(1L);
        assertThat(booking.getExpert().getId()).isEqualTo(2L);
        assertThat(booking.getService().getId()).isEqualTo(1L);
        assertThat(booking.getAddress().getId()).isEqualTo(1L);
        assertThat(booking.getDurationMinutes()).isEqualTo(120);
        assertThat(booking.getTotalPrice()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
        assertThat(booking.getStatus()).isEqualTo(Booking.BookingStatus.CONFIRMED);
        assertThat(booking.getNotes()).isEqualTo("Test booking");
    }

    @Test
    void testPaymentSetters() {
        Booking booking = Booking.builder().id(1L).build();
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setBooking(booking);
        payment.setAmount(BigDecimal.valueOf(150.00));
        payment.setMethod(Payment.PaymentMethod.CARD);
        payment.setStatus(Payment.PaymentStatus.SUCCEEDED);
        payment.setTransactionId("TXN123456");

        assertThat(payment.getId()).isEqualTo(1L);
        assertThat(payment.getBooking().getId()).isEqualTo(1L);
        assertThat(payment.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
        assertThat(payment.getMethod()).isEqualTo(Payment.PaymentMethod.CARD);
        assertThat(payment.getStatus()).isEqualTo(Payment.PaymentStatus.SUCCEEDED);
        assertThat(payment.getTransactionId()).isEqualTo("TXN123456");
    }

    @Test
    void testRatingSetters() {
        User customer = User.builder().id(1L).build();
        User expert = User.builder().id(2L).build();
        Booking booking = Booking.builder().id(1L).build();

        Rating rating = new Rating();
        rating.setId(1L);
        rating.setCustomer(customer);
        rating.setExpert(expert);
        rating.setBooking(booking);
        rating.setRating(5);
        rating.setComment("Excellent service");

        assertThat(rating.getId()).isEqualTo(1L);
        assertThat(rating.getCustomer().getId()).isEqualTo(1L);
        assertThat(rating.getExpert().getId()).isEqualTo(2L);
        assertThat(rating.getBooking().getId()).isEqualTo(1L);
        assertThat(rating.getRating()).isEqualTo(5);
        assertThat(rating.getComment()).isEqualTo("Excellent service");
    }

    @Test
    void testTicketSetters() {
        User user = User.builder().id(1L).build();
        Booking booking = Booking.builder().id(1L).build();

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setUser(user);
        ticket.setBooking(booking);
        ticket.setSubject("Issue with booking");
        ticket.setDescription("Detailed description");
        ticket.setStatus(Ticket.TicketStatus.OPEN);
        ticket.setPriority(Ticket.TicketPriority.HIGH);

        assertThat(ticket.getId()).isEqualTo(1L);
        assertThat(ticket.getUser().getId()).isEqualTo(1L);
        assertThat(ticket.getBooking().getId()).isEqualTo(1L);
        assertThat(ticket.getSubject()).isEqualTo("Issue with booking");
        assertThat(ticket.getDescription()).isEqualTo("Detailed description");
        assertThat(ticket.getStatus()).isEqualTo(Ticket.TicketStatus.OPEN);
        assertThat(ticket.getPriority()).isEqualTo(Ticket.TicketPriority.HIGH);
    }
}
