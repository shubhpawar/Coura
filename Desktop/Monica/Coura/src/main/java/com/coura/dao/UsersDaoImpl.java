package com.coura.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.coura.model.Users;

@Repository
@Transactional
public class UsersDaoImpl implements UsersDao {
	
	private static final Logger logger = LoggerFactory.getLogger(UsersDaoImpl.class);

	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sf){
		this.sessionFactory = sf;
	}
	
	public Users findUserByEmailId(String emailId) {
		Session session = this.sessionFactory.getCurrentSession();
		Users user = (Users) session.get(Users.class, emailId);
		return user;
	}
	
	public boolean isExistingEmailId(String emailId) {
		Users u = findUserByEmailId(emailId);
		if (u != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public void saveUsers(Users user) {
		Session session = this.sessionFactory.getCurrentSession();
		session.persist(user);
	}
	
	@SuppressWarnings("unchecked")
	public List<Users> listAllUsers() {
		Session session = this.sessionFactory.getCurrentSession();
		
		// To retrieve specific columns using hibernate query
		Criteria criteria = session.createCriteria(Users.class);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("firstName"), "firstName")
				.add(Projections.property("lastName"), "lastName")
				.add(Projections.property("emailId"), "emailId"))
		.setResultTransformer(Transformers.aliasToBean(Users.class));
		List<Users> usersList = criteria.list();
		for (int i = 0; i < usersList.size(); i++) {
			if (usersList.get(i).getEmailId().equalsIgnoreCase("admin@uncc.edu")) {
				usersList.remove(i);
			}
		}
		return usersList;
	}
	
	public void deleteUser(String emailId) {
		System.out.println(emailId);
		Session session = this.sessionFactory.getCurrentSession();
		Query query = session.createQuery("delete Users where emailId like :emailID");
		query.setParameter("emailID", emailId+"%");
		query.executeUpdate();
	}
}