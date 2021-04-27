package com.website.persocoach.repositories;

import com.website.persocoach.Models.Coach;
import com.website.persocoach.Models.CoachFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CoachRepository extends MongoRepository<Coach,String>, PagingAndSortingRepository<Coach,String> {

@Query("{ 'name' : /?0/ }")
Page<Coach> findAllByName(String name, Pageable page);
@Query("{'rate' : { $lte : ?0 }  , 'gender': /^?2/ , 'type' : /?1/ }")
Page<Coach> findAllByRateTOrTypeOrGender(int rate, String type, String gender, Pageable page);
@Query("{'rate' : { $lte : ?0 }  , 'gender': /^?2/ , 'type' : /?1/ }")
List<Coach> findAllByRateTOrTypeOrGender(int rate, String type, String gender);
Page<Coach> findAllByType(String type,Pageable page);
Page<Coach> findAllByGender(String gender,Pageable page);
}
