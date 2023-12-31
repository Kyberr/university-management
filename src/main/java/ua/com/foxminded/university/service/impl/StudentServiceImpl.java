package ua.com.foxminded.university.service.impl;

import static ua.com.foxminded.university.exception.ErrorCode.STUDENT_CREATE_ERROR;
import static ua.com.foxminded.university.exception.ErrorCode.STUDENT_DELETE_ERROR;
import static ua.com.foxminded.university.exception.ErrorCode.STUDENT_FETCH_ERROR;
import static ua.com.foxminded.university.exception.ErrorCode.STUDENT_UPDATE_ERROR;
import static ua.com.foxminded.university.exception.ErrorCode.STUDETNS_FETCH_ERROR;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.modelmapper.ConfigurationException;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ua.com.foxminded.university.dto.StudentDTO;
import ua.com.foxminded.university.entity.Student;
import ua.com.foxminded.university.exception.ServiceException;
import ua.com.foxminded.university.repository.StudentRepository;
import ua.com.foxminded.university.service.StudentService;

@Service
@Transactional
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    
    public static final Type STUDENT_MODEL_LIST_TYPE = 
            new TypeToken<List<StudentDTO>>() {}.getType();
    
    private final ModelMapper modelMapper;
    private final StudentRepository studentRepository;
    
    @Override
    public StudentDTO getByEmail(String email) {
        try {
            Student student = studentRepository.findByUserEmail(email);
            return modelMapper.map(student, StudentDTO.class);
        } catch (DataAccessException | IllegalArgumentException | 
                 ConfigurationException | MappingException e) {
            throw new ServiceException(STUDENT_FETCH_ERROR, e);
        }
    }
    
    @Override 
    public void sortByLastName(List<StudentDTO> students) {
        Collections.sort(students, Comparator.comparing(student -> student.getUser()
                                                                          .getPerson()
                                                                          .getLastName()));
    }
    
    @Override
    public void deleteById(Integer id) {
        try {
            studentRepository.deleteById(id);
        } catch (DataAccessException | IllegalArgumentException e) {
            throw new ServiceException(STUDENT_DELETE_ERROR, e);
        }
    }
    
    @Override
    public void update(StudentDTO studentDto) {
        try {
            Student student = modelMapper.map(studentDto, Student.class);
            Student persistedStudent = studentRepository.findById(student.getId());
            persistedStudent.setGroup(student.getGroup());
            persistedStudent.setUser(student.getUser());
            studentRepository.saveAndFlush(persistedStudent);
        } catch (DataAccessException | IllegalArgumentException | 
                 ConfigurationException | MappingException e) {
            throw new ServiceException(STUDENT_UPDATE_ERROR, e);
        }
    }
    
    @Override 
    public StudentDTO getById(int id) {
        try {
            Student studentEntity = studentRepository.findById(id);
            return modelMapper.map(studentEntity, StudentDTO.class);
        } catch (IllegalArgumentException | ConfigurationException | MappingException e) {
            throw new ServiceException(STUDENT_FETCH_ERROR, e);
        }
    }
    
    @Override
    public List<StudentDTO> getAll() {
        try {
            List<Student> studentEntities = studentRepository.findAll();
            return modelMapper.map(studentEntities, STUDENT_MODEL_LIST_TYPE);
        } catch (IllegalArgumentException | ConfigurationException | MappingException e) {
            throw new ServiceException(STUDETNS_FETCH_ERROR, e);
        }
    }
    
    @Override
    public StudentDTO create(StudentDTO model) {
        try {
            Student entity = modelMapper.map(model, Student.class);
            Student createdEntity = studentRepository.saveAndFlush(entity);
            return modelMapper.map(createdEntity, StudentDTO.class);
        } catch (IllegalArgumentException | ConfigurationException | MappingException e) {
            throw new ServiceException(STUDENT_CREATE_ERROR, e);
        }
    }
}
