package com.nextsolution.kintai.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,String> badRequest(RuntimeException e){return Map.of("message",e.getMessage()==null?"リクエストを処理できません.":e.getMessage());}
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String,String> server(Exception e){return Map.of("message","サーバーエラーが発生しました.");}
}
