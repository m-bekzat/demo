package com.example.demo.model;

import lombok.Data;

import java.util.List;

@Data
public class Response<T> {
    private Boolean success;
    private T obj;
    private List<T> list;
    private Integer totalCount;
    private String description;
}
