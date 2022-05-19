package moviesApp.entities;

import moviesApp.utils.enums.Color;
import moviesApp.utils.enums.Country;

import java.io.Serializable;

public class Person implements Serializable {
    private String name; //Поле не может быть null, Строка не может быть пустой
    private String passportID; //Значение этого поля должно быть уникальным, Длина строки должна быть не меньше 10, Длина строки не должна быть больше 49, Поле может быть null
    private Color eyeColor; //Поле не может быть null
    private Color hairColor; //Поле может быть null
    private Country nationality; //Поле может быть null

    public Person(String name, String passportID, Color eyeColor, Color hairColor, Country nationality) {
        setName(name);
        setPassportID(passportID);
        setEyeColor(eyeColor);
        setHairColor(hairColor);
        setNationality(nationality);
    }

    public String getName() {
        return name;
    }

    public String getPassportID() {
        return passportID;
    }

    public Color getEyeColor() {
        return eyeColor;
    }

    public Color getHairColor() {
        return hairColor;
    }

    public Country getNationality() {
        return nationality;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEyeColor(Color eyeColor) {
        this.eyeColor = eyeColor;
    }

    public void setHairColor(Color hairColor) {
        this.hairColor = hairColor;
    }

    public void setNationality(Country nationality) {
        this.nationality = nationality;
    }

    public void setPassportID(String passportID) {
        this.passportID = passportID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;

        Person person = (Person) o;

        if (name != null ? !name.equals(person.name) : person.name != null) return false;
        if (passportID != null ? !passportID.equals(person.passportID) : person.passportID != null) return false;
        if (eyeColor != person.eyeColor) return false;
        if (hairColor != person.hairColor) return false;
        return nationality == person.nationality;
    }

    @Override
    public int hashCode() {
        return passportID != null ? passportID.hashCode() : 0;
    }

    @Override
    public String toString() {
        return name + " " + passportID + " " + eyeColor + " " + hairColor + " " + nationality;
    }
}