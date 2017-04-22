package com.coura.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.coura.model.CourseInstructorWrapper;
import com.coura.model.Instructor;

@Transactional
@Repository
public class InstructorDaoImpl implements InstructorDao {
	
	private static final Logger logger = LoggerFactory.getLogger(InstructorDaoImpl.class);

	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sf) {
		this.sessionFactory = sf;
	}

	public boolean addInstructor(CourseInstructorWrapper wrapper) {
		Session session = this.sessionFactory.getCurrentSession();
		session.persist(wrapper.getInstructor());
		session.flush();
		session.clear();
		session.persist(wrapper.getCourseInstructor());
		session.flush();
		session.clear();
		return true;
	}
	
	public void deleteInstructor(Integer instructorId) {
		Session session = this.sessionFactory.getCurrentSession();
				
		// Delete from courseinstructor table
		Query query3 = session.createQuery("delete from CourseInstructor where instructorId = :instructorId");
		query3.setParameter("instructorId", instructorId);
		query3.executeUpdate();
		session.flush();
		session.clear();
			
		// Delete from course table
		Query query4 = session.createQuery("delete from Instructor where id = :instructorId");
		query4.setParameter("instructorId", instructorId);
		query4.executeUpdate();
		session.flush();
		session.clear();
	}
	
	@SuppressWarnings("unchecked")
	public List<Instructor> listAllInstructors() {
		Session session = this.sessionFactory.getCurrentSession();
		Query query = session.createQuery("select i.id as id, i.firstName as firstName, i.lastName as lastName, i.researchInterest as" + 
				" researchInterest, i.emailId as emailId from Instructor i").setResultTransformer(Transformers.aliasToBean(Instructor.class));
		List<Instructor> list = query.list();
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<Instructor> getInstructorsForCourse(Integer courseId) {
		Session session = this.sessionFactory.getCurrentSession();
		Query query = session.createQuery("select i.firstName as firstName, i.lastName as lastName, i.id as id from Instructor i, CourseInstructor ci" + 
				" where i.id = ci.instructorId and ci.courseId = :courseId").setResultTransformer(Transformers.aliasToBean(Instructor.class));
		query.setParameter("courseId", courseId);
		List<Instructor> instructors = query.list();
		return instructors;
	}
}
