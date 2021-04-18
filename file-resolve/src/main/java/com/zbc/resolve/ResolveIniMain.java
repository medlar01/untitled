package com.zbc.resolve;

import org.ini4j.Ini;

import java.net.URL;
import java.util.*;

public class ResolveIniMain {

    public static void main(String[] args) throws Exception {
        URL resource = ResolveIniMain.class.getResource("/schema.ini");
        Ini ini = new Ini();
        ini.load(resource);
        Map<String, String> names = ini.get("name");
        Map<String, String> relations = ini.get("relation");
        String sql = "SELECT * FROM t_order";
        String alias = names.get("t_order");
        sql += " AS " + alias;
        sql += joinSql("t_store", names, relations, alias);
        sql += joinSql("t_user", names, relations, alias);
        System.out.println("-----。>>> sql: " + sql);
    }

    private static String joinSql(String joinTable, Map<String, String> names, Map<String, String> relations, String mainAlias) {
        List<String> list1 = Arrays.asList("t_order", joinTable);
        Collections.sort(list1);
        String value = relations.get(String.join("|", list1));
        String[] split = value.split("[|]");
        int index = list1.indexOf("t_order");
        String alias = names.get(joinTable);
        return" LEFT JOIN " + joinTable + " AS " + alias +
                " ON " + mainAlias + "." + split[index] + " = " + alias + "." + split[index == 0 ? 1 : 0];
    }
}
