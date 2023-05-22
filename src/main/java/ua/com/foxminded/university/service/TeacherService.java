package ua.com.foxminded.university.service;

import java.util.List;

import ua.com.foxminded.university.exception.ServiceException;
import ua.com.foxminded.university.model.TeacherModel;

public interface TeacherService {

    public List<TeacherModel> getAll() throws ServiceException;

    public TeacherModel getByIdWithCourses(int teacherId) throws ServiceException;
}