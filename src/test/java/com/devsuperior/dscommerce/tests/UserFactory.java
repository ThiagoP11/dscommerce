package com.devsuperior.dscommerce.tests;

import com.devsuperior.dscommerce.entities.Role;
import com.devsuperior.dscommerce.entities.User;

import java.time.LocalDate;

public class UserFactory {

    public static User createClientUser() {
        User user = new User(1L, "maria@gmail.com", "maria", "988888888",
                LocalDate.parse("2001-07-25"), "$2a$10$82ysEuGFE7KQKslH.J5sEusrJhlhjWN8nVV3g6qYGoXAb.l28DPgK");

        user.addRole(new Role(1L, "ROLE_CLIENT"));

        return user;
    }

    public static User createAdminUser() {
        User user = new User(2L, "alex@gmail.com", "alex", "977777777",
                LocalDate.parse("2001-07-25"), "$2a$10$82ysEuGFE7KQKslH.J5sEusrJhlhjWN8nVV3g6qYGoXAb.l28DPgK");

        user.addRole(new Role(2L, "ROLE_ADMIN"));

        return user;
    }

    public static User createCustomClientUser(Long id, String username) {
        User user = new User(id, username, "maria", "988888888",
                LocalDate.parse("2001-07-25"), "$2a$10$82ysEuGFE7KQKslH.J5sEusrJhlhjWN8nVV3g6qYGoXAb.l28DPgK");

        user.addRole(new Role(1L, "ROLE_CLIENT"));

        return user;
    }

    public static User createCustomAdminUser(Long id, String username) {
        User user = new User(id, username, "alex", "977777777",
                LocalDate.parse("2001-07-25"), "$2a$10$82ysEuGFE7KQKslH.J5sEusrJhlhjWN8nVV3g6qYGoXAb.l28DPgK");

        user.addRole(new Role(2L, "ROLE_ADMIN"));

        return user;
    }

}
