import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;
import java.util.Stack;

/**
 *@author 黎钟俊
 *@date 2023/3/23 10:28
 *@version 1.0
 *@description 题目：写一个计算器类（src.main.java.Calculator），可以实现两个数的加、减、乘、除运算，
 * 并可以进行undo和redo操作，侯选人可在实现功能的基础上发挥最优设计，编写后请提供github地址
 */
public class Calculator {

    /**
     * 历史操作数
     */
    private final static Stack<BigDecimal> PRE_NUMBER_STACK = new Stack<>();
    /**
     * 历史操作符
     */
    private final static Stack<String> PRE_FU_HAO_STACK = new Stack<>();
    /**
     * 历史操作结果
     */
    private final static Stack<BigDecimal> PRE_RESULT_STACK = new Stack<>();

    /**
     * 重做操作数
     */
    private final static Stack<BigDecimal> REDO_NUMBER_STACK = new Stack<>();
    /**
     * 重做操作结果
     */
    private final static Stack<BigDecimal> REDO_RESULT_STACK = new Stack<>();
    /**
     * 重做操作符
     */
    private final static Stack<String> REDO_FU_HAO_STACK = new Stack<>();
    /**
     * 第一位操作数
     */
    private static BigDecimal preNum;
    /**
     * 第二位操作数
     */
    private static BigDecimal newNum;
    /**
     * 当前操作符
     */
    private static String curFuHao;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        //每一行输入操作符或者操作数
        /**
         * 示例
         * 5
         * +
         * 3
         * =
         * 5 + 3 = 8
         */
        while (sc.hasNextLine()) {
            String input = sc.nextLine();
            switch (input) {
                case "+":
                case "-":
                case "*":
                case "/":
                    process(input);
                    break;
                case "=":
                    //进行计算
                    cal();
                    break;
                case "undo":
                    undoProcess();
                    break;
                case "redo":
                    redoProcess();
                    break;
                default:
                    processNumber(input);
                    break;
            }
        }
    }

    /**
     * 符号的处理
     * @param input 符号
     */
    private static void process(String input) {
        PRE_FU_HAO_STACK.push(input);
        curFuHao = input;
        //清除redo
        if (!REDO_NUMBER_STACK.isEmpty()) {
            REDO_RESULT_STACK.clear();
            REDO_FU_HAO_STACK.clear();
            REDO_NUMBER_STACK.clear();
        }
    }

    /**
     * 重做处理
     */
    private static void redoProcess() {
        if (REDO_NUMBER_STACK.isEmpty() || REDO_FU_HAO_STACK.isEmpty() || newNum != null) {
            System.out.println("暂没有可重做的操作!");
            return;
        }
        BigDecimal preResult = PRE_RESULT_STACK.peek();
        BigDecimal redoNumber = REDO_NUMBER_STACK.pop();
        String redoFuHao = REDO_FU_HAO_STACK.pop();
        PRE_NUMBER_STACK.push(redoNumber);
        PRE_FU_HAO_STACK.push(redoFuHao);
        //当前结果
        preNum = REDO_RESULT_STACK.pop();
        PRE_RESULT_STACK.push(preNum);
        System.out.println(
            String.format(
                "redo前结果为:%s,redo的操作为:%s,redo的操作数为:%s,redo后结果为:%s",
                preResult,
                redoFuHao,
                redoNumber,
                preNum
            )
        );
    }

    /**
     * 撤销处理
     */
    private static void undoProcess() {
        if (PRE_FU_HAO_STACK.isEmpty() || PRE_NUMBER_STACK.isEmpty()) {
            System.out.println("暂没有可撤销的操作!");
            return;
        }
        BigDecimal preNumber = PRE_NUMBER_STACK.pop();
        String preFuHao = PRE_FU_HAO_STACK.pop();
        REDO_NUMBER_STACK.push(preNumber);
        REDO_FU_HAO_STACK.push(preFuHao);
        //已经输入了等号
        BigDecimal preResult = PRE_RESULT_STACK.pop();
        if (newNum == null) {
            //先去除当前结果
            REDO_RESULT_STACK.push(preResult);
            preNum = PRE_RESULT_STACK.isEmpty() ? BigDecimal.ZERO : PRE_RESULT_STACK.peek();
        }
        System.out.println(
            String.format(
                "undo前结果为:%s,undo的操作为:%s,undo的操作数为:%s,undo后结果为:%s",
                preResult,
                preFuHao,
                preNumber,
                preNum
            )
        );
    }

    /**
     * 操作数处理
     * @param input 操作数
     */
    private static void processNumber(String input) {
        BigDecimal inputDecimal = null;
        try {
            inputDecimal = new BigDecimal(input);
        } catch (Exception e) {
            System.out.println("不支持该类型操作！");
        }
        PRE_NUMBER_STACK.push(inputDecimal);
        if (preNum == null) {
            preNum = inputDecimal;
            PRE_RESULT_STACK.push(inputDecimal);
        } else {
            newNum = inputDecimal;
        }
    }

    /**
     * 计算
     */
    private static void cal() {
        if (curFuHao == null || preNum == null || newNum == null) {
            System.out.println("不支持该类型操作！");
            return;
        }
        BigDecimal result = computeAndDisplay();
        preNum = result;
        newNum = null;
        curFuHao = null;
        PRE_RESULT_STACK.push(result);
    }

    /**
     * 计算并展示
     * @return 计算的结果
     */
    private static BigDecimal computeAndDisplay() {
        BigDecimal result;
        switch (curFuHao) {
            case "+":
                result = preNum.add(newNum);
                System.out.println(String.format("%s + %s = %s", preNum, newNum, result));
                break;
            case "-":
                result = preNum.subtract(newNum);
                System.out.println(String.format("%s - %s = %s", preNum, newNum, result));
                break;
            case "*":
                result = preNum.multiply(newNum);
                System.out.println(String.format("%s * %s = %s", preNum, newNum, result));
                break;
            case "/":
                result = preNum.divide(newNum, 2, RoundingMode.HALF_UP);
                System.out.println(String.format("%s / %s = %s", preNum, newNum, result));
                break;
            default:
                result = BigDecimal.ZERO;
                break;
        }
        return result;
    }
}
