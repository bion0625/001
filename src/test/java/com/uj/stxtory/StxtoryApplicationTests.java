package com.uj.stxtory;

import com.uj.stxtory.domain.entity.TbUser;
import com.uj.stxtory.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.transaction.Transactional;
import java.util.List;

@SpringBootTest
//@Transactional
class StxtoryApplicationTests {

	@Autowired
	UserRepository userRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void save() {
		String id = "test005";
		String pwd = "11dnjf3dlf#";
		String name = "테스트";

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		pwd = passwordEncoder.encode(pwd);

		TbUser tbUser = new TbUser();
		tbUser.setUserLoginId(id);
		tbUser.setUserPassword(pwd);
		tbUser.setUserName(name);

		userRepository.save(tbUser);
	}

	@Test
	void findAll(){
		List<TbUser> all = userRepository.findAll();
	}

	@Test
	void deleteAll(){
		userRepository.deleteAll();
	}

	@Test
	void passencodeCampare(){
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		boolean matches = passwordEncoder.matches("11dnjf3dlf#", passEncoder(1));
		System.out.println("matches = " + matches);
	}

	private String passEncoder(int i){
		String pwd = "11dnjf3dlf#";

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		pwd = passwordEncoder.encode(pwd);
		System.out.println(i + ".pwd = " + pwd);
		return pwd;
	}
}
