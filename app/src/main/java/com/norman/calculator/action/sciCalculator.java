package com.norman.calculator.action;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.SuperscriptSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.norman.calculator.Function;
import com.norman.calculator.R;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Norman_Yi
 * @version 2.9.0
 */
public class sciCalculator extends Activity {
    //数字按钮
    private Button btn0, btn1, btn2, btn3, btn4, btn5,
            btn6, btn7, btn8, btn9, btnDot, btnMinus;
    //运算符按钮
    private Button btnAdd, btnMulti, btnDivide, btnNega, btnBracket;
    //功能键按钮
    private Button btnClear, btnBack, btnEqual, btnDegree, btnRadian;
    //菜单按钮
    private ImageButton btnMenu;
    //输入框和记录框
    private TextView inputWindow, appName;
    private RecyclerView recordWindow;
    private RecordAdapter RecyclerAdapter;
    private SharedPreferences Settings;
    //高级计算按钮
    private Button btnPi, btnSin, btnCos, btnTan, btnArc,
            btnSq, btnSqrt, btnPow, btnEPow, btnTenPow,
            btnPercent, btnFact,
            btnE, btnLn, btnLg;
    //输入框字符串 记录框列表 配置参数
    private String calc = "";
    private String Mode = "Scientific";
    private String Units = "Radian";
    private Locale locale = Locale.getDefault();
    private long firstClick;
    //反三角函数禁用按钮时用到
    @SuppressWarnings("ConstantConditions")
    private Button[] arcDisabledBtn = new Button[]{btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btnDot, btnMinus,
            btnAdd, btnMulti, btnDivide, btnNega, btnBracket, btnEqual, btnPi, btnSq, btnSqrt, btnPow,
            btnEPow, btnTenPow, btnPercent, btnFact, btnE, btnLn, btnLg};
    private final ColorStateList[] disabledBtnColor = new ColorStateList[arcDisabledBtn.length];

    /**
     * 程序入口
     *
     * @param savedInstanceState ？
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialSci();
    }

    /**
     * 单击返回事件
     *
     * @param keyCode 事件名
     * @param event   事件
     * @return 返回是否被触发
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            writeSettings();
            onAppExit();
            return true;
        }
        return false;
    }

    /**
     * 写设置保存数据
     */
    private void writeSettings() {
        Settings = getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = Settings.edit();
        editor.putString("calc", calc);
        editor.putString("Mode", Mode);
        editor.putString("Units", Units);
        editor.putString("locale", locale.toString());
        editor.putStringSet("adapter", new HashSet<>(RecyclerAdapter.getArrayList()));
        editor.apply();
    }

    /**
     * 退出事件
     */
    public void onAppExit() {
        if (System.currentTimeMillis() - this.firstClick > 2000L) {
            this.firstClick = System.currentTimeMillis();
            Toast T = Toast.makeText(this, R.string.exit, Toast.LENGTH_LONG);
            LayoutInflater inflater = getLayoutInflater();//调用Activity的getLayoutInflater()
            @SuppressLint("InflateParams")
            View view = inflater.inflate(R.layout.exit_toast, null);
            view.setBackgroundResource(R.drawable.fillet);
            T.setView(view);
            T.show();
            return;
        }
        finish();
    }

    /**
     * 初始化科学计算器页面
     */
    public void initialSci() {
        //布局设置
        setContentView(R.layout.sci_interface);
        hideNavigation();
        //按钮设置
        connectView();
        btnFunction();
        //读取配置文件
        readSettingsSci();
        //配置界面
        switchLanguage(locale);
        switchUnits(Units);
        refreshTitleSci();
        refreshCalc();
        initialRecord();
    }

    /**
     * 按钮各自的功能
     */
    private void btnFunction() {
        btn0.setOnClickListener(v -> getInput("0"));
        btn1.setOnClickListener(v -> getInput("1"));
        btn2.setOnClickListener(v -> getInput("2"));
        btn3.setOnClickListener(v -> getInput("3"));
        btn4.setOnClickListener(v -> getInput("4"));
        btn5.setOnClickListener(v -> getInput("5"));
        btn6.setOnClickListener(v -> getInput("6"));
        btn7.setOnClickListener(v -> getInput("7"));
        btn8.setOnClickListener(v -> getInput("8"));
        btn9.setOnClickListener(v -> getInput("9"));
        btnDot.setOnClickListener(v -> getInput("."));
        btnNega.setOnClickListener(v -> getInput("-"));

        btnAdd.setOnClickListener(v -> getInput("＋"));
        btnMinus.setOnClickListener(v -> getInput("－"));
        btnMulti.setOnClickListener(v -> getInput("×"));
        btnDivide.setOnClickListener(v -> getInput("÷"));
        btnBracket.setOnClickListener(v -> inputBracket());

        btnMenu.setOnClickListener(v -> showPopupMenu(btnMenu));
        btnDegree.setOnClickListener(v -> switchUnits("Degree"));
        btnRadian.setOnClickListener(v -> switchUnits("Radian"));

        btnArc.setOnClickListener(v -> inverseTri());
        btnSin.setOnClickListener(v -> triangleFunc(R.id.btnSin));
        btnCos.setOnClickListener(v -> triangleFunc(R.id.btnCos));
        btnTan.setOnClickListener(v -> triangleFunc(R.id.btnTan));
        btnPi.setOnClickListener(v -> getInput("π"));

        btnLg.setOnClickListener(v -> getInput("lg("));
        btnLn.setOnClickListener(v -> getInput("ln("));
        btnE.setOnClickListener(v -> getInput("e"));
        btnPow.setOnClickListener(v -> getInput("^("));
        btnEPow.setOnClickListener(v -> getInput("e^("));
        btnTenPow.setOnClickListener(v -> getInput("10^("));
        btnSq.setOnClickListener(v -> getInput("^2"));
        btnSqrt.setOnClickListener(v -> getInput("√("));
        btnPercent.setOnClickListener(v -> getInput("%"));
        btnFact.setOnClickListener(v -> getInput("!"));

        btnBack.setOnClickListener(v -> backSpaceSci());
        btnClear.setOnClickListener(v -> clearRecordSci());
        btnEqual.setOnClickListener(v -> getAnsSci());

        arcDisabledBtn = new Button[]{btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btnDot, btnMinus,
                btnAdd, btnMulti, btnDivide, btnNega, btnBracket, btnEqual, btnPi, btnSq, btnSqrt, btnPow,
                btnEPow, btnTenPow, btnPercent, btnFact, btnE, btnLn, btnLg};
        storeColor();
    }

    /**
     * 切换单位——角度和弧度
     *
     * @param units 单位名
     */
    private void switchUnits(String units) {
        if (units == null) units = "Degree";
        switch (units) {
            case "Degree": {
                btnRadian.setTextColor(getResources().getColor(R.color.disabledBtn));
                btnDegree.setEnabled(true);
                btnDegree.setTextColor(getResources().getColor(R.color.colorAccent));
                Units = "Degree";
                break;
            }
            case "Radian": {
                btnDegree.setTextColor(getResources().getColor(R.color.disabledBtn));
                btnRadian.setEnabled(true);
                btnRadian.setTextColor(getResources().getColor(R.color.colorAccent));
                Units = "Radian";
                break;
            }
        }
        writeSettings();
    }

    /**
     * 三角函数按钮
     *
     * @param buttonID 三角函数名
     */
    private void triangleFunc(int buttonID) {
        switch (buttonID) {
            case R.id.btnSin: {
                calc += "sin(";
                break;
            }
            case R.id.btnCos: {
                calc += "cos(";
                break;
            }
            case R.id.btnTan: {
                calc += "tan(";
                break;
            }
        }
        enableBtn();
        refreshCalc();
    }

    /**
     * 反三角函数按钮
     */
    private void inverseTri() {
        if (calc.endsWith("arc")) {
            calc = calc.substring(0, calc.length() - 3);
            enableBtn();
        } else {
            calc += "arc";
            disableBtn();
        }
        refreshCalc();
    }

    /**
     * 反三角函数按钮禁用其他按钮
     */
    private void disableBtn() {
        for (int i = 0; i < arcDisabledBtn.length; i++) {
            arcDisabledBtn[i].setEnabled(false);
            disabledBtnColor[i] = arcDisabledBtn[i].getTextColors();
            arcDisabledBtn[i].setTextColor(getResources().getColor(R.color.disabledBtn));
        }
    }

    /**
     * 启用被禁用的按钮
     */
    private void enableBtn() {
        for (int i = 0; i < arcDisabledBtn.length; i++) {
            arcDisabledBtn[i].setEnabled(true);
            arcDisabledBtn[i].setTextColor(disabledBtnColor[i]);
        }
    }

    /**
     * 预先储存被禁用的按钮颜色
     */
    private void storeColor() {
        disableBtn();
        enableBtn();
    }

    /**
     * 弹出菜单栏
     *
     * @param view 菜单对象
     */
    private void showPopupMenu(View view) {
        final PopupMenu popupMenu = new PopupMenu(this, view);
        //Menu布局
        popupMenu.getMenuInflater().inflate(R.menu.main, popupMenu.getMenu());
        //点击事件
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.Standard:
                    Mode = "Standard";
                    changeMode("Standard");
                    break;
                case R.id.Scientific:
                    Mode = "Scientific";
                    changeMode("Scientific");
                    break;
                case R.id.English:
                    locale = Locale.ENGLISH;
                    switchLanguage(locale);
                    refreshTitleSci();
                    break;
                case R.id.Chinese:
                    locale = Locale.SIMPLIFIED_CHINESE;
                    switchLanguage(locale);
                    refreshTitleSci();
                    break;
                default:
                    popupMenu.dismiss();
                    break;
            }
            writeSettings();
            return false;
        });
        //关闭事件
        popupMenu.setOnDismissListener(menu -> {
        });
        //显示菜单，不要少了这一步
        popupMenu.show();
    }

    /**
     * 刷新标题和单位
     */
    private void refreshTitleSci() {
        appName.setText(getString(R.string.app_name));
        btnDegree.setText(getString(R.string.btnDegree));
        btnRadian.setText(getString(R.string.btnRadian));
    }

    /**
     * 成对地输出括号
     */
    private void inputBracket() {
        calc += (Function.countStr(calc, "(") == 0 ? "(" :
                (Function.countStr(calc, "(") > Function.countStr(calc, ")") ? ")" : "("));
        refreshCalc();
    }

    /**
     * 将View与函数名绑定
     */
    private void connectView() {
        //数字按钮
        btn0 = findViewById(R.id.btn0);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn7 = findViewById(R.id.btn7);
        btn8 = findViewById(R.id.btn8);
        btn9 = findViewById(R.id.btn9);
        btnDot = findViewById(R.id.btnDot);
        btnMinus = findViewById(R.id.btnMinus);
        //运算符按钮
        btnAdd = findViewById(R.id.btnAdd);
        btnMulti = findViewById(R.id.btnMulti);
        btnDivide = findViewById(R.id.btnDivide);
        btnNega = findViewById(R.id.btnNega);
        btnBracket = findViewById(R.id.btnBracket);
        //功能键按钮
        btnClear = findViewById(R.id.btnClear);
        btnBack = findViewById(R.id.btnBack);
        btnEqual = findViewById(R.id.btnEqual);
        btnMenu = findViewById(R.id.btnMenu);
        btnDegree = findViewById(R.id.btnDegree);
        btnRadian = findViewById(R.id.btnRadian);
        //高级功能按钮
        btnPi = findViewById(R.id.btnPi);
        specialStyleSet(btnPi);

        btnSin = findViewById(R.id.btnSin);
        specialStyleSet(btnSin);

        btnCos = findViewById(R.id.btnCos);
        specialStyleSet(btnCos);

        btnTan = findViewById(R.id.btnTan);
        specialStyleSet(btnTan);

        btnSq = findViewById(R.id.btnSq);
        specialStyleSet(btnSq);

        btnSqrt = findViewById(R.id.btnSqrt);
        specialStyleSet(btnSqrt);

        btnE = findViewById(R.id.btnE);
        specialStyleSet(btnE);

        btnFact = findViewById(R.id.btnFact);
        specialStyleSet(btnFact);

        btnPercent = findViewById(R.id.btnPercent);
        specialStyleSet(btnPercent);

        btnPow = findViewById(R.id.btnPow);
        specialStyleSet(btnPow);

        btnArc = findViewById(R.id.btnArc);
        specialStyleSet(btnArc);

        btnLn = findViewById(R.id.btnLn);
        specialStyleSet(btnLn);

        btnLg = findViewById(R.id.btnLg);
        specialStyleSet(btnLg);

        btnTenPow = findViewById(R.id.btnTenPow);
        specialStyleSet(btnTenPow);

        btnEPow = findViewById(R.id.btnEPow);
        specialStyleSet(btnEPow);
        //输入框和记录框
        inputWindow = findViewById(R.id.inputWindow);
        recordWindow = findViewById(R.id.recordWindow);
        appName = findViewById(R.id.appName);
    }

    /**
     * 对按钮文字个性化
     *
     * @param btn 需要改变的按钮
     */
    private void specialStyleSet(Button btn) {
        if (btn == null) return;
        SpannableString spannableString;
        //字体
        Typeface TimesBold = Typeface.createFromAsset(getAssets(), "fonts/timesbd.ttf");
        //上标
        SuperscriptSpan superscriptSpan = new SuperscriptSpan();
        //上标相对大小
        RelativeSizeSpan superScriptSize = new RelativeSizeSpan(0.7f);
        //粗体
        StyleSpan bold = new StyleSpan(Typeface.BOLD);
        switch (btn.getId()) {
            case R.id.btnSq: {
                spannableString = new SpannableString("x2");
                spannableString.setSpan(superscriptSpan, 1, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(superScriptSize, 1, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                btnSq.setText(spannableString);
                btnSq.setTypeface(TimesBold);
                break;
            }
            case R.id.btnSqrt: {
                spannableString = new SpannableString("√￣");
                spannableString.setSpan(bold, 1, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                btnSqrt.setText(spannableString);
                btnSqrt.setTypeface(TimesBold);
                break;
            }
            case R.id.btnSin: {
                btnSin.setTypeface(TimesBold);
                break;
            }
            case R.id.btnCos: {
                btnCos.setTypeface(TimesBold);
                break;
            }
            case R.id.btnTan: {
                btnTan.setTypeface(TimesBold);
                break;
            }
            case R.id.btnPi: {
                btnPi.setTypeface(TimesBold);
                break;
            }
            case R.id.btnEPow: {
                spannableString = new SpannableString("ex");
                spannableString.setSpan(superscriptSpan, 1, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(superScriptSize, 1, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                btnEPow.setText(spannableString);
                btnEPow.setTypeface(TimesBold);
                break;
            }
            case R.id.btnE: {
                btnE.setTypeface(TimesBold);
                break;
            }
            case R.id.btnPow: {
                spannableString = new SpannableString("xy");
                spannableString.setSpan(superscriptSpan, 1, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(superScriptSize, 1, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                btnPow.setText(spannableString);
                btnPow.setTypeface(TimesBold);
                break;
            }
            case R.id.btnFact: {
                btnFact.setTypeface(TimesBold);
                break;
            }
            case R.id.btnArc: {
                btnArc.setTypeface(TimesBold);
                break;
            }
            case R.id.btnPercent: {
                btnPercent.setTypeface(TimesBold);
                break;
            }
            case R.id.btnLn: {
                btnLn.setTypeface(TimesBold);
                break;
            }
            case R.id.btnLg: {
                btnLg.setTypeface(TimesBold);
                break;
            }
            case R.id.btnTenPow: {
                spannableString = new SpannableString("10x");
                spannableString.setSpan(superscriptSpan, 2, 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(superScriptSize, 2, 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                btnTenPow.setText(spannableString);
                btnTenPow.setTypeface(TimesBold);
            }
        }
    }

    /**
     * 自适应文字大小的输出框
     */
    private void refreshCalc() {
        //获取屏幕宽度
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        int screenWidth = point.x;
        //标准单字大小
        float standardTextSize = Function.sp2px(64, inputWindow.getResources().getDisplayMetrics().scaledDensity);
        float suitedTextSize = standardTextSize;
        //如果字体宽度超出屏幕宽度
        if (calc.length() > (screenWidth / standardTextSize)) {
            do {
                suitedTextSize -= 1;
                inputWindow.setTextSize(TypedValue.COMPLEX_UNIT_PX, suitedTextSize);
            } while (inputWindow.getPaint().measureText(calc) > screenWidth);
        } else
            inputWindow.setTextSize(TypedValue.COMPLEX_UNIT_PX, standardTextSize);
        inputWindow.setText(calc);
    }

    /**
     * 初始化记录窗口
     */
    private void initialRecord() {
        recordWindow.setAdapter(RecyclerAdapter);
        ArrayList<String> record = new ArrayList<>(RecyclerAdapter.getArrayList());
        RecyclerAdapter.clearString();
        for (int i = 0; i < record.size(); i++)
            RecyclerAdapter.addString(record.get(i));
        recordWindow.requestLayout();
    }

    /**
     * 更换计算器模式
     *
     * @param mode 标准计算器/科学计算器
     */
    private void changeMode(String mode) {
        if (mode == null) mode = "Scientific";
        switch (mode) {
            case "Scientific":
                Mode = "Scientific";
                break;
            case "Standard": {
                Intent intent = new Intent();
                intent.setClass(sciCalculator.this, stdCalculator.class);  //从stdCalculator跳转到sciCalculator
                startActivity(intent);  //开始跳转
                finish();
                break;
            }
        }
        writeSettings();
    }

    /**
     * 修改界面语言
     *
     * @param locale 语言
     */
    public void switchLanguage(Locale locale) {
        Resources resources = getResources();// 获得res资源对象
        Configuration config = resources.getConfiguration();// 获得设置对象
        DisplayMetrics dm = resources.getDisplayMetrics();// 获得屏幕参数：主要是分辨率，像素等。
        config.setLocale(locale);
        resources.updateConfiguration(config, dm);
    }

    /**
     * 清除记录
     */
    private void clearRecordSci() {
        if (!calc.isEmpty()) {
            calc = "";
            enableBtn();
            refreshCalc();
        } else
            RecyclerAdapter.clearString();
    }

    /**
     * 删除输入
     */
    private void backSpaceSci() {
        if (!calc.isEmpty()) {
            if (calc.endsWith("sin(") | calc.endsWith("cos(") | calc.endsWith("tan(")) {
                calc = calc.substring(0, calc.length() - 4);
                if (calc.endsWith("arc"))
                    calc = calc.substring(0, calc.length() - 3);
            } else if (calc.endsWith("ln(") | calc.endsWith("lg(")) {
                calc = calc.substring(0, calc.length() - 3);
            } else if (calc.endsWith("√(")) {
                calc = calc.substring(0, calc.length() - 2);
            } else
                calc = calc.substring(0, calc.length() - 1);
            enableBtn();
            refreshCalc();
        }
    }

    /**
     * 获取输入
     *
     * @param keyString 输入字符
     */
    private void getInput(String keyString) {
        calc += keyString;
        refreshCalc();
    }

    /**
     * 隐藏导航栏
     */
    protected void hideNavigation() {
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(option);
    }

    /**
     * 根据输入获取答案
     */
    private void getAnsSci() {
        if (Function.isDigit(calc)) return;
        if (Function.isComplexExpression(calc)) {
            //记录表达式
            RecyclerAdapter.addString(calc);
            //左右括号的数目
            if (Function.countStr(calc, "(") != Function.countStr(calc, ")")) {
                RecyclerAdapter.addString(errorMapSci("BRACKETS_UNMATCH_ERROR"));
                recordWindow.scrollToPosition(RecyclerAdapter.getItemCount() - 1);
                return;
            } else {
                //停止条件是最后只剩下一个数字
                while (!Function.isDigit(calc)) {
                    String ans;
                    //还存在括号
                    if (calc.contains("(")) {
                        //最右边左括号的位置
                        int LeftBracket = calc.lastIndexOf("(");
                        //与左括号匹配的右括号
                        int RightBracket = calc.indexOf(")", LeftBracket);
                        //里面的表达式
                        String innerExpression = calc.substring(LeftBracket + 1, RightBracket);
                        ans = Function.evaluateAdvanced(innerExpression, locale, Units);
                        //把里面的表达式替换成结果，并去掉左右括号
                        if (Function.isDigit(ans))
                            calc = calc.substring(0, LeftBracket) + ans + calc.substring(RightBracket + 1);
                        else {
                            RecyclerAdapter.addString(errorMapSci(ans));
                            recordWindow.scrollToPosition(RecyclerAdapter.getItemCount() - 1);
                            return;
                        }
                    }
                    //没有括号
                    else {
                        ans = Function.evaluateAdvanced(calc, locale, Units);
                        if (!Function.isDigit(ans)) {
                            RecyclerAdapter.addString(errorMapSci(ans));
                            recordWindow.scrollToPosition(RecyclerAdapter.getItemCount() - 1);
                            return;
                        }
                        calc = ans;
                        break;
                    }
                }
                BigDecimal ans = new BigDecimal(calc);
                ans = ans.setScale(8, BigDecimal.ROUND_HALF_UP);
                if (ans.doubleValue() == 0)
                    ans = BigDecimal.ZERO;
                else
                    ans = ans.stripTrailingZeros();
                RecyclerAdapter.addString(ans.toPlainString());
                calc = ans.toPlainString();
            }
            //刷新界面
            refreshCalc();
        }
        //表达式有错误
        else
            RecyclerAdapter.addString(errorMapSci("SYNTAX_ERROR"));
        recordWindow.scrollToPosition(RecyclerAdapter.getItemCount() - 1);
    }

    /**
     * 读设置读取数据
     */
    private void readSettingsSci() {
        Settings = getSharedPreferences("Settings", MODE_PRIVATE);
        calc = Settings.getString("calc", calc);
        Mode = Settings.getString("Mode", Mode);
        Units = Settings.getString("Units", Units);
        String raw = Settings.getString("locale", Locale.getDefault().getDisplayLanguage());
        assert raw != null;
        switch (Function.countStr(raw, "_")) {
            case 2: {
                String language = raw.substring(0, raw.indexOf("_"));
                String country = raw.substring(raw.indexOf("_") + 1, raw.lastIndexOf("_"));
                String variant = raw.substring(raw.lastIndexOf("_") + 1);
                locale = new Locale(language, country, variant);
            }
            case 1: {
                String language = raw.substring(0, raw.indexOf("_"));
                String country = raw.substring(raw.indexOf("_") + 1);
                locale = new Locale(language, country);
                break;
            }
            case 0:
                locale = new Locale(raw);
        }
        RecyclerAdapter = new RecordAdapter(new ArrayList<>(
                Objects.requireNonNull(Settings.getStringSet("adapter", new HashSet<>()))));
    }

    /**
     * 错误类型映射
     *
     * @param error 错误类型
     * @return 错误语句
     */
    @NotNull
    private String errorMapSci(String error) {
        if (error == null) error = "";
        switch (error) {
            case "DIVISOR_ZERO_ERROR":
                return getString(R.string.divideZero);
            case "SYNTAX_ERROR":
                return getString(R.string.syntaxError);
            case "BRACKETS_UNMATCHED_ERROR":
                return getString(R.string.checkBracket);

            case "FACTORIAL_NEGATIVES_ERROR":
                return getString(R.string.factNega);
            case "FACTORIAL_DECIMAL_ERROR":
                return getString(R.string.factDecimal);
            case "SQUARE_ROOT_NEGATIVES_ERROR":
                return getString(R.string.sqrtNega);
            case "INVERSE_TRIANGLE_SIN_OUT_OF_RANGE_ERROR":
                return getString(R.string.inverseTriSinRangeError);
            case "INVERSE_TRIANGLE_COS_OUT_OF_RANGE_ERROR":
                return getString(R.string.inverseTriCosRangeError);
            case "TAN_OUT_OF_RANGE_ERROR":
                return getString(R.string.tanRangeError);
            case "LOGARITHM_OUT_OF_RANGE_ERROR":
                return getString(R.string.logRangeError);

            case "UNEXPECTED_INTERNAL_ERROR":
            default:
                return getString(R.string.internalError);
        }
    }
}
