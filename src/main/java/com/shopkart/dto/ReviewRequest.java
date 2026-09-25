package com.shopkart.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private String comment;
    private Integer rating;
}
