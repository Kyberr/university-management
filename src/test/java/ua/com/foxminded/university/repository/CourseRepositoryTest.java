package ua.com.foxminded.university.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import ua.com.foxminded.university.entity.Course;
import ua.com.foxminded.university.entity.Teacher;
import ua.com.foxminded.university.entity.User;
import ua.com.foxminded.university.entity.Lesson;
import ua.com.foxminded.university.entitymother.CourseMother;
import ua.com.foxminded.university.entitymother.LessonMother;
import ua.com.foxminded.university.entitymother.UserMother;

@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
class CourseRepositoryTest {
    
    private static final int TEACHERS_QUANTITY = 1;
    private static final int LESSONS_QUANTITY = 1;
    
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;
    
    @Autowired
    private CourseRepository courseRepository;
    
    private Course course;
    private Lesson lesson;
    private Teacher teacher;
    private User user;
    
    @BeforeEach
    void init() {
        course = CourseMother.complete().build();
        user = UserMother.complete().build();
        teacher = Teacher.builder().build();
        
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(course);
        entityManager.persist(user);
        
        teacher.setUser(user);
        teacher.setCourses(new HashSet<>());
        teacher.getCourses().add(course);
        entityManager.persist(teacher);

        lesson = LessonMother.complete()
                             .course(course)
                             .teacher(teacher).build();
        entityManager.persist(lesson);
        entityManager.getTransaction().commit();
        entityManager.close();
    }
    
    @Test
    void findByTeachersId_ShouldReturnCoursesOwnedByTeacher() {
        List<Course> courses = courseRepository.findByTeachersId(teacher.getId());
        assertEquals(course, courses.iterator().next());
    }
    
    @Test
    void getCourseRelationsById_ShouldContainAllDependencies() {
        Course persistedCourse = courseRepository.findTeachersAndLessonsById(course.getId());
        LocalDate persistedDate = persistedCourse.getLessons()
                                                 .iterator().next().getDatestamp();
        
        assertEquals(lesson.getDatestamp(), persistedDate);
        assertEquals(LESSONS_QUANTITY, persistedCourse.getLessons().size());
        assertEquals(TEACHERS_QUANTITY, persistedCourse.getTeachers().size());
    }

    @Test
    void findTimetablesById_ShouldReturnCourseWithTimetableList_WhenEnterCourseId() {
        Course receivedCourse = courseRepository.findTimetablesById(course.getId());
        Set<Lesson> timetables = receivedCourse.getLessons();
        assertEquals(LESSONS_QUANTITY, timetables.size());
    }
    
    @Test
    void findById_ShouldReturnCourseWithId() {
        Course receivedCourse = courseRepository.findById(course.getId().intValue());
        assertEquals(course.getId(), receivedCourse.getId());
    }
}
