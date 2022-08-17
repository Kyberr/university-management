package ua.com.foxminded.university.dao;

import ua.com.foxminded.university.entity.TeacherEntity;

public interface TeacherDao extends GenericDao<TeacherEntity> {
    
    public TeacherEntity getCoursesByTeacherId(int id);
}
