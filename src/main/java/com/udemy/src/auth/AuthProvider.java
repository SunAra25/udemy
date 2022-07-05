package com.udemy.src.auth;

import com.udemy.config.BaseException;
import com.udemy.src.post.model.GetPostImgRes;
import com.udemy.src.post.model.GetPostsRes;
import com.udemy.src.user.UserDao;
import com.udemy.src.user.model.GetUserFeedRes;
import com.udemy.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.udemy.config.BaseResponseStatus.DATABASE_ERROR;
import static com.udemy.config.BaseResponseStatus.USERS_EMPTY_USER_ID;

public class AuthProvider {

    private final AuthDao authDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public AuthProvider(AuthDao authDao, JwtService jwtService) {
        this.authDao = authDao;
        this.jwtService = jwtService;
    }

}