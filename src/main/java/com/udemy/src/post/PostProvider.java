package com.udemy.src.post;

import com.udemy.config.BaseException;
import com.udemy.src.post.model.GetPostImgRes;
import com.udemy.src.post.model.GetPostsRes;
import com.udemy.src.user.model.GetUserFeedRes;
import com.udemy.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.udemy.config.BaseResponseStatus.DATABASE_ERROR;
import static com.udemy.config.BaseResponseStatus.USERS_EMPTY_USER_ID;

//Provider : Read의 비즈니스 로직 처리
@Service
public class PostProvider {

    private final PostDao postDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public PostProvider(PostDao postDao, JwtService jwtService) {
        this.postDao = postDao;
        this.jwtService = jwtService;
    }


    public List<GetPostsRes> retrievePosts(int userIdx) throws BaseException {

        if (checkUserExist(userIdx) == 0) {
            throw new BaseException(USERS_EMPTY_USER_ID);
        }

        try {
            List<GetPostsRes> getPosts = postDao.selectPosts(userIdx);
            return getPosts;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public int checkUserExist(int userIdx) throws BaseException {
        try {
            return postDao.checkUserExist(userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkPostExist(int postIdx) throws BaseException {
        try {
            return postDao.checkPostExist(postIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
