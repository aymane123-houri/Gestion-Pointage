package com.example.notificationservice.document;

import com.example.notificationservice.model.Anomalie;
import com.example.notificationservice.model.Horaire;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notification")
public class Notification {
    private String _id;
    private Date date;
    private Anomalie anomalie;

    private Horaire horaire;

    public Notification(Date date, Anomalie anomalie,Horaire horaire) {
        this.date = date;
        this.anomalie = anomalie;
        this.horaire = horaire;
    }
}
