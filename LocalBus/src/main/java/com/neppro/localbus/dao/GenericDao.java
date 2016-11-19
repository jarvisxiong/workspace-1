package com.neppro.localbus.dao;

import java.io.Serializable;
import java.util.List;

public interface GenericDao<T, PK extends Serializable> {
	PK create(T persistentObject);
	T get(PK id);
	List<T> getAll();
	void update(T persistentObject);
//	void createOrUpdate(T persistentObject);
	void delete(T persistentObject);
}
