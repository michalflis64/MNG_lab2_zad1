import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.*;

public class CalculatorMainListener extends CalculatorBaseListener {

    Deque<Double> numbers = new ArrayDeque<>();

    @Override
    public void exitExpression(CalculatorParser.ExpressionContext ctx) {
        double value = numbers.pop();
        for (int i = 1; i < ctx.getChildCount(); i += 2) {
            String operator = ctx.getChild(i).getText();
            double nextValue = numbers.pop();
            if (operator.equals("+")) {
                value += nextValue;
            } else if (operator.equals("-")) {
                value -= nextValue;
            }
        }
        numbers.add(value);
        super.exitExpression(ctx);
    }

    @Override
    public void exitIntegralExpression(CalculatorParser.IntegralExpressionContext ctx) {
        if (ctx.MINUS() != null) {
            numbers.add((-1 * Double.valueOf(ctx.INT().toString())));
        } else if (ctx.expression() != null) {
            double value = getResult();
            numbers.add(value);
        } else {
            numbers.add(Double.valueOf(ctx.INT().toString()));
        }
        super.exitIntegralExpression(ctx);
    }


    public void exitMultiplicativeExpression(CalculatorParser.MultiplicativeExpressionContext ctx) {
        int numberOfOperators = ctx.DIV().size() + ctx.MULT().size();
        if (numberOfOperators > 0) {
            List<Double> tempList = getNumbersFromQueue(numberOfOperators + 1);
                double result = tempList.remove(0);
                for(int i = 1; i< ctx.getChildCount(); i +=2){
                    String operator = ctx.getChild(i).getText();
                if (operator.equals("/")) {
                    result /= tempList.remove(0);
                } else if (operator.equals("*")) {
                    result *= tempList.remove(0);
                }
            }
            numbers.add(result);
        }
        super.exitMultiplicativeExpression(ctx);
    }

    @Override
    public void exitPowerExpression(CalculatorParser.PowerExpressionContext ctx) {
        if (ctx.POW().size() != 0) {
            List<Double> tempList = getNumbersFromQueue(numbers.size() - 2);
            if (ctx.POW().size() != 0) {
                Double value = numbers.pop();
                Double exponent = numbers.pop();
                Double result = Math.pow(value, exponent);
                populateQueue(tempList);
                numbers.add(result);
            }
        }
        super.exitPowerExpression(ctx);
    }

    @Override
    public void exitSqrtExpression(CalculatorParser.SqrtExpressionContext ctx) {
        if (ctx.SQRT() != null) {
            List<Double> tempList = getNumbersFromQueue(numbers.size() - 1);
            if (ctx.SQRT() != null) {
                Double value = Math.sqrt(numbers.pop());
                populateQueue(tempList);
                numbers.add((double) value.intValue());
            }
        } else {
            super.exitSqrtExpression(ctx);
        }
    }

    private Double getResult() {
        return numbers.peek();
    }

    private List<Double> getNumbersFromQueue(int amount) {
        List<Double> tempList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            tempList.add(numbers.pop());
        }
        return tempList;
    }

    private void populateQueue(List<Double> listOfEntries) {
        listOfEntries.forEach(numbers::add);
    }


    public static void main(String[] args) throws Exception {
    //    double result = calc("7*4 + sqrt4  - 7 + 3^2");
    //    System.out.println("Result = " + result);
   //     double result2 = calc("2 + 4/2^2");
    //    System.out.println("Result = " + result2);
         double result3 = calc("2 + 3 * 6 / 3 * 3");
        System.out.println("Result = " + result3);
    //    double result4 = calc("2 + 3*2 + 2");
    //    System.out.println("Result = " + result4); ;
    }

    public static Double calc(String expression) {
        return calc(CharStreams.fromString(expression));
    }

    public static Double calc(CharStream charStream) {
        CalculatorLexer lexer = new CalculatorLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        CalculatorParser parser = new CalculatorParser(tokens);
        ParseTree tree = parser.expression();

        ParseTreeWalker walker = new ParseTreeWalker();
        CalculatorMainListener mainListener = new CalculatorMainListener();
        walker.walk(mainListener, tree);
        return mainListener.getResult();
    }
}


