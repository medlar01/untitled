package com.xxx.provider.api;

import com.xxx.provider.pojo.User;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Service;

@Service
public class UserApi implements GraphQLQueryResolver {
    public User findMyInfo() {
        return new User(100L, 25, "LiBai", "举头望明月，低头思故乡。");
    }
}
