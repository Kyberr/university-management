package ua.com.foxminded.university.service.impl;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.com.foxminded.university.dao.TeacherDao;
import ua.com.foxminded.university.entity.TeacherEntity;

@ExtendWith(MockitoExtension.class)
class TeacherServiceImplTestTest {
    
    private static final int COURSE_ID = 1;
    
    @InjectMocks
    private TeacherServiceImpl teacherService;
    
    @Mock
    private TeacherDao teacherDaoMock;

    @Test
    
    void getCourseListByTeacherId_GettingTeacherMocel_CorrectCallQuantity() {
        TeacherEntity teacher = new TeacherEntity();
        teacher.setCourseList(new ArrayList<>());
        when(teacherDaoMock.getCourseListByTeacherId(anyInt())).thenReturn(teacher);
        teacherService.getCourseListByTeacherId(COURSE_ID);
        verify(teacherDaoMock, times(1)).getCourseListByTeacherId(anyInt());
    }
}