package com.website.persocoach;

import com.github.javafaker.Faker;
import com.website.persocoach.Models.Coach;
import com.website.persocoach.repositories.CoachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Random;

@SpringBootApplication
public class PersocoachApplication {
    @Autowired
    CoachRepository repo;

    public static void main(String[] args) {
        SpringApplication.run(PersocoachApplication.class, args);
    }

   @Bean
    public CommandLineRunner init(CoachRepository repo) {

        return args -> {
            Random rand = new Random();
            int randomNum = (int) (Math.random() * (2));
            String[] genders = new String[2];
            genders[0] = "Women";
            genders[1] = "Men";
            String[] types = new String[2];
            types[0] = "Sport";
            types[1] = "Nutrition";
            Faker faker = new Faker();
            ArrayList<String> acadamicExp = new ArrayList<>();
            ArrayList<String> workExp = new ArrayList<>();
            ArrayList<String> reviews = new ArrayList<>();

                Coach c = new Coach();
                c.setName(faker.name().fullName());
                c.setType(types[randomNum]);
                //c.setId(faker.idNumber().toString());
                c.setGender(genders[randomNum]);
                c.setUrl(faker.internet().image());
                c.setDescription(faker.lorem().paragraph());
                acadamicExp.add(faker.lorem().sentence());
                acadamicExp.add(faker.lorem().sentence());
                workExp.add(faker.lorem().sentence());
                workExp.add(faker.lorem().sentence());
                c.setRate(3);
                reviews.add(faker.lorem().sentence());
                c.setWorkExp(workExp);
                c.setAcadamicExp(acadamicExp);

                repo.save(c);
        };
    }
}
