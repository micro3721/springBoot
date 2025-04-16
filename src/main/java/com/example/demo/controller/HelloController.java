package com.example.demo.controller; // 确保包名正确

import org.springframework.web.bind.annotation.CrossOrigin; // 1. 导入注解
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

// 2. 添加 @CrossOrigin 注解，允许来自 http://localhost:5173 的请求
//    注意：5173 是 Vite React 项目的默认端口，如果你的前端端口不同，请修改这里
@CrossOrigin(origins = "http://localhost:5173")
@RestController // 标记这个类是一个处理 RESTful 请求的 Controller
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        String s = "Hello, World! Welcome to Spring Boot!";
        return s;
    }

    @GetMapping("/")
    public String home() {
        return "This is the home page.";
    }

    @GetMapping("/sum")
    public String calculateSum() {
        int sum = 0;
        for (int i = 1; i <= 100; i++) {
            sum += i;
        }
        String result = "1到100的累加结果是: " + sum;
        return result;
    }

    @GetMapping("/bubblesort")
    public String bubbleSortDemo() {
        int[] numbers = {64, 34, 25, 12, 22, 11, 90};
        String originalArray = Arrays.toString(numbers);
        int n = numbers.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (numbers[j] > numbers[j + 1]) {
                    int temp = numbers[j];
                    numbers[j] = numbers[j + 1];
                    numbers[j + 1] = temp;
                }
            }
        }
        String sortedArray = Arrays.toString(numbers);
        // 返回包含 HTML <br> 标签的字符串
        return "Bubble Sort Demo:<br>" +
                "Original Array: " + originalArray + "<br>" +
                "Sorted Array: " + sortedArray;
    }

    private long calculateNthFibonacci(int n) {
        if (n <= 0) return 0;
        if (n == 1) return 1;
        long fibPrev = 0, fibCurr = 1;
        for (int i = 2; i <= n; i++) {
            long nextFib = fibPrev + fibCurr;
            fibPrev = fibCurr;
            fibCurr = nextFib;
        }
        return fibCurr;
    }

    @GetMapping("/fibonacci")
    public String demonstrateFibonacci() {
        int n = 10;
        long fibResult = calculateNthFibonacci(n);
        // 返回包含 HTML 标签的字符串
        String result = "<h2>经典算法演示: 斐波那契数列</h2>";
        result += "计算斐波那契数列的第 <b>" + n + "</b> 项 ( F(0)=0, F(1)=1 ... )<br><br>";
        result += "计算结果: <b>" + fibResult + "</b>";
        return result;
    }
}