import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Это программа-парсер математических выражений. Она умеет:");
        System.out.println("- Проверять корректность введенного выражения;\n" +
                "- Предлагает ввести значение всех переменных, которые присутствуют в выражении;\n" +
                "- Вычисляет результат выражения.");
        System.out.println("* В текущей версии не поддерживаются пробелы, скобки и вещественные числа (с плавающей точкой).");
        System.out.println();
        String str;
        while (true) {
            System.out.println("Введите математическое выражение или 0 для выхода:");
            str = sc.nextLine();
            if (str.equals("0")) {
                return;
            }
            else {
                Parser.parse(sc, str);
            }
        }
        //Parser.parse(sc, "x1+x2+27-x3*x5/x1+x6*myComplexVar1");
        //Parser.parse(sc, "7-2*3");
        //Parser.parse(sc, "5*6+2-9");
        //Parser.parse(sc,"17-5*6/3-2+4/2");
        //Parser.parse(sc, "a+b*c*d");
    }
}
