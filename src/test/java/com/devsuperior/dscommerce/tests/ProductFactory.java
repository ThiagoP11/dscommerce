package com.devsuperior.dscommerce.tests;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.dto.ProductMinDTO;
import com.devsuperior.dscommerce.entities.Category;
import com.devsuperior.dscommerce.entities.Product;

public class ProductFactory {

    public static Product createProduct(){
        Category category = CategoryFactory.createCategory();
        Product product = new Product(1l, "Console Playstation 5", "Muito legal, pois funciona GTA 6",
                3999.0, "http://image.com");
        product.getCategories().add(category);

        return product;
    }

    public static Product createProduct(String name){
        Product product = createProduct();
        product.setName(name);
        return product;
    }

    public static ProductMinDTO createProductMinDTO(){
        Product product = createProduct();
        return new ProductMinDTO(product);
    }

    public static ProductDTO createProductDTO(){
        Category category = CategoryFactory.createCategory();
        Product product = createProduct();
        product.getCategories().add(category);
        return new ProductDTO(product);
    }

    public static ProductDTO createProductDTO(String name, String description, Double price){
        Product product = new Product(1L, name, description, price, "http://image.com");
        return new ProductDTO(product);
    }
    public static ProductDTO createProductDTOComCategoria(String name, String description, Double price){
        Category category = CategoryFactory.createCategory();
        Product product = new Product(1L, name, description, price, "http://image.com");
        product.getCategories().add(category);
        return new ProductDTO(product);
    }

}
