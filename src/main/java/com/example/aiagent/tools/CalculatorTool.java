package com.example.aiagent.tools;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class CalculatorTool {

    @Tool("Calculates the length of a string")
    public int stringLength(String s) {
        System.out.println("Called stringLength with: " + s);
        return s.length();
    }

    @Tool("Calculates the sum of two numbers")
    public double add(double a, double b) {
        System.out.println("Called add with: " + a + ", " + b);
        return a + b;
    }

    @Tool("Calculates the square root of a number")
    public double sqrt(double x) {
        System.out.println("Called sqrt with: " + x);
        return Math.sqrt(x);
    }
}
