package ua.com.foxminded.university.service;

import java.util.List;
import java.util.Set;

import ua.com.foxminded.university.dto.TeacherDTO;

public interface TeacherService extends GenericService<TeacherDTO> {
    
    public void sortByLastName(Set<TeacherDTO> teachers);

    public void sortByLastName(List<TeacherDTO> teachers);
    
    public TeacherDTO getTeacherByEmail(String email);
    
    public List<TeacherDTO> getByCoursesId(int id);
    
    public TeacherDTO getByUserId(int id);

    public TeacherDTO getByIdWithCourses(int teacherId);
}
