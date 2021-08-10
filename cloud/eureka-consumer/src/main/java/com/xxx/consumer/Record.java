package com.xxx.consumer;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "t_record")
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "`from`")
    private Integer from;
    @Column(name = "`to`")
    private Integer to;
    private Double amount;
    private String remark;
}
