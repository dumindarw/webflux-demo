package com.springboot.webflux.events.controller;

import com.springboot.webflux.events.model.CalenderEvent;
import com.springboot.webflux.events.repository.EventsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.validation.Valid;
import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/event")
public class EventsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventsController.class);

    @Autowired
    private EventsRepository eventsRepository;

    @GetMapping("/")
    public Flux<CalenderEvent> getAllEvents() {
        return eventsRepository.findAll();
    }

    @GetMapping("/byNameAndAbbreviation")
    public Flux<CalenderEvent> getEventsByNameAndAbbreviation(@RequestParam Map<String, String> queryMap) {
        return eventsRepository.findByNameAndAbbreviation(queryMap.get("name"),queryMap.get("abbreviation"));
    }

    @PostMapping("/")
    public Mono<ResponseEntity<CalenderEvent>> createCalenderEvents(@Valid @RequestBody CalenderEvent event) {

        LOGGER.info("1. event.getEventId() >> " + event.getEventId());

        return eventsRepository.findById(event.getEventId()).
                flatMap(existingEvent->
                     Mono.just(new ResponseEntity<>(new CalenderEvent(), HttpStatus.CONFLICT))
        ).switchIfEmpty(eventsRepository.insert(event).map(evt -> new ResponseEntity<>(evt, HttpStatus.OK)));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<CalenderEvent>> getEventsByEventId(@PathVariable String id) {

        Mono<CalenderEvent> result = eventsRepository.findByEventId(id);

        return result.map(event -> new ResponseEntity<>(event, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<CalenderEvent>> updateCalenderEvent(@PathVariable String id,
                                                                   @Valid @RequestBody CalenderEvent calenderEvent) {


        return eventsRepository.findByEventId(id).
                flatMap(existingEvent -> {
                    existingEvent.setName(calenderEvent.getName());
                    existingEvent.setAbbreviation(calenderEvent.getAbbreviation());
                    return eventsRepository.save(existingEvent);
                    //return eventsRepository.updateCalenderEvent(calenderEvent.getName(),calenderEvent.getAbbreviation(),id);
                }).
                map(updatedEvent -> new ResponseEntity<>(updatedEvent, HttpStatus.OK)).
                defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteCalenderEvent(@PathVariable(value = "id") String calenderEventId) {
        return eventsRepository.findById(calenderEventId).
                flatMap(existingCalenderEvent -> eventsRepository.delete(existingCalenderEvent).
                        then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK)))).
                defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<CalenderEvent> streamAllCalenderEvents() {
        Flux<Long> durationFlux = Flux.interval(Duration.ofSeconds(1));

        return Flux.zip(eventsRepository.findAll(), durationFlux).map(Tuple2::getT1);
        //return eventsRepository.findAll();
    }
}
