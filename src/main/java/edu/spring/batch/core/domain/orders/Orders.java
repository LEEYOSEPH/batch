package edu.spring.batch.core.domain.orders;

import lombok.Getter;
import lombok.ToString;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@EnableBatchProcessing
@Getter
@ToString
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String orderItem;
    private Integer price;
    private Date orderDate;
}
