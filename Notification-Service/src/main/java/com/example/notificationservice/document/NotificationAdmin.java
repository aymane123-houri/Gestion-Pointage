package com.example.notificationservice.document;

import com.example.notificationservice.model.Anomalie;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notificationAdmin")
public class NotificationAdmin {
    private String sujet;
    private String message;
    private List<Anomalie> absences;


    public NotificationAdmin(Date date, List<Anomalie> absents) {
    }
}
