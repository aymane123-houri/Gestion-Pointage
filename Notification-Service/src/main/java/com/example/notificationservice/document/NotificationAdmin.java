package com.example.notificationservice.document;

import com.example.notificationservice.model.AbsenceInfo;
import com.example.notificationservice.model.Anomalie;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notificationAdmin")
public class NotificationAdmin {
    private String sujet;
    private String message;
    private List<AbsenceInfo> absences;


    public NotificationAdmin(Date date, List<AbsenceInfo> absences) {
        this.sujet = "Rapport des absences du " + date;
        this.absences = absences;

        System.out.println("✅ NotificationAdmin créée avec " + absences.size() + " absences.");
    }
}
