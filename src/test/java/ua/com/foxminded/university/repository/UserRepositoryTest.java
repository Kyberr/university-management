package ua.com.foxminded.university.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ua.com.foxminded.university.config.RepositoryTestConfig;
import ua.com.foxminded.university.entity.RoleAuthority;
import ua.com.foxminded.university.entity.UserAuthorityEntity;
import ua.com.foxminded.university.entity.UserEntity;

@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ContextConfiguration(classes = RepositoryTestConfig.class)
@Transactional
@ActiveProfiles("test")
class UserRepositoryTest {
    
    public static final int USERS_QUANTITY = 1;
    public static final String PASSWORD = "admin";
    public static final String EMAIL_B = "email@com";
    public static final String EMAIL_A = "admin@com";
    
    @PersistenceUnit
    EntityManagerFactory entityManagerFactory;
    
    @PersistenceContext
    EntityManager entityManager;
    
    @Autowired
    UserRepository userRepository;
    
    @BeforeEach
    void init() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
             
        UserEntity user = new UserEntity();
        user.setEnabled(true);
        user.setEmail(EMAIL_A);
        user.setPassword(PASSWORD);
        entityManager.persist(user);
        
        UserEntity anotherUser = new UserEntity();
        anotherUser.setEmail(EMAIL_B);
        entityManager.persist(anotherUser);
        
        UserAuthorityEntity authority = new UserAuthorityEntity();
        authority.setRoleAuthority(RoleAuthority.ROLE_ADMIN);
        authority.setUser(user);
        entityManager.persist(authority);
        entityManager.getTransaction().commit();
    }
    
    @Test
    void findByUserAuthorityIsEmpty() {
        List<UserEntity> users = userRepository.findByUserAuthorityIsNull();
        assertEquals(USERS_QUANTITY, users.size());
    }
    
    @Test
    void findByEmail_shouldReturnUser_whenEnterEmaill() {
        UserEntity user = userRepository.findByEmail(EMAIL_A);
        assertEquals(EMAIL_A, user.getEmail());
    }
    
    @Test
    void findByPasswordIsNotNull_shouldReturnAllUsersHavingPassword() {
        List<UserEntity> users = userRepository.findByPasswordIsNotNull();
        assertEquals(USERS_QUANTITY, users.size());
    }
}
