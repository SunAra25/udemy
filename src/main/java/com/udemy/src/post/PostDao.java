package com.udemy.src.post;


import com.udemy.src.post.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PostDao {

    private JdbcTemplate jdbcTemplate;
    private List<GetPostImgRes> getPostImgRes;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetPostsRes> selectPosts(int userIdx){
        String selectPostsQuery =
                "select p.postIdx as postIdx, \n" +
                        "u.userIdx as userIdx, " +
                        "u.nickName as nickName," +
                        "u.profileImgUrl as profileImgUrl," +
                        "p.content as content," +
                        "if(postLikeCount is null, 0, postLikeCount) as postLikeCount," +
                        "if(commentCount is null, 0, commentCount) as commentCount," +
                        "case when timestampdiff(second, p.updatedAt, current_timestamp) < 60" +
                        "then concat(timestampdiff(second, p.updatedAt, current_timestamp), '초 전')" +
                        "when timestampdiff(minute, p.updatedAt, current_timestamp) < 60" +
                        "then concat(timestampdiff(minute, p.updatedAt, current_timestamp), '분 전')" +
                        "when timestampdiff(hour, p.updatedAt, current_timestamp) < 24" +
                        "then concat(timestampdiff(hour, p.updatedAt, current_timestamp), '시간 전')" +
                        "when timestampdiff(day, p.updatedAt, current_timestamp) < 365" +
                        "then concat(timestampdiff(day, p.updatedAt, current_timestamp), '일 전')" +
                        "else timestampdiff(year , p.updatedAt, current_timestamp)" +
                        "end as updatedAt," +
                        "if(pl.status = 'ACTIVE', 'Y', 'N') as likeOrNot" +
                        "from Post as p" +
                        "join User as u on u.userIdx = p.userIdx" +
                        "left join (select postIdx, userIdx, count(postLikeidx) as postLikeCount from PostLike where status = 'ACTIVE'" +
                        "left join (select postIdx, count(commentIdx) as commentCount from Comment where status = 'ACTIVE'" +
                        "left join Follow as f on f.followeeIdx = p.userIdx and f.status = 'ACTIVE'" +
                        "left join PostLike as pl on pl.userIdx = f.followerIdx and pl.postIdx = p.postIdx" +
                        "where f.followerIdx = ? and p.status = 'ACTIVE'" +
                        "group by p.postIdx;";
        int selectPostsParam = userIdx;

        return this.jdbcTemplate.query(selectPostsQuery,
                (rs,rowNum) -> new GetPostsRes(
                        rs.getInt("postIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("nickName"),
                        rs.getString("profileImgUrl"),
                        rs.getString("content"),
                        rs.getInt("postLikeCount"),
                        rs.getInt("commentCount"),
                        rs.getString("updatedAt"),
                        rs.getString("likeOrNot"),
                        getPostImgRes = this.jdbcTemplate.query("\"select pi.postImgUrlIdx, pi.imgUrl +\n" +
                                "\" from PostImgUrl as pi\" +\n" +
                                "\" join Post as p on p.postIdx = pi.postIdx +\n" +
                                "\" where pi.status = 'ACTIVE' and p.postIdx = ?;\"",
                        (rk,rownum)-> new GetPostImgRes(
                                rk.getInt("postImgUrlIdx"),
                                rk.getString("imgUrl")
                        ),rs.getInt("postIdx")
                        )
                ),selectPostsParam);
    }

    public int checkUserExist(int userIdx){
        String checkUserExistQuery = "select exists(select userIdx from User where userIdx = ?)";
        int checkUserExistParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParams);
    }

    public int checkPostExist(int postIdx){
        String checkPostExistQuery = "select exists(select postIdx from Post where postIdx = ?)";
        int checkPostExistParams = postIdx;
        return this.jdbcTemplate.queryForObject(checkPostExistQuery,
                int.class,
                checkPostExistParams);
    }

    public int insertPosts(int userIdx,PostPostsReq postPostsReq){//String content
        String insertPostQuery = "insert into Post(userIdx,content) values(?,?)";
        Object []insertPostParams = new Object[] {userIdx,postPostsReq.getContent()};
        this.jdbcTemplate.update(insertPostQuery,
                insertPostParams);

        String lastInsertIdxQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdxQuery,int.class);
    }

    public int insertPostImgs(int postIdx,PostImgsUrlReq postImgsUrlReq){
        String insertPostImgsQuery = "insert into PostImgUrl(postIdx, imgUrl) values(?,?)";
        Object []insertPostImgsParams = new Object[] {postIdx,postImgsUrlReq.getImgurl()};
        this.jdbcTemplate.update(insertPostImgsQuery,
                insertPostImgsParams);

        String lastInsertIdxQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdxQuery,int.class);
    }

    public int updatePost(int postIdx,String content){
        String updatePostQuery = "update Post set content=? where postIdx=?";
        Object []updatePostParams = new Object[] {content,postIdx};
        return this.jdbcTemplate.update(updatePostQuery,updatePostParams);
    }

    public int deletePost(int postIdx){
        String deletePostQuery = "update Post set status='INACTIVE' where postIdx=?";
        Object []deletePostParams = new Object[] {postIdx};
        return this.jdbcTemplate.update(deletePostQuery,deletePostParams);
    }

}
