package com.norman.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class function {
    private final static BigDecimal PI = new BigDecimal("3.14159265358979323846264338327950288419716939937510" +
            "58209749445923078164062862089986280348253421170679");//π后面100位
    private final static BigDecimal E = new BigDecimal("2.71828182845904523536028747135266249775724709369995" +
            "95749669676277240766303535475945713821785251664274");//e后面100位

    /**
     * 字符串中出现子字符串的次数
     *
     * @param str     字符串
     * @param sToFind 子字符串
     * @return 个数
     */
    public static int countStr(String str, String sToFind) {
        if (str == null) return 0;
        int num = 0;
        while (str.contains(sToFind)) {
            str = str.substring(str.indexOf(sToFind) + sToFind.length());
            num++;
        }
        return num;
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue   sp值
     * @param fontScale DisplayMetrics类中属性scaledDensity
     * @return pix值
     */
    public static float sp2px(float spValue, float fontScale) {
        return (spValue * fontScale + 0.5f);
    }

    /**
     * 判断一个表达式是不是有括号的简单表达式
     */
    @SuppressWarnings({"ConstantConditions", "MismatchedReadAndWriteOfArray"})
    public static boolean isExpression(String str) {
        boolean pass = true;
        //表达式是否合法
        Pattern rightExpression = Pattern.compile("^(\\(*(-?[0-9]+\\.?[0-9]*)\\)*[＋－×÷^])*" +
                "(\\(*(\\(*-?[0-9]+\\.?[0-9]*))\\)*$");//正确表达式（有Bug）
        Pattern[] wrongExpression = {
        };//补丁式更正
        Matcher rightMather = rightExpression.matcher(str);
        Matcher[] wrongMatcher = new Matcher[wrongExpression.length];
        //错误匹配
        for (int i = 0; i < wrongExpression.length; i++) {
            wrongMatcher[i] = wrongExpression[i].matcher(str);
            pass &= !wrongMatcher[i].find();//只要匹配了一个补丁就会不通过
            if (!pass) break;//提前停止查找以节省资源
            wrongMatcher[i].reset();
        }
        //正确匹配
        pass &= rightMather.find();
        rightMather.reset();
        return pass;
    }

    /**
     * 判断一个表达式是不是复杂表达式
     *
     * @param str 表达式
     * @return 结果
     */
    @SuppressWarnings({"ConstantConditions", "MismatchedReadAndWriteOfArray"})
    public static boolean isComplexExpression(String str) {
        boolean pass = true;
        if (isExpression(str)) return true;
        //表达式是否合法
        Pattern rightExpression = Pattern.compile("^(\\(*(((arc)?(sin\\(|cos\\(|tan\\())|(ln\\(|lg\\())*√*((-?[0-9]+\\.?[0-9]*)|e|π)!*%*\\)*[＋－×÷^])*" +
                "(\\(*(((arc)?(sin\\(|cos\\(|tan\\())|(ln\\(|lg\\())?√*(\\(*-?[0-9]+\\.?[0-9]*|e|π)!*%*)\\)*$");//正确表达式（有Bug）
        Pattern[] wrongExpression = {
        };//补丁式更正
        Matcher rightMather = rightExpression.matcher(str);
        Matcher[] wrongMatcher = new Matcher[wrongExpression.length];
        //错误匹配
        for (int i = 0; i < wrongExpression.length; i++) {
            wrongMatcher[i] = wrongExpression[i].matcher(str);
            pass &= !wrongMatcher[i].find();//只要匹配了一个补丁就会不通过
            if (!pass) break;//提前停止查找以节省资源
            wrongMatcher[i].reset();
        }
        //正确匹配
        pass &= rightMather.find();
        rightMather.reset();
        return pass;
    }

    /**
     * 表达式计算——高级
     *
     * @param calc 输入
     * @return 结果字符串
     */
    public static String evaluateAdvanced(String calc, Locale locale, String Units) {
        while (!(isSimpleExpression(calc) | isDigit(calc))) {
            Pattern pattern;
            Matcher matcher;
            //圆周率、自然常数
            while (calc.contains("π") | calc.contains("e")) {
                calc = calc.replace("π", PI.toPlainString());
                calc = calc.replace("e", E.toPlainString());
            }
            //阶乘
            while (calc.contains("!")) {
                pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*!");
                matcher = pattern.matcher(calc);
                if (matcher.find()) {
                    BigDecimal base = new BigDecimal(Objects.requireNonNull(matcher.group(0)).replace("!", ""));
                    BigDecimal fact = BigDecimal.ONE;
                    if (isInteger(base)) {
                        if (moreThan(base, BigDecimal.ZERO)) {
                            for (BigDecimal i = new BigDecimal("2"); i.compareTo(base) <= 0; i = i.add(BigDecimal.ONE)) {
                                fact = fact.multiply(i);
                            }
                        } else {
                            return "FACTORIAL_NEGATIVES_ERROR";
                        }
                    } else {
                        return "FACTORIAL_DECIMAL_ERROR";
                    }
                    calc = calc.substring(0, matcher.start()) + fact.toPlainString() + calc.substring(matcher.end());
                    matcher.reset();
                } else
                    break;
            }
            //开方
            while (calc.contains("√")) {
                pattern = Pattern.compile("√(-?[0-9]+\\.?[0-9]*)");
                matcher = pattern.matcher(calc);
                if (matcher.find()) {
                    BigDecimal base = new BigDecimal(matcher.group(1));
                    if (lessThan(base, BigDecimal.ZERO))
                        return "SQUARE_ROOT_NEGATIVES_ERROR";
                    calc = calc.substring(0, matcher.start()) + Math.sqrt(base.doubleValue()) + calc.substring(matcher.end());
                    matcher.reset();
                } else
                    break;
            }
            //百分号
            while (calc.contains("%")) {
                pattern = Pattern.compile("(-?[0-9]+\\.?[0-9]*)%");
                matcher = pattern.matcher(calc);
                if (matcher.find()) {
                    BigDecimal base = new BigDecimal(matcher.group(1));
                    calc = calc.substring(0, matcher.start()) + base.movePointLeft(2).toPlainString() + calc.substring(matcher.end());
                    matcher.reset();
                } else
                    break;
            }
            //幂
            while (calc.contains("^")) {
                pattern = Pattern.compile("(-?[0-9]+\\.?[0-9]*)\\^(-?[0-9]+\\.?[0-9]*)");
                matcher = pattern.matcher(calc);
                if (matcher.find()) {
                    BigDecimal base = new BigDecimal(matcher.group(1)), index = new BigDecimal(matcher.group(2));
                    Double ans = Math.pow(base.doubleValue(), index.doubleValue());
                    calc = calc.substring(0, matcher.start()) + String.format(locale, "%.0f", ans) + calc.substring(matcher.end());
                    matcher.reset();
                } else
                    break;
            }
            //反三角函数
            while (calc.contains("arcsin") | calc.contains("arccos") | calc.contains("arctan")) {
                pattern = Pattern.compile("arc(sin|cos|tan)(-?[0-9]+\\.?[0-9]*)");
                matcher = pattern.matcher(calc);
                if (matcher.find()) {
                    BigDecimal opnd = new BigDecimal(matcher.group(2));
                    BigDecimal ans = new BigDecimal("0");
                    switch (Objects.requireNonNull(matcher.group(1))) {
                        case "sin": {
                            if (moreThan(opnd, BigDecimal.ONE) | lessThan(opnd, BigDecimal.ONE.negate()))
                                return "INVERSE_TRIANGLE_SIN_OUT_OF_RANGE_ERROR";
                            ans = BigDecimal.valueOf(Math.asin(opnd.doubleValue()));
                            break;
                        }
                        case "cos": {
                            if (moreThan(opnd, BigDecimal.ONE) | lessThan(opnd, BigDecimal.ONE.negate()))
                                return "INVERSE_TRIANGLE_COS_OUT_OF_RANGE_ERROR";
                            ans = BigDecimal.valueOf(Math.acos(opnd.doubleValue()));
                            break;
                        }
                        case "tan": {
                            ans = BigDecimal.valueOf(Math.atan(opnd.doubleValue()));
                            break;
                        }
                    }
                    if (Units.equals("Degree"))
                        ans = BigDecimal.valueOf(Math.toDegrees(ans.doubleValue()));
                    calc = calc.substring(0, matcher.start()) + ans.toPlainString() + calc.substring(matcher.end());
                    matcher.reset();
                } else
                    break;
            }
            //三角函数
            while (calc.contains("sin") | calc.contains("cos") | calc.contains("tan")) {
                pattern = Pattern.compile("(sin|cos|tan)(-?[0-9]+\\.?[0-9]*)");
                matcher = pattern.matcher(calc);
                if (matcher.find()) {
                    BigDecimal opnd = new BigDecimal(matcher.group(2));
                    if (Units.equals("Degree"))
                        opnd = BigDecimal.valueOf(Math.toRadians(opnd.doubleValue()));
                    BigDecimal ans = new BigDecimal("0");
                    switch (Objects.requireNonNull(matcher.group(1))) {
                        case "sin": {
                            ans = BigDecimal.valueOf(Math.sin(opnd.doubleValue()));
                            break;
                        }
                        case "cos": {
                            ans = BigDecimal.valueOf(Math.cos(opnd.doubleValue()));
                            break;
                        }
                        case "tan": {
                            if (isInteger(opnd.multiply(BigDecimal.valueOf(Math.PI)).add(BigDecimal.valueOf(0.5f))))
                                return "TAN_OUT_OF_RANGE_ERROR";
                            ans = BigDecimal.valueOf(Math.tan(opnd.doubleValue()));
                            break;
                        }
                    }
                    calc = calc.substring(0, matcher.start()) + ans.toPlainString() + calc.substring(matcher.end());
                    matcher.reset();
                } else
                    break;
            }
            //对数
            while (calc.contains("ln") | calc.contains("lg")) {
                pattern = Pattern.compile("(ln|lg)(-?[0-9]+\\.?[0-9]*)");
                matcher = pattern.matcher(calc);
                if (matcher.find()) {
                    BigDecimal opnd = new BigDecimal(matcher.group(2));
                    if (opnd.compareTo(BigDecimal.ZERO) <= 0)
                        return "LOGARITHM_OUT_OF_RANGE_ERROR";
                    BigDecimal ans = new BigDecimal("0");
                    switch (Objects.requireNonNull(matcher.group(1))) {
                        case "ln": {
                            ans = BigDecimal.valueOf(Math.log(opnd.doubleValue()));
                            break;
                        }
                        case "lg": {
                            ans = BigDecimal.valueOf(Math.log10(opnd.doubleValue()));
                            break;
                        }
                    }
                    calc = calc.substring(0, matcher.start()) + ans.toPlainString() + calc.substring(matcher.end());
                    matcher.reset();
                } else
                    break;
            }
        }
        if (isDigit(calc))
            return calc;
        return evaluatePrime(calc);
    }

    /**
     * 判断一个表达式是不是无括号的简单表达式
     *
     * @param str 需要判断的字符串
     * @return 结果
     */
    public static boolean isSimpleExpression(String str) {
        if (str == null) return false;
        return str.matches("^(-?[0-9]+\\.?[0-9]*[＋－×÷])*-?[0-9]+\\.?[0-9]*$");
    }

    /**
     * 判断一个字符串是不是数字
     *
     * @param str 需要判断的字符串
     * @return 结果
     */
    public static boolean isDigit(String str) {
        if (str == null) return false;
        return str.matches("^-?[0-9]+\\.?[0-9]*$");
    }

    /**
     * 判断一个BigDecimal是不是整数
     *
     * @param num 判断的数字
     * @return 结果
     */
    public static boolean isInteger(BigDecimal num) {
        if (num == null)
            return false;
        return num.compareTo(new BigDecimal(num.toBigInteger().toString())) == 0;//此处不适用BigDecimal.equal();
    }

    /**
     * 两个BigDecimal的数字谁更小
     *
     * @param numBefore 前面一个数
     * @param numAfter  后边一个数
     * @return 结果
     */
    public static boolean moreThan(BigDecimal numBefore, BigDecimal numAfter) {
        if (numBefore == null) return false;
        return numBefore.compareTo(numAfter) > 0;
    }

    /**
     * 两个BigDecimal的数字谁更大
     *
     * @param numBefore 前面一个数
     * @param numAfter  后边一个数
     * @return 结果
     */
    public static boolean lessThan(BigDecimal numBefore, BigDecimal numAfter) {
        if (numBefore == null) return false;
        return numBefore.compareTo(numAfter) < 0;
    }

    /**
     * 表达式计算——初级
     *
     * @param calc 输入
     * @return 结果字符串
     */
    public static String evaluatePrime(String calc) {
        StringBuilder str = new StringBuilder(calc);
        Stack<BigDecimal> opnd = new Stack<>();
        Stack<Character> optr = new Stack<>();
        //运算符优先级映射表
        Map<Character, Integer> prior = new HashMap<>();
        prior.put('\0', Integer.valueOf("100"));
        prior.put('×', Integer.valueOf("1"));
        prior.put('÷', Integer.valueOf("1"));
        prior.put('＋', Integer.valueOf("2"));
        prior.put('－', Integer.valueOf("2"));
        optr.push('\0');
        //运算符栈堆为空表示计算结束
        while (!optr.empty()) {
            //当前字符为str初字符
            char nowChar = (str.length() != 0) ? str.charAt(0) : '\0';
            //字符为操作数的一部分
            if ((nowChar <= '9' & nowChar >= '0') | nowChar == '.' | nowChar == '-') {
                StringBuilder temp = new StringBuilder();
                do {
                    temp.append(nowChar);
                    str.deleteCharAt(0);
                    if (str.length() != 0) {
                        nowChar = str.charAt(0);
                    } else break;
                } while ((nowChar <= '9' & nowChar >= '0') | nowChar == '.' | nowChar == '-');
                opnd.push(new BigDecimal(temp.toString()));
            }
            //字符为运算符的一部分
            else {
                //当前运算符优先级高于栈顶优先级，压栈
                if (Objects.requireNonNull(prior.get(nowChar)) < Objects.requireNonNull(prior.get(optr.peek()))) {
                    optr.push(nowChar);
                    str.deleteCharAt(0);
                }
                //结束使用到
                else if (Objects.equals(prior.get(nowChar), prior.get(optr.peek())) & nowChar == '\0') {
                    optr.pop();
                    break;
                }
                //进行运算
                else {
                    //弹出栈顶运算符
                    char opLocal = optr.pop();
                    //按运算符类型分类
                    switch (opLocal) {
                        case '^': {
                            BigDecimal num2 = opnd.pop(), num1 = opnd.pop();
                            opnd.push(BigDecimal.valueOf(Math.pow(num1.doubleValue(), num2.doubleValue())));
                            break;
                        }
                        //加法
                        case '＋': {
                            BigDecimal num2 = opnd.pop(), num1 = opnd.pop();
                            opnd.push(num1.add(num2));
                            break;
                        }
                        //减法
                        case '－': {
                            BigDecimal num2 = opnd.pop(), num1 = opnd.pop();
                            opnd.push(num1.subtract(num2));
                            break;
                        }
                        //乘法
                        case '×': {
                            BigDecimal num1 = opnd.pop(), num2 = opnd.pop();
                            opnd.push(num1.multiply(num2));
                            break;
                        }
                        //除法
                        case '÷': {
                            BigDecimal num2 = opnd.pop(), num1 = opnd.pop();
                            if (!num2.equals(BigDecimal.ZERO)) {
                                opnd.push(num1.divide(num2, 8, RoundingMode.HALF_UP));
                                break;
                            } else {
                                return "DIVISOR_ZERO_ERROR";
                            }
                        }
                        //未知的内部错误
                        default: {
                            return "UNEXPECTED_INTERNAL_ERROR";
                        }
                    }
                }
            }
        }
        return opnd.pop().toPlainString();
    }
}