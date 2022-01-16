package com.otakuhuang.springmongoredisdemo.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.joda.money.Money;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * @author otaku
 * @version 1.0
 * @date 2022/1/16 22:45
 * @description description
 */
@Document
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class Coffee extends BaseEntity implements Serializable {
    private String name;
    private Money price;
}
