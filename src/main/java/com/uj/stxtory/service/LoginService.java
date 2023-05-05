package com.uj.stxtory.service;

import com.uj.stxtory.domain.entity.TbUser;
import com.uj.stxtory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    @Autowired
    UserRepository userRepository;

    public String login(String id, String pwd){
        Optional<TbUser> byUserId = userRepository.findByUserId(id);
        if(byUserId.isEmpty()){
            return "noId";
        }
        if(!pwd.equals(byUserId.get().getUserPassword())){
            return "noPwd";
        }

        return "SUCCESS";
    }
}
