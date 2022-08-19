package com.weatherService.weatherService.domian;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@DiscriminatorValue("address")
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "pun_code", unique = true)
    private String punCode;

    @Column(name = "zip_code")
    private int zipCode;
    @Column(name = "read_name")
    private String roadName;

    @Column(name = "x")
    private double x;

    @Column(name = "y")
    private double y;

    public Address(String punCode, int zipCode, String roadName, double x, double y){
        this.punCode = punCode;
        this.zipCode = zipCode;
        this.roadName = roadName;
        this.x = x;
        this.y = y;
    }

}
