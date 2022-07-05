package com.udemy.src.auth;

import com.udemy.config.BaseException;
import com.udemy.src.auth.model.PostLoginReq;
import com.udemy.src.auth.model.PostLoginRes;
import com.udemy.src.auth.model.User;
import com.udemy.utils.JwtService;
import com.udemy.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.udemy.config.BaseResponseStatus.*;

@Service
public class AuthService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AuthDao authDao;
    private final AuthProvider authProvider;
    private final JwtService jwtService;


    @Autowired
    public AuthService(AuthDao authDao, AuthProvider authProvider, JwtService jwtService) {
        this.authDao = authDao;
        this.authProvider = authProvider;
        this.jwtService = jwtService;

    }

    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException {
        User user=authDao.getPwd(postLoginReq);
        String encryptPwd;
        try {
            encryptPwd=new SHA256().encrypt(postLoginReq.getPwd());
        }
        catch (Exception exception) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        if(user.getPwd().equals(encryptPwd)){
            int userIdx=user.getUserIdx();
            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(userIdx,jwt);
        }
        else
            throw new BaseException(FAILED_TO_LOGIN);
    }

}
