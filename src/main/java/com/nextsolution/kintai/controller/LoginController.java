package com.nextsolution.kintai.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api")
public class LoginController {
    private final JdbcTemplate jdbc;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public LoginController(JdbcTemplate jdbc){this.jdbc=jdbc;}

    @PostMapping("/login")
    public Map<String,Object> login(@RequestParam String username,@RequestParam String password){
        String t=LocalDateTime.now().format(fmt);
        if(password==null||password.length()<8){System.out.println("["+t+"] [ログイン失敗] パスワード条件未達: "+username);return Map.of("success",false,"message","パスワードは8文字以上で入力してください。");}
        try{
            List<Map<String,Object>> rows=jdbc.queryForList("SELECT u.user_id,u.employee_code,u.name,u.password,u.role,u.dept_id,u.manager_id,u.work_type_id,d.dept_name FROM users u LEFT JOIN department d ON u.dept_id=d.dept_id WHERE u.employee_code=?",username);
            if(rows.isEmpty()){System.out.println("["+t+"] [ログイン失敗] 存在しない社員番号: "+username);return Map.of("success",false,"message","社員番号が存在しません。");}
            Map<String,Object> u=rows.get(0); String hash=(String)u.get("password");
            if(hash==null || !encoder.matches(password,hash)){System.out.println("["+t+"] [ログイン失敗] パスワード不一致: "+username);return Map.of("success",false,"message","パスワードが一致しません。");}
            u.remove("password");
            u.put("userId",u.get("user_id")); u.put("employeeCode",u.get("employee_code")); u.put("department",u.get("dept_name"));
            System.out.println("["+t+"] [ログイン成功] "+u.get("name")+"（社員番号:"+username+"、権限:"+u.get("role")+"）がログインしました。");
            return Map.of("success",true,"message","ログインしました。","user",u);
        }catch(Exception e){System.out.println("["+t+"] [ログインエラー] "+e.getMessage());return Map.of("success",false,"message","サーバー内部エラーが発生しました。");}
    }

    @PostMapping("/logout")
    public Map<String,Object> logout(@RequestParam(required=false) String username){
        String t=LocalDateTime.now().format(fmt); System.out.println("["+t+"] [ログアウト] 社員番号:"+(username==null?"不明":username)+" がログアウトしました。");
        return Map.of("success",true,"message","ログアウトしました。");
    }
}
