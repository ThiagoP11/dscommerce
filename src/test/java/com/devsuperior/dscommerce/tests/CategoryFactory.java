package com.devsuperior.dscommerce.tests;

import com.devsuperior.dscommerce.entities.Category;

public class CategoryFactory {

    public static Category createCategory(){
        return new Category("Games", 1L);
    }

    public static Category createCategory(String name, Long id) {
        return new Category(name, id);
    }

}
