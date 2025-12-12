package com.smart.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Сущность Contact представляет собой модель одного контактного лица
 * в системе. Каждый контакт привязан к конкретному пользователю
 * (Many-to-One), имеет основную информацию: ФИО, email, телефон,
 * описание и изображение, а также дату создания.
 */
@Entity
@Table(name = "CONTACT")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int cId;  // Уникальный идентификатор контакта (PRIMARY KEY)

    private String name;        // Имя контакта
    private String secondName;  // Ник или фамилия
    private String work;        // Место работы или должность
    private String email;       // Email контакта
    private String phone;       // Телефон
    private String image;       // Имя файла изображения контакта

    @Column(length = 5000)
    private String description; // Дополнительная информация (биография, заметки)

    private LocalDateTime createdAt; // Автоматическая отметка времени создания

    @ManyToOne
    private User user;  // Контакт принадлежит одному пользователю

    // ---- Геттеры и сеттеры (стандартные методы доступа к полям) ----

    public int getcId() {
        return cId;
    }

    public void setcId(int cId) {
        this.cId = cId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}



