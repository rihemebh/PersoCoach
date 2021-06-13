package com.website.persocoach.controllers;

import com.website.persocoach.Models.Client;
import com.website.persocoach.Models.Coach;
import com.website.persocoach.Models.ProgramRequest;
import com.website.persocoach.Models.Review;
import com.website.persocoach.repositories.ClientRepository;
import com.website.persocoach.Models.*;
import com.website.persocoach.repositories.ClientRepository;
import com.website.persocoach.repositories.CoachRepository;
import com.website.persocoach.repositories.RequestRepository;
import com.website.persocoach.repositories.RequestRepositoriy;
import com.website.persocoach.repositories.ReviewRepository;
import com.website.persocoach.security.jwt.CoachService;
import com.website.persocoach.services.ClientService;
import com.website.persocoach.services.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@RestController
@RequestMapping("/catalog")
@CrossOrigin(origins = "http://localhost:3000")

public class CoachController {

    CoachService repository;
    @Autowired
    CoachRepository repo;

    @Autowired
    RequestRepositoriy Reqrepo;
    @Autowired
    ReviewRepository ReviewRepo;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    RequestRepository requestRepository;
    @Autowired
    ClientRepository clientRepo;


    Collection<Coach> coaches = new ArrayList<>();

    CoachController(CoachService repository) {
        super();
        this.repository = repository;

    }


    @RequestMapping(value = "/coaches", method = RequestMethod.GET)
    public Page<Coach> coaches(@RequestParam("key") Optional<String> key,
                               @RequestParam("type") Optional<String> type,
                               @RequestParam("rate") Optional<Integer> rate,
                               @RequestParam("gender") Optional<String> gender,
                               @RequestParam("sort") Optional<String> sort,
                               @RequestParam("direction") Optional<Integer> direction,
                               @RequestParam("page") Optional<Integer> page) {

        if (direction.orElse(0) == 1) {
            return repository.findByRateTypeGender(rate.orElse(5), type.orElse(""), gender.orElse(""),
                    key.orElse(""),
                    PageRequest.of(page.orElse(0), 9, Sort.by(sort.orElse("rate")).ascending()));
        } else {
            return repository.findByRateTypeGender(rate.orElse(5), type.orElse(""), gender.orElse(""),
                    key.orElse(""),
                    PageRequest.of(page.orElse(0), 9, Sort.by(sort.orElse("rate")).descending()));
        }

    }

    @RequestMapping(value ="/coach/{id}", method = RequestMethod.GET)
    public Optional<Coach> getCoach(@PathVariable String id) {

        return repo.findById(id);
    }
    @RequestMapping(value ="/coach/{id}/requests", method = RequestMethod.GET)
    public List<ProgramRequest> getAllRequests(@PathVariable String id){
        Coach c =repo.findById(id).orElse(null);
        return   Reqrepo.getAllByCoach(c);
    }

    @RequestMapping(value = "/coach/{id}", method = RequestMethod.PUT)
    public void saveRequest(@PathVariable  String id,
                            @RequestParam String gender,
                            @RequestParam String goal,
                            @RequestParam Integer age,
                            @RequestParam Double height,
                            @RequestParam Double weight,
                            @RequestParam String c,
                            @RequestParam String practice

    ) {

        Client client = clientRepo.findById(c).orElse(null);
        Coach coach = repo.findById(id).orElse(null);

        ProgramRequest prog =new ProgramRequest(coach,client,height,
                weight, practice, gender, age,goal,"pending");
        Reqrepo.save(prog);
    }

    @RequestMapping(value = "/coachesNb", method = RequestMethod.GET)
    public int getNbCoaches(@RequestParam("key") Optional<String> key,
                            @RequestParam("type") Optional<String> type,
                            @RequestParam("rate") Optional<Integer> rate,
                            @RequestParam("gender") Optional<String> gender) {

        return repository.getNbCoaches(key.orElse(""), rate.orElse(5), type.orElse(""),
                gender.orElse(""));
    }

    @RequestMapping(value = "/coach/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCoach(@PathVariable String  id) {
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }





    @RequestMapping(value = "/coach/update/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Coach> updateCoach(@RequestBody Coach c) {
        repository.saveCoach(c);
        return ResponseEntity.ok().body(c);
    }

    @RequestMapping(value = "/coach/add", method = RequestMethod.PUT)
    public ResponseEntity<Coach> addCoach(Coach c) throws URISyntaxException {

        repository.saveCoach(c);
        return ResponseEntity.created(new URI("/coach/add" + c.getId())).body(c);
    }


    /******* Reviews ********/


    @RequestMapping(value = "/coach/{id}/review", method = RequestMethod.PUT)
    public void saveReview(@PathVariable String  id,@RequestParam Optional<String> desc,@RequestParam int rate){
        Client client;
        Coach coach= repo.findById(id).orElse(null);
        List<Review> reviews = ReviewRepo.findAllByCoach(coach);
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        double r=rate;
        try{
            if (reviews.size() > 0) {
                for(int i=0; i< reviews.size() ;i++){
                    r+= reviews.get(i).getRate();


                }
                if (r  % 5 == 0){
                    coach.setRate(5);
                }else{
                    coach.setRate( (int) ((r/(reviews.size() +1)) % 5));
                }

                for(int i=0; i< reviews.size() ;i++){
                    reviews.get(i).getCoach().setRate(coach.getRate());
                    ReviewRepo.save(reviews.get(i));

                }
            }else{
                coach.setRate(rate);
            }
        }catch(Exception e){
            System.out.println(coach);
            System.out.println(e);
        }

        repository.saveCoach(coach);

        try {
            client =  clientRepo.findByUsername(((UserDetails)principal).getUsername());

        }catch(Exception e){
            System.out.println(e);
            client = new Client(principal.toString());
        }
        ReviewRepo.save(new Review(client,coach, desc.orElse(""),rate,new Date(System.currentTimeMillis())));
    }
    @RequestMapping(value = "/coach/{id}/review", method = RequestMethod.GET)
    public List<Review> getCoachesReview(@PathVariable String id){
        Optional<Coach> coach = repo.findById(id);

        return ReviewRepo.findAllByCoach(coach.orElse(null));
    }

    @RequestMapping(value = "/coach/review/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Review> updateReview(@RequestBody Review  review){
        //Review review = ReviewRepo.findById(id).orElse(null);
        Review r = ReviewRepo.save(review);
        return ResponseEntity.ok().body(r);
    }

    @RequestMapping(value = "/coach/review/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteReview(@PathVariable String  id) {
        ReviewRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

}