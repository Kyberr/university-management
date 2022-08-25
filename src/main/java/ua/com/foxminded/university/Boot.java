package ua.com.foxminded.university;


import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ua.com.foxminded.university.dao.DaoConfig;
import ua.com.foxminded.university.dao.jdbc.JdbcCourseDao;
import ua.com.foxminded.university.dao.jdbc.JdbcGroupDao;
import ua.com.foxminded.university.dao.jdbc.JdbcStudentDao;
import ua.com.foxminded.university.dao.jdbc.JdbcTimetableDao;
import ua.com.foxminded.university.entity.CourseEntity;
import ua.com.foxminded.university.entity.GroupEntity;
import ua.com.foxminded.university.entity.StudentEntity;
import ua.com.foxminded.university.entity.TimetableEntity;

public class Boot {
    
    public static void main(String[] arg) {
        
        /*
        ApplicationContext context = new ClassPathXmlApplicationContext("dao.xml");
        JdbcStudentDao studentDao = context.getBean("studentDao", JdbcStudentDao.class);
        StudentEntity student = studentDao.getById(1);
        JdbcTimetableDao timetableDao = context.getBean("timetableDao", JdbcTimetableDao.class);
        TimetableEntity timetable = timetableDao.getById(1);
        System.out.println(timetable.toString());
        System.out.println(student.toString());
        */
        
        ApplicationContext context = new AnnotationConfigApplicationContext(DaoConfig.class);
        JdbcCourseDao courseDao = context.getBean("courseDao", JdbcCourseDao.class);
        CourseEntity course = courseDao.getById(1);
        System.out.println(course.toString());
        
        JdbcGroupDao groupDao = context.getBean("groupDao", JdbcGroupDao.class);
        GroupEntity group = groupDao.getById(1);
        System.out.println(group.toString());
        
        JdbcStudentDao studentDao = context.getBean("studentDao", JdbcStudentDao.class);
        StudentEntity student = studentDao.getGroupByStudentId(1);
        System.out.println(student.toString());
    }
}
