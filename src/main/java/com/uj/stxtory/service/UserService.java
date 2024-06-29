package com.uj.stxtory.service;

import com.uj.stxtory.domain.entity.TbUser;
import com.uj.stxtory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public boolean isIdDupl(String userLoginId) {
        return userRepository.findByUserLoginId(userLoginId).orElse(null) != null;
    }

    public void save(TbUser user) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setUserPassword(encoder.encode(user.getUserPassword()));
        userRepository.save(user);
    }
}
