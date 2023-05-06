package com.uj.stxtory.service;

import com.uj.stxtory.MsgConstants;
import com.uj.stxtory.domain.dto.LoginUser;
import com.uj.stxtory.domain.entity.TbUser;
import com.uj.stxtory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    @Autowired
    UserRepository userRepository;

    public LoginUser loginSession(String id){
        String loginMsg = MsgConstants.SUCCESS;

        Optional<TbUser> byUserId = userRepository.findByUserId(id);
        LoginUser loginUser = null;
        if (byUserId.isPresent()){
            loginUser = new LoginUser(byUserId.get().getUserName(), loginMsg);
        }else{
            loginMsg = MsgConstants.LOGIN_ID_EX_001;
            loginUser = new LoginUser("", loginMsg);
        }
        loginUser.setId(id);
        return loginUser;
    }

    public LoginUser login(String id, String pwd){
        String loginMsg = MsgConstants.SUCCESS;

        if (id.isEmpty() && pwd.isEmpty()){
            loginMsg = MsgConstants.LOGIN_ACCOUNT_EX_001;
            return new LoginUser("", loginMsg);
        }
        if (id.isEmpty()){
            loginMsg = MsgConstants.LOGIN_ID_EX_002;
            return new LoginUser("", loginMsg);
        }
        if (pwd.isEmpty()){
            loginMsg = MsgConstants.LOGIN_PWD_EX_002;
            return new LoginUser("", loginMsg);
        }
        Optional<TbUser> byUserId = userRepository.findByUserId(id);
        LoginUser loginUser = null;
        if (byUserId.isPresent()){
            if(!pwd.equals(byUserId.get().getUserPassword())){
                loginMsg = MsgConstants.LOGIN_PWD_EX_001;
                loginUser = new LoginUser("", loginMsg);
            }else{
                loginUser = new LoginUser(byUserId.get().getUserName(), loginMsg);
            }
        }else{
            loginMsg = MsgConstants.LOGIN_ID_EX_001;
            loginUser = new LoginUser("", loginMsg);
        }
        return loginUser;
    }
}
