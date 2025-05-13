package sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sys.mapper.AdminMapper;
import sys.mapper.UserLoginMapper;
import sys.people.Admin;
import sys.people.UserLogin;
import sys.utils.JwtUtils;
import sys.utils.Result;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class EmployeeController {

    @Autowired
    private UserLoginMapper userLoginMapper;

    @Autowired
    private AdminMapper adminMapper;

    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String role = body.get("role");

        if (username == null || password == null || role == null) {
            return Result.error().code(40000).message("用户名、密码或角色不能为空");
        }

        QueryWrapper<?> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username).eq("password", password);

        boolean valid = false;
        if ("admin".equals(role)) {
            valid = adminMapper.selectOne((QueryWrapper<Admin>) wrapper) != null;
        } else if ("employee".equals(role)) {
            valid = userLoginMapper.selectOne((QueryWrapper<UserLogin>) wrapper) != null;
        }

        if (valid) {
            String token = JwtUtils.generateToken(username + ":" + role);
            Map<String, String> data = new HashMap<>();
            data.put("token", token);
            return Result.ok().data(data);
        } else {
            return Result.error().code(60204).message("账号或密码错误");
        }
    }

    @PostMapping("/info")
    public Result info(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        if (token == null || token.isEmpty()) {
            return Result.error().code(40001).message("Token不能为空");
        }

        String subject = JwtUtils.getClaimsByToken(token).getSubject();
        if (subject == null || !subject.contains(":")) {
            return Result.error().code(40002).message("无效的Token");
        }

        String[] parts = subject.split(":");
        String username = parts[0];
        String role = parts[1];

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("username", username);

        boolean exists = false;
        if ("admin".equals(role)) {
            exists = adminMapper.selectByMap(queryMap).size() > 0;
        } else if ("employee".equals(role)) {
            exists = userLoginMapper.selectByMap(queryMap).size() > 0;
        }

        if (exists) {
            return Result.ok().data("username", username).data("role", role);
        } else {
            return Result.error().code(60205).message("用户信息未找到");
        }
    }

    @PostMapping("/logout")
    public Result logout() {
        return Result.ok();
    }
}
