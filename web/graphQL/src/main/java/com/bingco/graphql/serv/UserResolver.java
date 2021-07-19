package com.bingco.graphql.serv;

import com.bingco.graphql.pojo.Post;
import com.bingco.graphql.pojo.User;
import graphql.com.google.common.collect.Lists;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
class UserResolver implements GraphQLQueryResolver {
    private final List<User> userList = Lists.newArrayList();

    public User getUserById(int id) {
        System.out.println("getUserById");
        return userList.stream()
                .filter(item -> item.getUserId() == id)
                .findAny()
                .orElse(null)
            ;
    }

    @PostConstruct
    public void init() {
        Post post1 = new Post(1,"Hello,Graphql1","Graphql初体验1","日记", null);
        Post post2 = new Post(2,"Hello,Graphql2","Graphql初体验2","日记", null);
        Post post3 = new Post(3,"Hello,Graphql3","Graphql初体验3","日记", null);
        List<Post> posts = Lists.newArrayList(post1,post2,post3);

        User user1 = new User(1,"zhangsan","张三","zhangsan@qq.com", posts);
        User user2 = new User(2,"lisi","李四","lisi@qq.com", posts);

        userList.add(user1);
        userList.add(user2);
    }
}
