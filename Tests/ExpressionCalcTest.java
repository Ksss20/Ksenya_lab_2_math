import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionCalcTest {

    private final InputStream originalIn = System.in;

    @Test
    public void testNoVariables() {
        List<Double> inputDouble = List.of();
        String expression = "1 + 1 / (-1)";
        double expectedResult = 0;

        checkResult(inputDouble, expression, expectedResult);
    }

    @Test
    public void testAllOperations() {
        List<Double> inputDouble = List.of(5d, 3d, 10d, 4d);
        String expression = "a + b * (c / d) - 1";
        double expectedResult = 11.5;

        checkResult(inputDouble, expression, expectedResult);
    }

    @Test
    public void testSameVariables() {
        List<Double> inputDouble = List.of(5d);
        String expression = "a + a * (a - a)";
        double expectedResult = 5.0;

        checkResult(inputDouble, expression, expectedResult);
    }

    @Test
    public void testLongVariable() {
        List<Double> inputDouble = List.of(1d, 2d, 3d, 4d, 5d);
        String expression = "a + b * c / d + theLastVariableWithValue5";
        double expectedResult = 7.5;

        checkResult(inputDouble, expression, expectedResult);
    }
    @Test
    public void testUnaryMinus() {
        List<Double> inputDouble = List.of(1d);
        String expression = "-a + (-5)";
        double expectedResult = -6;

        checkResult(inputDouble, expression, expectedResult);
    }

    @Test
    public void testDivZero() {
        List<Double> inputDouble = List.of(0d);
        String expression = "a + a * (a / a)";
        String expectedMessage = "Division by zero";

        checkResult(inputDouble, expression, expectedMessage);
    }

    @Test
    public void testIncorrectExpression() {
        List<Double> inputDouble = List.of(5d);
        String expression = "a + + a * (a / a)";
        String expectedMessage = "Incorrect expression";

        checkResult(inputDouble, expression, expectedMessage);
    }

    @Test
    public void testNotClosed() {
        List<Double> inputDouble = List.of(5d);
        String expression = "a + a * (a / a";
        String expectedMessage = "Invalid token: (";

        checkResult(inputDouble, expression, expectedMessage);
    }

    private static void checkResult(List<Double> inputDouble, String expression, double expected){
        System.out.println("Input: " + inputDouble);
        System.out.println(expression);

        System.setIn(inputDoubleToString(inputDouble));
        double result = ExpressionCalc.evaluateExpression(expression);

        System.out.println("result: " + result);
        assertEquals(expected, result, 0.001);
    }

    private static void checkResult(List<Double> inputDouble, String expression, String expectedMessage){
        System.out.println("Input: " + inputDouble);
        System.out.println(expression);

        System.setIn(inputDoubleToString(inputDouble));

        Exception thrown = assertThrows(Exception.class, () -> {
            ExpressionCalc.evaluateExpression(expression);
        });
        System.out.println(thrown.getMessage());
        assertEquals(expectedMessage, thrown.getMessage());
    }


    private static ByteArrayInputStream inputDoubleToString(List<Double> inputDouble) {
        StringBuilder builder = new StringBuilder();
        for (Double d : inputDouble) {
            builder.append(d.toString()).append('\n');
        }
        String toInput = builder.toString();
        return new ByteArrayInputStream(toInput.getBytes());
    }
}