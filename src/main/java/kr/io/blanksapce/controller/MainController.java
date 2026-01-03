package kr.io.blanksapce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController
{
    // 메인
    @GetMapping("/")
    public String Main() { return "main"; }
}
