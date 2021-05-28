import java.util.*;

public class Parser {
    static void parse(Scanner sc, String str) {
        if (str.length() != 0) {
            //заносить объекты типа знак, число, переменная, а не строки
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
                        break;
                    case EXP_VAR:
                        if (Character.isLetterOrDigit(c)) {
                            varBuilder.append(c);
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
                    System.out.println("Некорректное математическое выражение! Ошибка в позиции "+ ++i);
                    return;
                }
            }
            if (varBuilder.length() != 0 && !isOperator(varBuilder.charAt(0))) {
                arrayList.add(varBuilder.toString());
            } else {
                System.out.println("Математическое выражение не может оканчиваться на знак операции!");
                return;
            }

            //заполняем хэш-таблицу путем ввода значений переменных с консоли
            values = storeVars(sc, arrayList);
            //меняем переменные на введенные пользователем числа
            changeVarsForNums(arrayList, values);
            //переводим инфиксную нотацию в постфиксную
            arrayList = infixToPostfix(arrayList);
            //вычисляем выражение в постфиксной форме
            calculateInPostfix(arrayList);

        } else {
            System.out.println("Введенная строка пуста!");
        }
        System.out.println();
    }

    private static HashMap<String, Integer> storeVars(Scanner sc, ArrayList<String> operands) {
        HashMap<String, Integer> varsNumbers = new HashMap<>();
        int num;
        for (String operand : operands) {
            if (!varsNumbers.containsKey(operand) & !isInteger(operand) & !isOperator(operand.charAt(0))) {
                System.out.println("Введите значение переменной " + operand + ":");
                num = sc.nextInt();
                sc.nextLine();
                varsNumbers.put(operand, num);
            }
        }
        return varsNumbers;
    }

    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '/' || c == '*';
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

    //метод с помощью которого переменные в ArrayList мы меняем на ранее введенные пользователем числа
    private static void changeVarsForNums(ArrayList<String> arrayList, HashMap<String, Integer> values) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (values.containsKey(arrayList.get(i))) {
                String newVal = values.get(arrayList.get(i)).toString();
                arrayList.set(i, newVal);
            }
        }
    }

    private static ArrayList<String> infixToPostfix(ArrayList<String> infix) {
        ArrayList<String> postfix = new ArrayList<>();
        ArrayDeque<String> operators = new ArrayDeque<>();
        // сделать свитч и выбирать тип
        for (String s : infix) {
            if (isInteger(s)) {
                postfix.add(s);
            } else {
                if (operators.isEmpty()) {
                    operators.add(s);
                } else if (hasHigherPriority(s, operators.peekLast())) {
                    operators.add(s);
                } else {
                    while (!operators.isEmpty() && !hasHigherPriority(s, operators.peekLast())) {
                        postfix.add(operators.removeLast());
                    }
                    operators.add(s);
                }
            }
        }

        while (!operators.isEmpty()) {
            postfix.add(operators.removeLast());
        }
        System.out.println("\nВведенное выражение в постфиксной форме:");
        System.out.println(postfix.toString() + "\n");

        return postfix;
    }

    private static boolean hasHigherPriority(String newOp, String stackOp) {
        return precedenceLevel(newOp) > precedenceLevel(stackOp);
    }

    //метод возвращает "вес" оператора
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

    private static void calculateInPostfix(ArrayList<String> arrayList) {
        ArrayDeque<Integer> stack = new ArrayDeque<>();
        int res, op1, op2;
        //цикл for необходим, поскольку в стэк поочередно подаются данные из ArrayList, начиная с первого
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
        System.out.println(stack.pop());
    }
}
