package ua.com.foxminded.university.model;

import lombok.Data;

@Data
public class StudentModel {

    private int id;
    private GroupModel group;
    private String firstName;
    private String lastName;
    private UserModel user;
    
    public boolean hasGroup() {
        return group !=null && group.getId() != null;
    }
}
