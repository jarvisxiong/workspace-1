package com.neppro.competition;

import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages= {"com.neppro.competition.dao"})
@EntityScan(basePackages = {"com.neppro.competition.model"})
public class DAOConfig {

}
