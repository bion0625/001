package com.uj.stxtory;

import com.uj.stxtory.domain.entity.TbUser;
import com.uj.stxtory.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.List;

@SpringBootTest
@Transactional
class StxtoryApplicationTests {

	@Autowired
	UserRepository userRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void save() {
		String id = "stxtory";
		String pwd = "11dnjf3dlf#";
		String name = "테스트";

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
}
