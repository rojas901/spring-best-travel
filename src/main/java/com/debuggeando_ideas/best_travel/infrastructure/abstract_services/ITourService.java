package com.debuggeando_ideas.best_travel.infrastructure.abstract_services;

import com.debuggeando_ideas.best_travel.api.models.request.TourRequest;
import com.debuggeando_ideas.best_travel.api.models.responses.TourResponse;

import java.util.UUID;

public interface ITourService extends SimpleCrudService<TourRequest, TourResponse, Long> {

    void remoteTicket(Long tourId, UUID ticketId);
    UUID addTicket(Long flyId, Long tourId);
    void remoteReservation(Long tourId, UUID reservationId);
    UUID addReservation(Long hotelId, Long tourId, Integer totalDays);
}
