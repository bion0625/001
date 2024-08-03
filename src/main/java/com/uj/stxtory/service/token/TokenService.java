package com.uj.stxtory.service.token;

import com.uj.stxtory.domain.entity.GmailToken;
import com.uj.stxtory.repository.GmailTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TokenService {

    @Autowired
    GmailTokenRepository gmailTokenRepository;

    public GmailToken getGmailToken() {
        List<GmailToken> all = gmailTokenRepository.findAll();
        if (all.size() == 0) return null;
        return all.get(all.size()-1);
    }

    public void saveGmailToken(String token, String userLoginId, String fromEmail) {
        gmailTokenRepository.save(new GmailToken(token, userLoginId, fromEmail));
    }
}
