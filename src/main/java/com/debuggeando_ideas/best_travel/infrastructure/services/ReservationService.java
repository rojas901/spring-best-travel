package com.debuggeando_ideas.best_travel.infrastructure.services;

import com.debuggeando_ideas.best_travel.api.models.request.ReservationRequest;
import com.debuggeando_ideas.best_travel.api.models.responses.HotelResponse;
import com.debuggeando_ideas.best_travel.api.models.responses.ReservationResponse;
import com.debuggeando_ideas.best_travel.domain.entities.ReservationEntity;
import com.debuggeando_ideas.best_travel.domain.repositories.CustomerRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.HotelRepository;
import com.debuggeando_ideas.best_travel.domain.repositories.ReservationRepository;
import com.debuggeando_ideas.best_travel.infrastructure.abstract_services.IReservationService;
import com.debuggeando_ideas.best_travel.infrastructure.helpers.BlackListHelper;
import com.debuggeando_ideas.best_travel.infrastructure.helpers.CustomerHelper;
import com.debuggeando_ideas.best_travel.util.enums.Tables;
import com.debuggeando_ideas.best_travel.util.exceptions.IdNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Transactional
@Service
@Slf4j
@AllArgsConstructor
public class ReservationService implements IReservationService {

    private final HotelRepository hotelRepository;
    private final CustomerRepository customerRepository;
    private final ReservationRepository reservationRepository;
    private final CustomerHelper customerHelper;
    private final BlackListHelper blackListHelper;

    @Override
    public ReservationResponse create(ReservationRequest request) {
        blackListHelper.isInBlackListCustomer(request.getIdClient());
        var hotel = this.hotelRepository.findById(request.getIdHotel())
                .orElseThrow(() -> new IdNotFoundException(Tables.hotel.name()));
        var customer = this.customerRepository.findById(request.getIdClient())
                .orElseThrow(() -> new IdNotFoundException(Tables.customer.name()));

        var reservationToPersist = ReservationEntity.builder()
                .id(UUID.randomUUID())
                .hotel(hotel)
                .customer(customer)
                .totalDays(request.getTotalDays())
                .price(hotel.getPrice().add(hotel.getPrice().multiply(charges_price_percentage)))
                .dateTimeReservation(LocalDateTime.now())
                .dateStart(LocalDate.now())
                .dateEnd(LocalDate.now().plusDays(request.getTotalDays()))
                .build();

        var reservationPersisted = this.reservationRepository.save(reservationToPersist);

        this.customerHelper.increase(customer.getDni(), ReservationService.class);

        log.info("Reservation saved with id: {}", reservationPersisted.getId());

        return this.entityToResponse(reservationPersisted);
    }

    @Override
    public ReservationResponse read(UUID uuid) {
        var reservationFromDB = this.reservationRepository.findById(uuid)
                .orElseThrow(() -> new IdNotFoundException(Tables.reservation.name()));
        return entityToResponse(reservationFromDB);
    }

    @Override
    public ReservationResponse update(ReservationRequest request, UUID uuid) {
        var reservationToUpdate = this.reservationRepository.findById(uuid)
                .orElseThrow(() -> new IdNotFoundException(Tables.reservation.name()));
        var hotel = this.hotelRepository.findById(request.getIdHotel())
                .orElseThrow(() -> new IdNotFoundException(Tables.hotel.name()));

        reservationToUpdate.setHotel(hotel);
        reservationToUpdate.setPrice(hotel.getPrice().add(hotel.getPrice().multiply(charges_price_percentage)));
        reservationToUpdate.setTotalDays(request.getTotalDays());
        reservationToUpdate.setDateTimeReservation(LocalDateTime.now());
        reservationToUpdate.setDateStart(LocalDate.now());
        reservationToUpdate.setDateEnd(LocalDate.now().plusDays(request.getTotalDays()));

        var reservationUpdated = this.reservationRepository.save(reservationToUpdate);

        log.info("Reservation updated with id: {}", reservationUpdated.getId());

        return this.entityToResponse(reservationUpdated);
    }

    @Override
    public void delete(UUID uuid) {
        var reservationToDelete = this.reservationRepository.findById(uuid)
                .orElseThrow(() -> new IdNotFoundException(Tables.reservation.name()));
        this.reservationRepository.delete(reservationToDelete);
    }

    @Override
    public BigDecimal findPrice(Long hotelId) {
        var hotel = this.hotelRepository.findById(hotelId)
                .orElseThrow(() -> new IdNotFoundException(Tables.hotel.name()));
        return hotel.getPrice().add(hotel.getPrice().multiply(charges_price_percentage));
    }

    private ReservationResponse entityToResponse(ReservationEntity entity) {
        var response = new ReservationResponse();
        BeanUtils.copyProperties(entity, response);
        var hotelResponse = new HotelResponse();
        BeanUtils.copyProperties(entity.getHotel(), hotelResponse);
        response.setHotel(hotelResponse);
        return response;
    }

    public static final BigDecimal charges_price_percentage = BigDecimal.valueOf(0.20);

}
