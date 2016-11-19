package com.neppro.competition.dao;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.neppro.competition.model.User;

@Repository
public interface UserDao extends CrudRepository<User,String> {
	
	@Query("SELECT u FROM User u WHERE u.msisdn = (:msisdn)")
	public User getUserByMsisdn(@Param("msisdn")String msisdn);
	
	
	@EntityGraph(value="privileges",type=EntityGraphType.FETCH)
	public User findOne(String username);
}
