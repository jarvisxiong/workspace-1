package com.neppro.localbus.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.proxy.HibernateProxy;

public class GenericDaoImpl<T, PK extends Serializable> implements
		GenericDao<T, PK> {

	private SessionFactory sessionFactory;
	private Class<T> type;

	public GenericDaoImpl(SessionFactory sessionFactory, Class<T> type) {
		this.setSessionFactory(sessionFactory);
		this.type = type;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PK create(T persistentObject) {
		Session session = sessionFactory.getCurrentSession();
		Serializable serializable=session.save(persistentObject);
		return (PK)serializable;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(PK id) {
		T value = (T) sessionFactory.getCurrentSession().get(type, id);
		if (value == null) {
			return null;
		}
		if (value instanceof HibernateProxy) {
			Hibernate.initialize(value);
			value = (T) ((HibernateProxy) value).getHibernateLazyInitializer()
					.getImplementation();
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getAll() {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(type);
		return crit.list();

	}

	@Override
	public void update(T persistentObject) {
		sessionFactory.getCurrentSession().update(persistentObject);
	}

	/*@Override
	public void createOrUpdate(T persistentObject) {
		if (persistentObject instanceof AbstractPersistentObject) {
			if (((AbstractPersistentObject) persistentObject).isCreation()) {
				sessionFactory.getCurrentSession().saveOrUpdate(persistentObject);
			} else {
				sessionFactory.getCurrentSession().merge(persistentObject);
			}
		} else {
			throw new RuntimeException(
					"this method support only AbstractPersistentObject");
		}

	}*/

	@Override
	public void delete(T persistentObject) {
		sessionFactory.getCurrentSession().delete(persistentObject);

	}

}
