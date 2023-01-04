package ua.com.foxminded.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ua.com.foxminded.university.entity.StudentEntity;
import ua.com.foxminded.univesity.exception.RepositoryException;

public interface StudentRepository extends JpaRepository<StudentEntity, Integer> {
    
    public StudentEntity findGroupById(Integer id) throws RepositoryException;
    public StudentEntity findById(int id) throws RepositoryException;
}