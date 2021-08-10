package com.xxx.provider;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "t_card")
public class Card {
    @Id
    private Integer id;
    private Double amount;
    @Column(name = "user_id")
    private Integer userId;
}
