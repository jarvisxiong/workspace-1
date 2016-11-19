package com.neppro.competition.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.neppro.competition.model.MobileClubSubscriber;
import com.neppro.competition.model.MobileClubSubscriberId;

@Repository
public interface MobileClubSubscriberDao extends CrudRepository<MobileClubSubscriber,MobileClubSubscriberId> {

}
