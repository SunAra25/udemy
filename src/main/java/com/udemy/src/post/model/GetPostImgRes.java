package com.udemy.src.post.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetPostImgRes {
    private int postImgIdx;
    private String imgUrl;

}
