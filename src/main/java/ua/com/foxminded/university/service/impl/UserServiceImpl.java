package ua.com.foxminded.university.service.impl;

import java.lang.reflect.Type;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.modelmapper.ConfigurationException;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ua.com.foxminded.university.entity.UserEntity;
import ua.com.foxminded.university.exception.ServiceException;
import ua.com.foxminded.university.model.UserModel;
import ua.com.foxminded.university.repository.UserRepository;
import ua.com.foxminded.university.service.UserService;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    public static final Type TYPE = new TypeToken<List<UserModel>>() {}.getType();
    
    private final UserRepository userRepository;
    private final UserDetailsManager userDetailsManager;
    private final ModelMapper modelMapper;
    
    public UserModel getById(int id) throws ServiceException {
        try {
            UserEntity entity = userRepository.findById(id);
            return modelMapper.map(entity, UserModel.class);
        } catch (IllegalArgumentException | ConfigurationException | MappingException e) {
            throw new ServiceException("Getting user by its id fails.", e);
        }
    }
    
    @Override
    public void deleteById(Integer id) throws ServiceException {
        try {
            userRepository.deleteById(id);
        } catch (IllegalArgumentException e) {
            throw new ServiceException("Deleting the user fails", e);
        }
    }
    
    @Override
    public void deleteByEmail(String email) throws ServiceException {
        try {
            userDetailsManager.deleteUser(email);
        } catch (IllegalArgumentException e) {
            throw new ServiceException("Deleting the user fails.", e);
        }
    }
    
    @Override
    public void create(UserModel model) throws ServiceException {
        try {
            PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
            String authority = String.valueOf(model.getUserAuthority().getAuthority());
            UserDetails user = User.builder().username(model.getEmail())
                                             .password(model.getPassword())
                                             .passwordEncoder(encoder::encode)
                                             .authorities(authority)
                                             .disabled(!model.getEnabled())
                                             .build();
            userDetailsManager.createUser(user);
        } catch (ConstraintViolationException e) {
            throw new ServiceException("Creating the user fails.", e);
        }
    }
    
    @Override
    public List<UserModel> getNotAuthorizedUsers() throws ServiceException {
        try {
            List<UserEntity> entities = userRepository.findByUserAuthorityIsNull();
            return modelMapper.map(entities, TYPE);
        } catch (IllegalArgumentException | ConfigurationException | MappingException e) {
            throw new ServiceException("Getting not authrized users fails.", e);
        }
    }
    
    @Override
    public UserModel getByEmail(String email) throws ServiceException {
        try {
            UserEntity entity = userRepository.findByEmail(email);
            return modelMapper.map(entity, UserModel.class);
        } catch (IllegalArgumentException e) {
            throw new ServiceException("Getting the email fails", e);
        }
    }
    
    @Override
    public List<UserModel> getAll() throws ServiceException {
        try {
            List<UserEntity> entities = userRepository.findAll();
            return modelMapper.map(entities, TYPE);
        } catch (IllegalArgumentException | ConfigurationException | MappingException e) {
            throw new ServiceException("Getting all students fails", e);
        }
    }
    
    @Override
    public void update(UserModel model) throws ServiceException {
        try {   
            PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
            UserDetails user = User.builder().username(model.getEmail())
                                             .password(model.getPassword())
                                             .passwordEncoder(encoder::encode)
                                             .roles(String.valueOf(model.getUserAuthority()
                                                                        .getAuthority()))
                                             .disabled(!model.getEnabled())
                                             .build();
            userDetailsManager.updateUser(user);
        } catch (ConstraintViolationException  e) {
            throw new ServiceException("Updating user object fails.", e);
        }
    }
}
