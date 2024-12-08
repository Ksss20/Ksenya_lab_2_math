import java.util.*;

public class ExpressionCalc {

    public static String infixToRPN(String infixExpression) {
        StringBuilder output = new StringBuilder();
        Stack<String> operatorStack = new Stack<>();
        StringTokenizer tokenizer = new StringTokenizer(infixExpression, " +-*/()", true);
        boolean nextIsNegative = false;
        boolean previousIsOperation = true;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();

            if (isNumber(token) || isVariable(token)) {
                output.append(token).append(" ");
                if (nextIsNegative) {
                    output.append("~ "); // Унарный минус
                }
                previousIsOperation = false;
                nextIsNegative = false;
            } else if (nextIsNegative) {
                throw new RuntimeException("Incorrect expression");
            } else if (isOperator(token)) {
                if (!previousIsOperation) {
                    while (!operatorStack.isEmpty() && hasPrecedence(token, operatorStack.peek())) {
                        output.append(operatorStack.pop()).append(" ");
                    }
                    operatorStack.push(token);
                    previousIsOperation = true;
                } else if ("-".equals(token)) {
                    nextIsNegative = true;
                    previousIsOperation = false;
                } else {
                    throw new RuntimeException("Incorrect expression");
                }
            } else if ("(".equals(token)) {
                operatorStack.push("(");
            } else if (")".equals(token)) {
                while (!operatorStack.isEmpty() && !"(".equals(operatorStack.peek())) {
                    output.append(operatorStack.pop()).append(" ");
                }
                if (!operatorStack.isEmpty() && "(".equals(operatorStack.peek())) {
                    operatorStack.pop();
                }
            }

        }

        while (!operatorStack.isEmpty()) {
            output.append(operatorStack.pop()).append(" ");
        }

        return output.toString().trim();
    }

    private static boolean isNumber(String token) {
        return token.matches("-?\\d+(\\.\\d+)?");
    }

    private static boolean isOperator(String c) {
        c = c.trim();
        return c.length() == 1 && "+-*/".contains(c);
    }


    private static boolean isVariable(String token) {
        return token.matches("[a-zA-Z_][a-zA-Z0-9_]*");
    }

    private static boolean hasPrecedence(String op1, String op2) {
        if ("(".equals(op2) || ")".equals(op2)) {

            return false;
        }
        return (!"*".equals(op1) && !"/".equals(op1)) || (!"+".equals(op2) && !"-".equals(op2));
    }


    public static double evaluateExpression(String expression) {
        Map<String, Double> variables = new HashMap<>();
        String rpn = infixToRPN(expression);
        // System.out.println(rpn);
        expression = preprocessExpression(rpn, variables);
        // System.out.println(expression);
        return evaluate(expression, variables);
    }

    private static String preprocessExpression(String expression, Map<String, Double> variables) {
        Scanner scanner = new Scanner(expression);
        Scanner input = new Scanner(System.in);
        StringBuilder processedExpression = new StringBuilder();
        while (scanner.hasNext()) {
            String token = scanner.next();
            if (isVariable(token)) {
                if (!variables.containsKey(token)) {
                    System.out.println("Enter value for variable " + token + ":");
                    double value = Double.parseDouble(input.nextLine());
                    variables.put(token, value);
                }
                processedExpression.append(variables.get(token));
            } else {
                processedExpression.append(token);
            }
            processedExpression.append(' ');
        }
        return processedExpression.toString();
    }

    private static double evaluate(String expression, Map<String, Double> variables) {
        return evaluate(new Scanner(expression), variables);
    }

    private static double evaluate(Scanner scanner, Map<String, Double> variables) {
        Stack<Double> values = new Stack<>();

        while (scanner.hasNext()) {
            String token = scanner.next();
            if (token.matches("\\d+(\\.\\d+)?")) {
                values.push(Double.parseDouble(token));
            } else if (isVariable(token)) {
                if (!variables.containsKey(token)) {
                    throw new RuntimeException("Undefined variable: " + token);
                }
                values.push(variables.get(token));
            } else if (token.matches("[+\\-*/]")) {
                if (values.size() < 2) {
                    throw new RuntimeException("Incorrect expression");
                }
                values.push(applyOp(token, values.pop(), values.pop()));
            } else if ("~".equals(token)) {
                values.add(-values.pop());
            } else {
                throw new RuntimeException("Invalid token: " + token);
            }
        }

        if (values.size() != 1) {
            throw new RuntimeException("Invalid expression");
        }

        return values.pop();
    }


    private static double applyOp(String operation, double b, double a) {
        char op = operation.strip().charAt(0);
        return switch (op) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> {
                if (b == 0) {
                    throw new RuntimeException("Division by zero");
                }
                yield a / b;
            }
            default -> throw new RuntimeException("Invalid operator: " + op);
        };
    }
}
