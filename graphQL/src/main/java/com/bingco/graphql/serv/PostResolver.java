package com.bingco.graphql.serv;

import com.bingco.graphql.pojo.Post;
import com.bingco.graphql.pojo.User;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Service;

@Service
public class PostResolver implements GraphQLQueryResolver {
    public Post getPostById(int id) {
        System.out.println("getPostById");
        if(id != 1) return null;
        int a = 1/0;
        User user = new User(1, "Lisa", "Ohz.Lisa", "lisa@gmail.com", null);
        return new Post(1, "java doc", "openjdk jvm", "diary", user);
    }
}
