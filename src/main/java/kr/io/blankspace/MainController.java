package kr.io.blankspace;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController
{
    // 메인
    @GetMapping("/")
    public String main() { return "main"; }
}