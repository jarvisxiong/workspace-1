package com.neppro.competition.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.neppro.competition.model.MobileClub;

@Repository
public interface MobileClubDao extends CrudRepository<MobileClub,String> {

}
