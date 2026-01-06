package kr.io.blankspace.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
public class ErrorPageController implements ErrorController {
    private static final Map<Integer, String> MESSAGE_MAP = Map.ofEntries(
        Map.entry(400, "요청이 올바르지 않습니다."),
        Map.entry(401, "로그인이 필요합니다."),
        Map.entry(403, "권한이 없습니다."),
        Map.entry(404, "요청하신 페이지를 찾을 수 없습니다."),
        Map.entry(405, "허용되지 않은 요청 방식입니다."),
        Map.entry(409, "요청이 충돌했습니다. 잠시 후 다시 시도해 주세요."),
        Map.entry(415, "지원하지 않는 형식의 요청입니다."),
        Map.entry(429, "요청이 너무 많습니다. 잠시 후 다시 시도해 주세요."),
        Map.entry(500, "서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요."),
        Map.entry(502, "서버 연결에 문제가 발생했습니다. 잠시 후 다시 시도해 주세요."),
        Map.entry(503, "현재 서비스를 이용할 수 없습니다. 잠시 후 다시 시도해 주세요.")
    );

    @RequestMapping("/error")
    public ModelAndView handleError(HttpServletRequest request, Model model) {
        Object statusObj = request.getAttribute("jakarta.servlet.error.status_code");

        Integer status = null;
        if (statusObj instanceof Integer i) status = i;
        if (statusObj instanceof String s)
        { try { status = Integer.parseInt(s); } catch (NumberFormatException ignored) {} }

        String code;
        String message;

        if (status == null) { code = "?"; message = "알 수 없는 에러가 발생했습니다."; }
        else {
            code = String.valueOf(status);
            message = MESSAGE_MAP.getOrDefault(status, "알 수 없는 에러가 발생했습니다.");
        }

        ModelAndView mv = new ModelAndView("error");
        mv.addObject("code", code);
        mv.addObject("message", message);
        return mv;
    }
}