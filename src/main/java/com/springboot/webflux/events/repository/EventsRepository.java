package com.springboot.webflux.events.repository;

import com.springboot.webflux.events.model.CalenderEvent;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventsRepository extends ReactiveMongoRepository<CalenderEvent, String> {

    Mono<CalenderEvent> findByEventId(String id);
    Flux<CalenderEvent> findByNameAndAbbreviation(String evtName, String evtAbbr);

    @Query("{'name': ?0, 'abbreviation': ?1, 'eventId': ?2}")
    //@Query("UPDATE CalenderEvent SET name = ?0, abbreviation = ?1 WHERE eventId = ?2")
    Mono<CalenderEvent> updateCalenderEvent(String name, String abbreviation, String eventId);


}
