package com.nextsolution.kintai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.nextsolution.kintai") // <-- 全パッケージを明示的にスキャン
public class KintaiApplication {

    public static void main(String[] args) {
        SpringApplication.run(KintaiApplication.class, args);
    }
}