package com.udemy.src.post;

import com.udemy.config.BaseException;
import com.udemy.config.BaseResponse;
import com.udemy.config.BaseResponseStatus;
import com.udemy.src.post.model.*;
import com.udemy.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/posts")
public class PostController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final PostProvider postProvider;
    @Autowired
    private final PostService postService;
    @Autowired
    private final JwtService jwtService;





    public PostController(PostProvider postProvider, PostService postService, JwtService jwtService){
        this.postProvider = postProvider;
        this.postService = postService;
        this.jwtService = jwtService;
    }


    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/users
    public BaseResponse<List<GetPostsRes>> getPosts(@RequestParam int userIdx) {
        try{
            List<GetPostsRes> getPostsRes = postProvider.retrievePosts(userIdx);
            return new BaseResponse<>(getPostsRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    @ResponseBody
    @PostMapping("") // (GET) 127.0.0.1:9000/users
    public BaseResponse<PostPostsRes> createPosts(@RequestBody PostPostsReq postPostsReq) {
        try{
            int userIdxByJwt = jwtService.getUserIdx();
            if(postPostsReq.getUserIdx()!=userIdxByJwt){
                return new BaseResponse<>(BaseResponseStatus.INVALID_USER_JWT);
            }
            if(postPostsReq.getContent().length()>450){
                return new BaseResponse<>(BaseResponseStatus.POST_POSTS_INVALID_CONTENTS);
            }
            if(postPostsReq.getPostImgUrls().size()<1){
                return new BaseResponse<>(BaseResponseStatus.POST_POSTS_EMPTY_IMGURL);
            }
            PostPostsRes postPostsRes = postService.createPosts(postPostsReq.getUserIdx(),postPostsReq);
            return new BaseResponse<>(postPostsRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PatchMapping ("/{postIdx}") // (GET) 127.0.0.1:9000/users
    public BaseResponse<String> modifyPost(@PathVariable ("postIdx") int postIdx,@RequestBody PatchPostsReq patchPostsReq) {
        try{
            if(patchPostsReq.getContent().length()>450){
                return new BaseResponse<>(BaseResponseStatus.POST_POSTS_INVALID_CONTENTS);
            }
            postService.modifyPost(patchPostsReq.getUserIdx(),postIdx,patchPostsReq);
            String result = "게시물 정보 수정을 완료하였습니다.";
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PatchMapping ("/{postIdx}/status") // (GET) 127.0.0.1:9000/users
    public BaseResponse<String> deletePost(@PathVariable ("postIdx") int postIdx,@RequestBody PatchPostsReq patchPostsReq) {
        try{
            postService.deletePost(postIdx);
            String result = "삭제를 성공했습니다.";
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
