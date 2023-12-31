package ua.com.foxminded.university.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.com.foxminded.university.controller.CourseController.COURSE_ID_PARAMETER_NAME;
import static ua.com.foxminded.university.controller.GroupController.GROUP_ID_PARAMETER_NAME;
import static ua.com.foxminded.university.controller.LessonController.DATE_PARAMETER;
import static ua.com.foxminded.university.controller.LessonController.LESSON_ATTRIBUTE;
import static ua.com.foxminded.university.controller.TimetableController.TIMETABLE_ID_PARAMETER_NAME;
import static ua.com.foxminded.university.entity.Authority.ADMIN;
import static ua.com.foxminded.university.entity.Authority.STAFF;
import static ua.com.foxminded.university.entity.Authority.STUDENT;
import static ua.com.foxminded.university.entity.Authority.TEACHER;

import java.time.LocalDate;
import java.util.HashSet;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.transaction.AfterTransaction;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import ua.com.foxminded.university.dto.LessonDTO;
import ua.com.foxminded.university.dto.TeacherDTO;
import ua.com.foxminded.university.dto.TimetableDTO;
import ua.com.foxminded.university.dtomother.LessonDTOMother;
import ua.com.foxminded.university.entity.Course;
import ua.com.foxminded.university.entity.Group;
import ua.com.foxminded.university.entity.Lesson;
import ua.com.foxminded.university.entity.Student;
import ua.com.foxminded.university.entity.Teacher;
import ua.com.foxminded.university.entity.Timetable;
import ua.com.foxminded.university.entitymother.CourseMother;
import ua.com.foxminded.university.entitymother.GroupMother;
import ua.com.foxminded.university.entitymother.LessonMother;

@SpringBootTest
@ActiveProfiles("prod")
@AutoConfigureMockMvc
@Testcontainers
class LessonControllerIntegrationTest extends DefaultControllerTest {
    
    @Container
    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres");
    
    private Lesson lesson;
    private LessonDTO lessonDto;
    private Timetable timetable;
    private TimetableDTO timetableDto;
    private Course course;
    private Group group;
    private Teacher teacher;
    private Student student;
    
    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @BeforeEach
    void setUp() {
        teacher = Teacher.builder().user(teacherUser).build();
        course = CourseMother.complete().build();
        timetable = Timetable.builder().name(TIMETABLE_NAME).build();
        lesson = LessonMother.complete().groups(new HashSet<>()).build();
        group = GroupMother.complete()
                           .lessons(new HashSet<>()).build();
        student = new Student();
        
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        
        entityManager.persist(timetable);
        entityManager.persist(course);
        entityManager.persist(group);
        lesson.setTimetable(timetable);
        lesson.setCourse(course);
        lesson.addGroup(group);
       
        student.setUser(studentUser);
        student.setGroup(group);
        
        entityManager.persist(lesson);
        entityManager.persist(teacher);
        entityManager.persist(student);
        
        entityManager.getTransaction().commit();
        entityManager.close();
        
        timetableDto = TimetableDTO.builder().name(TIMETABLE_NAME)
                                             .id(timetable.getId()).build();
        
        timetableDto.setId(timetable.getId());
        lessonDto = LessonDTOMother.complete()
                                   .id(lesson.getId())
                                   .timetable(timetableDto)
                                   .build();
    }
    
    @AfterTransaction
    void dropDown() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        
        Timetable persistedTimetabe = entityManager.find(Timetable.class, timetable.getId());
        Group persistedGroup = entityManager.find(Group.class, group.getId());
        Lesson persistedLesson = entityManager.find(Lesson.class, lesson.getId());
        Student persistedStudent = entityManager.find(Student.class, student.getId());
        
        if (persistedTimetabe != null) {
            entityManager.remove(persistedTimetabe);
        }
        
        if (persistedLesson != null) {
            entityManager.remove(persistedLesson);
        }
        
        if (persistedGroup != null) {
            entityManager.remove(persistedGroup);
        }
        
        if (persistedStudent != null) {
            entityManager.remove(persistedStudent);
        }
        
        entityManager.getTransaction().commit();
        entityManager.close();
    }
    
    @Test
    @WithUserDetails(STAFF_EMAIL)
    void edit_ShouldRedirect() throws Exception {
        mockMvc.perform(post("/lessons/{lessonId}/update", lesson.getId())
                    .flashAttr(LESSON_ATTRIBUTE, lessonDto)
                    .with(csrf()))
               .andExpect(authenticated().withRoles(STAFF.toString()))
               .andExpect(status().is3xxRedirection());
    }
    
    @Test
    @WithUserDetails(STUDENT_EMAIL)
    void getGroupScheduleForDate_ShouldRedirect() throws Exception {
        mockMvc.perform(get("/lessons/group-week-schedule/{email}", studentUser.getEmail())
                    .param(DATE_PARAMETER, lesson.getDatestamp().toString()))
               .andExpect(authenticated().withRoles(STUDENT.toString()))
               .andExpect(status().is3xxRedirection());
    }
    
    @Test
    @WithUserDetails(STUDENT_EMAIL)
    void getPreviousWeekGroupSchedule_ShouldRedirect() throws Exception {
        mockMvc.perform(get("/lessons/group-week-schedule/{date}/{email}/back", 
                            lesson.getDatestamp(), studentUser.getEmail()))
               .andExpect(authenticated().withRoles(STUDENT.toString()))
               .andExpect(status().is3xxRedirection());
    }
    
    @Test
    @WithUserDetails(STUDENT_EMAIL)
    void getNextWeekGroupSchedule_ShouldReturnStatusIsOk() throws Exception {
        mockMvc.perform(get("/lessons/group-week-schedule/{date}/{email}/next", 
                            lesson.getDatestamp(), studentUser.getEmail()))
               .andExpect(authenticated().withRoles(STUDENT.toString()))
               .andExpect(status().is3xxRedirection());
    }
    
    @Test
    @WithUserDetails(STUDENT_EMAIL)
    void getGroupWeekSchedule_ShouldReturnStatusIsOk() throws Exception {
        mockMvc.perform(get("/lessons/group-week-schedule/{date}/{email}", 
                            lesson.getDatestamp(), studentUser.getEmail()))
               .andExpect(authenticated().withRoles(STUDENT.toString()))
               .andExpect(status().isOk());
    }
    
    @Test
    @WithUserDetails(TEACHER_EMAIL)
    void getTeacherScheduleForDate_ShouldAuthorizeCredentialsAndRedirect() throws Exception {
        mockMvc.perform(get("/lessons/teacher-week-schedule/{email}", teacherUser.getEmail())
                    .param("date", lesson.getDatestamp().toString()))
               .andExpect(authenticated().withRoles(TEACHER.toString()))
               .andExpect(status().is3xxRedirection());
    }
    
    @Test
    @WithUserDetails(TEACHER_EMAIL)
    void getPreviousTeacherWeekSchedule_ShouldAuthorizeCredentialsAndRedirect() throws Exception {
        mockMvc.perform(get("/lessons/teacher-week-schedule/{date}/{email}/back", 
                    lesson.getDatestamp(), teacherUser.getEmail()))
               .andExpect(authenticated().withRoles(TEACHER.toString()))
               .andExpect(status().is3xxRedirection());
    }
    
    @Test
    @WithUserDetails(TEACHER_EMAIL)
    void getNextWeekTeacherSchedule_ShouldAuthorizeCredentialsAndRedirect() throws Exception {
        mockMvc.perform(get("/lessons/teacher-week-schedule/{date}/{email}/next", 
                    lesson.getDatestamp(), teacherUser.getEmail()))
               .andExpect(authenticated().withRoles(TEACHER.toString()))
               .andExpect(status().is3xxRedirection());
    }
    
    @Test
    @WithUserDetails(TEACHER_EMAIL)
    void getTeacherWeekSchedule_ShouldAuthorizeCredentialsAndReturnStatusIsOk() throws Exception {
        mockMvc.perform(get("/lessons/teacher-week-schedule/{date}/{email}", 
                            lesson.getDatestamp(), teacherUser.getEmail()))
               .andExpect(authenticated().withRoles(TEACHER.toString()))
               .andExpect(status().isOk());
    }
    
    @Test
    @WithUserDetails(ADMIN_EMAIL)
    void applyTimetable_ShouldAuthorizeCredentialsAndRedirect() throws Exception {
        mockMvc.perform(post("/lessons/{date}/apply-timetable", lesson.getDatestamp())
                    .param(TIMETABLE_ID_PARAMETER_NAME, String.valueOf(timetable.getId()))
                    .with(csrf()))
               .andExpect(authenticated().withRoles(ADMIN.toString()))
               .andExpect(status().is3xxRedirection());
    }
    
    @Test
    @WithUserDetails(ADMIN_EMAIL)
    void create_ShouldAuthorizeCredentialsAndRedirect() throws Exception {
        LocalDate date = LocalDate.now();
        lessonDto.setTeacher(TeacherDTO.builder().id(teacher.getId()).build());;
        
        mockMvc.perform(post("/lessons/{date}/create", date)
                    .param(TIMETABLE_ID_PARAMETER_NAME, String.valueOf(timetable.getId()))
                    .param(COURSE_ID_PARAMETER_NAME, String.valueOf(course.getId()))
                    .param(GROUP_ID_PARAMETER_NAME, String.valueOf(group.getId()))
                    .flashAttr(LESSON_ATTRIBUTE, lessonDto)
                    .with(csrf()))
               .andExpect(authenticated().withRoles(ADMIN.toString()))
               .andExpect(status().is3xxRedirection());
    }
    
    @Test
    @WithUserDetails(ADMIN_EMAIL)
    void deleteById_ShouldAuthanticateCredentialsAndRedirect() throws Exception {
        lessonDto.setDatestamp(lesson.getDatestamp());
        mockMvc.perform(post("/lessons/delete/{lessonId}", lesson.getId())
                    .flashAttr(LESSON_ATTRIBUTE, lessonDto)
                    .with(csrf()))
               .andExpect(authenticated().withRoles(ADMIN.toString()))
               .andExpect(status().is3xxRedirection());
    }
    
    @Test
    @WithUserDetails(ADMIN_EMAIL)
    void getDayLessons_ShouldAuthorizeCredentialsAndRenderTemplate() throws Exception {
        int timetableId = lesson.getTimetable().getId();
        int courseId = lesson.getCourse().getId();
        LocalDate localDate = lesson.getDatestamp();
        
        mockMvc.perform(get("/lessons/day-lessons/{date}", localDate)
                    .param(TIMETABLE_ID_PARAMETER_NAME, String.valueOf(timetableId))
                    .param(COURSE_ID_PARAMETER_NAME, String.valueOf(courseId)))
               .andExpect(authenticated().withRoles(ADMIN.toString()))
               .andExpect(status().isOk());
    }
    
    @Test
    @WithUserDetails(ADMIN_EMAIL)
    void back_ShouldAuthorizeCredentialsAndRedirect() throws Exception {
        mockMvc.perform(get("/lessons/{date}/back", lesson.getDatestamp().toString()))
               .andExpect(authenticated().withRoles(ADMIN.toString()))
               .andExpect(status().is3xxRedirection());
    }
    
    @Test
    @WithUserDetails(ADMIN_EMAIL)
    void next_ShouldAuthorizeCredentialsAndRedirect() throws Exception {
        mockMvc.perform(get("/lessons/{date}/next", lesson.getDatestamp().toString()))
               .andExpect(authenticated().withRoles(ADMIN.toString()))
               .andExpect(status().is3xxRedirection());
    }
    
    @Test
    @WithUserDetails(ADMIN_EMAIL)
    void getMonthLessons_ShouldAuthorizeCredentialsAndReturnStatusIsOk() throws Exception {
        mockMvc.perform(get("/lessons/month-lessons/{date}", lesson.getDatestamp().toString()))
               .andExpect(authenticated().withRoles(ADMIN.toString()))
               .andExpect(status().isOk());
    }
}
