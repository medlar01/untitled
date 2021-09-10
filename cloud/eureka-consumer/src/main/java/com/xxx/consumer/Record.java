package com.xxx.consumer;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "t_record")
public class Record implements Ask {
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
