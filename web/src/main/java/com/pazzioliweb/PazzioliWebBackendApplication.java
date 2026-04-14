package com.pazzioliweb;

import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
@SpringBootApplication
@EnableScheduling
@EnableFeignClients(basePackages = "com.pazzioliweb.comprasmodule.client")
@EntityScan(basePackages = {
	    "com.pazzioliweb",
	  	})
@EnableJpaRepositories(basePackages = {
    "com.pazzioliweb",
   
})
@ComponentScan(basePackages = {"com.pazzioliweb"})

public class PazzioliWebBackendApplication {

	

public static void main(String[] args) {
	
		SpringApplication.run(PazzioliWebBackendApplication.class, args);
	

}


@Autowired
private EntityManager entityManager;

@PostConstruct
public void showEntities() {
    System.out.println("=== Entidades registradas en Hibernate ===");
    for (EntityType<?> entity : entityManager.getMetamodel().getEntities()) {
        System.out.println(entity.getName() + " -> " + entity.getJavaType().getName());
    }
    System.out.println("=========================================");
}
}

