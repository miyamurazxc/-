package com.smart.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Сущность User описывает модель зарегистрированного пользователя системы.
 * Пользователь может создавать множество контактов (One-to-Many).
 * В классе хранится основная информация учётной записи:
 * имя, email, пароль, роль, статус и список контактов.
 */
@Entity
@Table(name = "USER")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;  // Уникальный идентификатор пользователя

    @NotBlank(message = "Имя обязательно для заполнения")
    @Size(min = 2, max = 20, message = "Имя должно содержать от 2 до 20 символов")
    private String name;  // Имя пользователя (отображается в системе)

    @Column(unique = true)
    private String email; // Email — используется как логин

    private String password; // Хэшированный пароль (BCrypt)

    private String role; // Роль пользователя: ROLE_USER или ROLE_ADMIN

    private boolean enabled; // Статус учётной записи (активна/нет)

    private String imageUrl; // Аватар пользователя (файл по умолчанию)

    @Column(length = 500)
    private String about; // Дополнительная информация о пользователе

    /**
     * One-to-Many:
     * Один пользователь может иметь много контактов.
     * mappedBy = "user" — связь определяется в классе Contact.
     * CascadeType.ALL — удаление пользователя удалит его контакты.
     * FetchType.LAZY — контакты загружаются только при необходимости.
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
    private List<Contact> contacts = new ArrayList<>();

    // Конструктор по умолчанию
    public User() {
        super();
    }

    // ----- Геттеры и сеттеры -----

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    // Удобный вывод информации (для логов)
    @Override
    public String toString() {
        return "User [id=" + id +
                ", name=" + name +
                ", email=" + email +
                ", password=" + password +
                ", role=" + role +
                ", enabled=" + enabled +
                ", imageUrl=" + imageUrl +
                ", about=" + about +
                ", contacts=" + contacts + "]";
    }
}

