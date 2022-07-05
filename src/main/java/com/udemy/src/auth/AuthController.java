package com.udemy.src.auth;

import com.udemy.config.BaseException;
import com.udemy.config.BaseResponse;
import com.udemy.src.auth.model.PostLoginReq;
import com.udemy.src.auth.model.PostLoginRes;
import com.udemy.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.udemy.config.BaseResponseStatus.*;
import static com.udemy.utils.ValidationRegex.isRegexEmail;


@RestController
@RequestMapping("/auth")
public class AuthController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final AuthProvider authProvider;
    @Autowired
    private final AuthService authService;
    @Autowired
    private final JwtService jwtService;


    public AuthController(AuthProvider authProvider, AuthService authService, JwtService jwtService) {
        this.authProvider = authProvider;
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @PostMapping ("/login") // (GET) 127.0.0.1:9000/users
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) {
        try {
            if (postLoginReq.getEmail() == null) {
                return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
            }
            if (postLoginReq.getPwd() == null) {
                return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
            }
            if (!isRegexEmail(postLoginReq.getEmail())) {
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
            }
            PostLoginRes postLoginRes = authService.logIn(postLoginReq);

            return new BaseResponse<>(postLoginRes);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
