package com.neppro.competition.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.neppro.competition.model.Domain;
import com.neppro.competition.model.User;

@Repository
public interface DomainDao extends CrudRepository<User,String>{
	
	@Query("SELECT d FROM Domain d WHERE d.defaultUrl = :defaultUrl")
	public Domain getDomainByDefaultUrl(@Param("defaultUrl")String defaultUrl);

}
