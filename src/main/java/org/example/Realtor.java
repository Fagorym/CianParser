package org.example;

import java.util.ArrayList;
import java.util.List;

public class Realtor {
    private final String id;
    private String name;
    private String surname;
    private String registrationDate;
    private String workingExperience;
    private final List<String> contacts;
    private String phoneNumber;

    private String objectCount;

    public Realtor(String id) {
        this.id = id;
        this.contacts = new ArrayList<>();
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }


    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setWorkingExperience(String workingExperience) {
        this.workingExperience = workingExperience;
    }

    public void setObjectCount(String objectCount) {
        this.objectCount = objectCount;
    }

    public void addContact(String contact){
        this.contacts.add(contact);
    }

    @Override
    public String toString() {
        return "Имя: " + name + "\n" +
                "Фамилия: " + surname + "\n" +
                "Дата регистрации: " + registrationDate + "\n" +
                "Опыт работы: " + workingExperience + "\n" +
                "Объекты в работе: " + objectCount + "\n" +
                "Контакты: " + contacts.toString() + "\n" +
                "Телефон: " + phoneNumber + "\n";
    }
}
