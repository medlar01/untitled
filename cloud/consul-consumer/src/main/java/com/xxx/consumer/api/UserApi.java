package com.xxx.consumer.api;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.xxx.consumer.pojo.User;
import com.xxx.consumer.vary.UserMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserApi implements GraphQLQueryResolver {
    @Autowired
    private RestTemplate restTemplate;

    public User findMyInfo() {
        @SuppressWarnings("rawtypes")
        ResponseEntity<HashMap> responseEntity = restTemplate.postForEntity("http://consul-provider/graphql",
                new HashMap<String, Serializable>() {{
                    put("operationName", null);
                    put("query", "query{ findMyInfo{ id age name motto } }");
                    put("variables", new HashMap<>());
                }}, HashMap.class);
        @SuppressWarnings("unchecked")
        HashMap<String, ?> map = responseEntity.getBody();
        System.out.println("请求结果: " + responseEntity.getStatusCode() + " , " + map);
        assert map != null;
        @SuppressWarnings("unchecked")
        User user = UserMap.INST.mapToUser((Map<String, Serializable>)
                ((Map<String, Serializable>) map.get("data")).get("findMyInfo"));
        return user;
    }
}
