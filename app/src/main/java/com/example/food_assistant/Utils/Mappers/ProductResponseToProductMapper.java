package com.example.food_assistant.Utils.Mappers;

import com.example.food_assistant.Models.Product;
import com.example.food_assistant.Responses.ProductResponse;

public class ProductResponseToProductMapper {
    public static Product map(ProductResponse productResponse) {
        return new Product();
    }
}
