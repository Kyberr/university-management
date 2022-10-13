package ua.com.foxminded.university.dao.jdbc;

import static org.junit.jupiter.api.Assertions.*;

import javax.persistence.EntityManagerFactory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ua.com.foxminded.university.config.AppConfigTest;
import ua.com.foxminded.university.dao.DaoException;
import ua.com.foxminded.university.dao.GroupDao;
import ua.com.foxminded.university.entity.GroupEntity;

@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = AppConfigTest.class)
@ExtendWith(SpringExtension.class)
class GroupJdbcDaoTest {
    
    private static final String EXPECTED_WEEK_DAY = "MONDAY";
    private static final String EXPECTED_STUDENT_LAST_NAME = "Smith";
    private static final String EXPECTED_STUDENT_FIST_NAME = "Julitta";
    private static final String NEW_GROUP_NAME = "kt-53";
    private static final String EXPECTED_GROUP_NAME = "kt-52";
    private static final long EXPECTED_ENDTIME = 39360000;
    private static final long EXPECTED_START_TIME = 36360000;
    private static final int TIMETABLES_QUANTITY = 2;
    private static final int INSERTED_GROUP_ID = 3;
    private static final int EXPECTED_COURSE_ID = 1;
    private static final int EXPECTED_TIMETABLE_ID = 1;
    private static final int FIST_ELEMENT = 0;
    private static final int EXPECTED_STUDENT_ID = 2;
    private static final int STUDENTS_QUANTITY = 2;
    private static final int GROUP_ID_NUMBER = 2;
    
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    
    @Test
    void insert_InsertingDataOfGroupToDatabase_DatabaseHasCorrectData() throws DaoException {
        GroupDao groupDao = new GroupJdbcDao(entityManagerFactory);
        GroupEntity group = new GroupEntity();
        group.setName(NEW_GROUP_NAME);
        GroupEntity groupWithId = groupDao.insert(group);
        GroupEntity insertedGroup = entityManagerFactory.createEntityManager()
                                                        .find(GroupEntity.class, INSERTED_GROUP_ID);
        
        assertEquals(INSERTED_GROUP_ID, groupWithId.getId());
        assertEquals(NEW_GROUP_NAME, insertedGroup.getName());
    }
    
    @Test
    void getById_ReceivingDatabaseDataOfGroup_CorrectReceivedData() throws DaoException {
        GroupDao groupDao = new GroupJdbcDao(entityManagerFactory);
        GroupEntity group = groupDao.getById(GROUP_ID_NUMBER);
        
        assertEquals(GROUP_ID_NUMBER, group.getId());
        assertEquals(EXPECTED_GROUP_NAME, group.getName());
    }
    
    @Test
    void update_UpdatingDatabaseDataOfGroup_DatabaseHasCorrectData() throws DaoException {
        GroupDao groupDao = new GroupJdbcDao(entityManagerFactory);
        GroupEntity group = new GroupEntity();
        group.setId(GROUP_ID_NUMBER);
        group.setName(EXPECTED_GROUP_NAME);
        groupDao.update(group);
        
        GroupEntity updatedGroup = entityManagerFactory.createEntityManager()
                                                       .find(GroupEntity.class, GROUP_ID_NUMBER);
        assertEquals(group.getId(), updatedGroup.getId());
        assertEquals(group.getName(), updatedGroup.getName());
    }
    
    @Test
    void deleteById_DeletingDatabaseDataOfGroup_DatabaseHasNoData() throws DaoException {
        GroupDao groupDao = new GroupJdbcDao(entityManagerFactory);
        groupDao.deleteById(GROUP_ID_NUMBER);
        GroupEntity group = new GroupEntity();
        group.setId(GROUP_ID_NUMBER);
        boolean containStatus = entityManagerFactory.createEntityManager().contains(group);
        assertFalse(containStatus);
    }
    
    @Test
    void getTimetableListByGroupId_GettingDataFromDatabase_CorrectRecevedData() throws DaoException {
        GroupDao groupDao = new GroupJdbcDao(entityManagerFactory);
        GroupEntity receivedGroupData = groupDao.getTimetableListByGroupId(GROUP_ID_NUMBER);
        
        assertEquals(GROUP_ID_NUMBER, receivedGroupData.getId());
        assertEquals(EXPECTED_GROUP_NAME, receivedGroupData.getName());
        assertEquals(EXPECTED_TIMETABLE_ID, receivedGroupData.getTimetableList().get(FIST_ELEMENT).getId());
        assertEquals(EXPECTED_START_TIME, receivedGroupData.getTimetableList().get(FIST_ELEMENT)
                                                                              .getStartTime());
        assertEquals(EXPECTED_ENDTIME, receivedGroupData.getTimetableList().get(FIST_ELEMENT).getEndTime());
        assertEquals(EXPECTED_WEEK_DAY, receivedGroupData.getTimetableList().get(FIST_ELEMENT).getWeekDay()
                                                                                              .toString());
        assertEquals(EXPECTED_COURSE_ID, receivedGroupData.getTimetableList().get(FIST_ELEMENT).getCourse()
                                                                                               .getId());
        assertEquals(TIMETABLES_QUANTITY, receivedGroupData.getTimetableList().size());
    }
    
    @Test
    void getStudentListByGroupId_GettingDataFromDatabase_CorrectReceivedData() throws DaoException {
        GroupDao groupDao = new GroupJdbcDao(entityManagerFactory);
        GroupEntity receivedGroupData = groupDao.getStudentListByGroupId(GROUP_ID_NUMBER);
        
        assertEquals(GROUP_ID_NUMBER, receivedGroupData.getId());
        assertEquals(EXPECTED_GROUP_NAME, receivedGroupData.getName());
        assertEquals(EXPECTED_STUDENT_ID, receivedGroupData.getStudentList().get(FIST_ELEMENT).getId());
        assertEquals(EXPECTED_STUDENT_FIST_NAME, receivedGroupData.getStudentList().get(FIST_ELEMENT)
                                                                                   .getFirstName());
        assertEquals(EXPECTED_STUDENT_LAST_NAME, receivedGroupData.getStudentList().get(FIST_ELEMENT)
                                                                                   .getLastName());
        assertEquals(GROUP_ID_NUMBER, receivedGroupData.getStudentList().get(FIST_ELEMENT).getGroup()
                                                                                          .getId());
        assertEquals(STUDENTS_QUANTITY, receivedGroupData.getStudentList().size());
    }
}
