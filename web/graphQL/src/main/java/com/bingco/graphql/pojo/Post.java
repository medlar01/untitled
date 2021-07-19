package com.bingco.graphql.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    private int postId;
    private String title;
    private String text;
    private String category;
    private User user;
}
