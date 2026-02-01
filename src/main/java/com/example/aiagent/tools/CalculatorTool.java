package com.example.aiagent.tools;

import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CalculatorTool {

    private static final Logger logger = LoggerFactory.getLogger(CalculatorTool.class);

    @Tool("Calculates the length of a string")
    public int stringLength(String s) {
        logger.info("Called stringLength with: {}", s);
        return s.length();
    }

    @Tool("Calculates the sum of two numbers")
    public double add(double a, double b) {
        logger.info("Called add with: {}, {}", a, b);
        return a + b;
    }

    @Tool("Calculates the square root of a number")
    public double sqrt(double x) {
        logger.info("Called sqrt with: {}", x);
        return Math.sqrt(x);
    }
}