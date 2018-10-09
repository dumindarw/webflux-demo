package com.springboot.webfluxdemo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Document(collection = "events")
@Data
public class CalenderEvent {

    @Id
    private String eventId;

    private String name;

    @NotBlank
    private String abbreviation;

    @NotNull
    private Date createdAt = new Date();

    public CalenderEvent(String id, String name, String abbreviation){
        this.eventId = id;
        this.name = name;
        this.abbreviation = abbreviation;
    }

    public CalenderEvent() {

    }
}
