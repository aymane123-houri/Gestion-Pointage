package com.example.notificationservice.service;

import com.example.notificationservice.document.Notification;
import com.example.notificationservice.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public Notification createNotification(Notification notification){
        return repository.save(notification);
    }

    public Notification updateNotification(Notification notification){
        return repository.save(notification);
    }

    public void deleteNotification(String id){
        repository.deleteById(id);
    }

    public Notification getById(String id) {
        return repository.findById(id).orElse(null);
    }

    public List<Notification> getAll(){
        return repository.findAll();
    }

}
