package com.uj.stxtory.service;

import com.uj.stxtory.domain.entity.TbUser;
import com.uj.stxtory.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class SecurityService implements UserDetailsService {

    private UserRepository userRepository;

    public SecurityService(@Autowired UserRepository userRepository){
        this.userRepository = userRepository;
    }
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userLoginId) throws UsernameNotFoundException {

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        Optional<TbUser> tbUser = userRepository.findByUserLoginId(userLoginId);
        if (tbUser.isPresent()){
            TbUser user = tbUser.get();
            grantedAuthorities.add(new SimpleGrantedAuthority(user.getUserRole()));
            return new User(user.getUserLoginId(), user.getUserPassword(), grantedAuthorities);
        }else{
            throw new UsernameNotFoundException("can not found User : " + userLoginId);
        }
    }
}
