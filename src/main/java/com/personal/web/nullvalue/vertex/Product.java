package com.personal.web.nullvalue.vertex;

import lombok.Data;

@Data
public class Product {
    private String id;
    private String description;
    private Integer price;
    private Quote quote;
}