import java.util.*;

public class Parser {
    static void parse(Scanner sc, String str) {
        if (str.length() != 0) {
            ArrayList<String> arrayList = new ArrayList<>(); //лист для хранения операторов и операндов
            HashMap<String, Integer> values;

            States currentState = States.BEGIN_STATE;
            States nextState = States.BEGIN_STATE;
            int i = 0;
            char[] chars = str.toCharArray();
            StringBuilder varBuilder = new StringBuilder();
            //разбиваем строку на лист с отдельными элементами (операндами и операторами)
            //может вынести проверку последнего элемента за цикл и тогда добавлять остаток значения varBuilder (если он не пустой) в массив?
            for (; i < chars.length; i++) {
                char c = chars[i];
                switch (currentState) {
                    case BEGIN_STATE:
                        if (Character.isLetter(c)) {
                            nextState = States.EXP_VAR;
                            varBuilder.append(c);
                        } else if (Character.isDigit(c)) {
                            nextState = States.EXP_NUM;
                            varBuilder.append(c);
                        } else {
                            nextState = States.ERROR;
                        }
                        if (i == chars.length - 1) {
                            arrayList.add(varBuilder.toString());
                        }
                        break;
                    case EXP_VAR:
                        if (Character.isLetterOrDigit(c)) {
                            varBuilder.append(c);
                            if (i == chars.length - 1) {
                                arrayList.add(varBuilder.toString());
                            }
                        } else if (isOperator(c)) {
                            arrayList.add(varBuilder.toString());
                            arrayList.add(String.valueOf(c));
                            varBuilder.setLength(0);
                            nextState = States.BEGIN_STATE;
                        } else {
                            nextState = States.ERROR;
                        }
                        break;
                    case EXP_NUM:
                        if (Character.isDigit(c)) {
                            varBuilder.append(c);
                            if (i == chars.length - 1) {
                                arrayList.add(varBuilder.toString());
                            }
                        } else if (isOperator(c)) {
                            arrayList.add(varBuilder.toString());
                            arrayList.add(String.valueOf(c));
                            varBuilder.setLength(0);
                            nextState = States.BEGIN_STATE;
                        } else {
                            nextState = States.ERROR;
                        }
                        break;
                    default:
                        nextState = States.ERROR;
                }
                currentState = nextState;
                if (currentState == States.ERROR) {
                    break;
                }
            }
            System.out.println(arrayList.toString());
            //заполняем хэш-таблицу путем ввода значений переменных с консоли
            values = storeVars(sc, arrayList);
            //меняем переменные на их значения
            changeVarsForNums(arrayList, values);
            //переводим инфиксную нотацию в постфиксную
            arrayList = infixToPostfix(arrayList);
            //вычисляем выражение в постфиксной форме
            calculateInPostfix(arrayList);

        } else {
            System.out.println("Введенная строка пуста!");
        }
    }

    private static HashMap<String, Integer> storeVars(Scanner sc, ArrayList<String> operands) {
        HashMap<String, Integer> varsNumbers = new HashMap<>();
        int num;
        for (String operand : operands) {
            if (!varsNumbers.containsKey(operand) & !isInteger(operand) & !isOperator(operand.charAt(0))) {
                System.out.println("Введите значение переменной " + operand + ":");
                num = sc.nextInt();
                varsNumbers.put(operand, num);
            }
        }
        varsNumbers.forEach((k, v) -> System.out.println("Key: " + k + ": Value: " + v));
        return varsNumbers;
    }

    private static boolean isOperator(char c) {
        //return c == '+' || c == '-' || c == '/' || c == '*';
        switch (c) {
            case '+':
            case '-':
            case '*':
            case '/':
                return true;
            default:
                return false;
        }
    }

    private static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /* будет добавлено при доработке
    private static boolean isBracket(char c) {
        return c == '(' || c == ')';
    }*/

    private static void changeVarsForNums(ArrayList<String> arrayList, HashMap<String, Integer> values) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (values.containsKey(arrayList.get(i))) {
                String newVal = values.get(arrayList.get(i)).toString();
                arrayList.set(i, newVal);
            }
        }
        System.out.println(arrayList.toString());
    }

    private static ArrayList<String> infixToPostfix(ArrayList<String> infix) {
        ArrayList<String> postfix = new ArrayList<>();
        ArrayDeque<String> operators = new ArrayDeque<>();
        for (String s : infix) {
            if (isInteger(s)) {
                postfix.add(s);
            } else if (isOperator(s.charAt(0))) {
                while (!operators.isEmpty() && hasHigherPriority(operators.peek(), s)) {
                    postfix.add(operators.peek());
                    operators.pop();
                }
                operators.push(s);
            }
        }
        while (!operators.isEmpty()) {
            postfix.add(operators.peek());
            operators.pop();
        }
        System.out.println(postfix.toString());
        return postfix;
    }

    private static boolean hasHigherPriority(String stackOp, String newOp) {
        return precedenceLevel(stackOp) > precedenceLevel(newOp);
    }

    private static int precedenceLevel(String op) {
        switch (op) {
            case "+":
            case "-":
                return 0;
            case "*":
            case "/":
                return 1;
            default:
                throw new IllegalArgumentException("Неизвестный оператор: " + op);
        }
    }

    private static int calculateInPostfix(ArrayList<String> arrayList) {
        ArrayDeque<Integer> stack = new ArrayDeque<>();
        int res, op1, op2;
        for (String s : arrayList) {
            if (isInteger(s)) {
                stack.push(Integer.parseInt(s));
            } else {
                switch (s) {
                    case "+":
                        op2 = stack.pop();
                        op1 = stack.pop();
                        res = op1 + op2;
                        stack.push(res);
                        break;
                    case "-":
                        op2 = stack.pop();
                        op1 = stack.pop();
                        res = op1 - op2;
                        stack.push(res);
                        break;
                    case "*":
                        op2 = stack.pop();
                        op1 = stack.pop();
                        res = op1 * op2;
                        stack.push(res);
                        break;
                    case "/":
                        op2 = stack.pop();
                        op1 = stack.pop();
                        res = op1 / op2;
                        stack.push(res);
                        break;
                }
            }
        }
        System.out.println(stack.peek());
        return stack.pop();
    }
}
