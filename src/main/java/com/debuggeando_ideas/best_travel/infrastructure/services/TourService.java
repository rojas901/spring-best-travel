package com.debuggeando_ideas.best_travel.infrastructure.services;

import com.debuggeando_ideas.best_travel.api.models.request.TourRequest;
import com.debuggeando_ideas.best_travel.api.models.responses.TourResponse;
import com.debuggeando_ideas.best_travel.domain.entities.*;
import com.debuggeando_ideas.best_travel.domain.repositories.CustomerRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.FlyRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.HotelRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.TourRepository;
import com.debuggeando_ideas.best_travel.infrastructure.abstract_services.ITourService;
import com.debuggeando_ideas.best_travel.infrastructure.helpers.CustomerHelper;
import com.debuggeando_ideas.best_travel.infrastructure.helpers.TourHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
@Service
@AllArgsConstructor
public class TourService implements ITourService {

    private final TourRepository tourRepository;
    private final FlyRepository flyRepository;
    private final HotelRepository hotelRepository;
    private final CustomerRepository customerRepository;
    private final TourHelper tourHelper;
    private final CustomerHelper customerHelper;

    @Override
    public TourResponse create(TourRequest request) {
        var customer = customerRepository.findById(request.getCustomerId()).orElseThrow();
        var flights = new HashSet<FlyEntity>();
        request.getFlights().forEach(fly -> flights.add(this.flyRepository.findById(fly.getId()).orElseThrow()));
        var hotels = new HashMap<HotelEntity, Integer>();
        request.getHotels().forEach(hotel -> hotels.put(this.hotelRepository.findById(hotel.getId()).orElseThrow(), hotel.getTotalDays()));

        var tourToSave = TourEntity.builder()
                .tickets(this.tourHelper.createTickets(flights, customer))
                .reservations(this.tourHelper.createReservations(hotels, customer))
                .customer(customer)
                .build();

        var tourSaved = this.tourRepository.save(tourToSave);

        this.customerHelper.increase(customer.getDni(), TourService.class);
        return TourResponse.builder()
                .reservationsIds(tourSaved.getReservations().stream().map(ReservationEntity::getId).collect(Collectors.toSet()))
                .ticketsIds(tourSaved.getTickets().stream().map(TicketEntity::getId).collect(Collectors.toSet()))
                .id(tourSaved.getId())
                .build();
    }

    @Override
    public TourResponse read(Long id) {
        var tourFromDB = this.tourRepository.findById(id).orElseThrow();
        return TourResponse.builder()
                .reservationsIds(tourFromDB.getReservations().stream().map(ReservationEntity::getId).collect(Collectors.toSet()))
                .ticketsIds(tourFromDB.getTickets().stream().map(TicketEntity::getId).collect(Collectors.toSet()))
                .id(tourFromDB.getId())
                .build();
    }

    @Override
    public void delete(Long id) {
        var tourToDelete = this.tourRepository.findById(id).orElseThrow();
        this.tourRepository.delete(tourToDelete);
    }

    @Override
    public void remoteTicket(Long tourId, UUID ticketId) {
        var tourUpdate = this.tourRepository.findById(tourId).orElseThrow();
        tourUpdate.removeTicket(ticketId);
        this.tourRepository.save(tourUpdate);
    }

    @Override
    public UUID addTicket(Long flyId, Long tourId) {
        var tourUpdate = this.tourRepository.findById(tourId).orElseThrow();
        var fly = this.flyRepository.findById(flyId).orElseThrow();
        var ticket = this.tourHelper.createTicket(fly, tourUpdate.getCustomer());
        tourUpdate.addTicket(ticket);
        this.tourRepository.save(tourUpdate);
        return ticket.getId();
    }

    @Override
    public void remoteReservation(Long tourId, UUID reservationId) {
        var tourUpdate = this.tourRepository.findById(tourId).orElseThrow();
        tourUpdate.removeReservation(reservationId);
        this.tourRepository.save(tourUpdate);
    }

    @Override
    public UUID addReservation(Long hotelId, Long tourId, Integer totalDays) {
        var tourUpdate = this.tourRepository.findById(tourId).orElseThrow();
        var hotel = this.hotelRepository.findById(hotelId).orElseThrow();
        var reservation = this.tourHelper.createReservation(hotel, tourUpdate.getCustomer(), totalDays);
        tourUpdate.addReservation(reservation);
        this.tourRepository.save(tourUpdate);
        return reservation.getId();
    }
}