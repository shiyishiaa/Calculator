package com.norman.calculator.action;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.norman.calculator.Function;
import com.norman.calculator.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Norman_Yi
 * @version 2.9.0
 */
public class stdCalculator extends AppCompatActivity {
    //数字按钮
    protected Button btn0, btn1, btn2, btn3, btn4, btn5,
            btn6, btn7, btn8, btn9, btnDot, btnMinus;
    //运算符按钮
    protected Button btnAdd, btnMulti, btnDivide, btnNega, btnBracket;
    //功能键按钮
    protected Button btnClear, btnBack, btnEqual;
    //菜单按钮
    protected ImageButton btnMenu;
    //输入框和记录框
    protected TextView inputWindow, appName;
    protected RecyclerView recordWindow;
    protected RecordAdapter RecyclerAdapter;
    protected SharedPreferences Settings;
    //输入框字符串 记录框列表 配置参数
    protected String calc = "";
    protected String Mode = "Standard";
    protected Locale locale = Locale.getDefault();

    /**
     * 程序入口
     *
     * @param savedInstanceState Activity的状态
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialStd();
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

    protected long firstClick;

    /**
     * 退出事件
     */
    public void onAppExit() {
        if (System.currentTimeMillis() - this.firstClick > 2000L) {
            this.firstClick = System.currentTimeMillis();
            Toast T = Toast.makeText(this, R.string.exit, Toast.LENGTH_LONG);
            // LayoutInflater的作用：
            // 对于一个没有被载入或者想要动态载入的界面都需要LayoutInflater.inflate()来载入
            // LayoutInflater是用来找res/layout/下的xml布局文件，并且实例化
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
     * 初始化标准计算器页面
     */
    public void initialStd() {
        //布局设置
        setContentView(R.layout.std_interface);
        hideNavigation();
        //按钮设置
        connectView();
        btnFunction();
        //读取配置文件
        readSettingsStd();
        //配置界面
        switchLanguage(locale);
        refreshTitleStd();
        refreshCalc();
        initialRecord();
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
     * 将View与函数名绑定
     */
    protected void connectView() {
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
        //输入框和记录框
        inputWindow = findViewById(R.id.inputWindow);
        recordWindow = findViewById(R.id.recordWindow);
        appName = findViewById(R.id.appName);
    }

    /**
     * 按钮各自的功能
     */
    protected void btnFunction() {
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

        btnBack.setOnClickListener(v -> backSpaceStd());
        btnClear.setOnClickListener(v -> clearRecordStd());
        btnEqual.setOnClickListener(v -> getAnsStd());
    }

    /**
     * 读设置读取数据
     */
    protected void readSettingsStd() {
        Settings = getSharedPreferences("Settings", MODE_PRIVATE);
        calc = Settings.getString("calc", calc);
        Mode = Settings.getString("Mode", Mode);
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
     * 修改界面语言功能函数
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
     * 刷新标题
     */
    protected void refreshTitleStd() {
        appName.setText(getString(R.string.app_name));
    }

    /**
     * 自适应文字大小的输出框
     */
    protected void refreshCalc() {
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
                suitedTextSize -= 0.5f;
                inputWindow.setTextSize(TypedValue.COMPLEX_UNIT_PX, suitedTextSize);
            } while (inputWindow.getPaint().measureText(calc) > screenWidth);
        } else
            inputWindow.setTextSize(TypedValue.COMPLEX_UNIT_PX, standardTextSize);
        inputWindow.setText(calc);
    }

    /**
     * 初始化记录窗口
     */
    protected void initialRecord() {
        recordWindow.setAdapter(RecyclerAdapter);
        ArrayList<String> record = new ArrayList<>(RecyclerAdapter.getArrayList());
        RecyclerAdapter.clearString();
        for (int i = 0; i < record.size(); i++)
            RecyclerAdapter.addString(record.get(i));
        recordWindow.requestLayout();
    }

    /**
     * 获取输入
     *
     * @param keyString 输入字符
     */
    protected void getInput(String keyString) {
        calc += keyString;
        refreshCalc();
    }

    /**
     * 成对地输出括号
     */
    protected void inputBracket() {
        calc += (Function.countStr(calc, "(") == 0 ? "(" :
                (Function.countStr(calc, "(") > Function.countStr(calc, ")") ? ")" : "("));
        refreshCalc();
    }

    /**
     * 弹出菜单栏
     *
     * @param view 菜单对象
     */
    protected void showPopupMenu(View view) {
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
                    refreshTitleStd();
                    break;
                case R.id.Chinese:
                    locale = Locale.SIMPLIFIED_CHINESE;
                    switchLanguage(locale);
                    refreshTitleStd();
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
     * 删除输入
     */
    protected void backSpaceStd() {
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
            refreshCalc();
        }
    }

    /**
     * 清除记录
     */
    protected void clearRecordStd() {
        if (!calc.isEmpty()) {
            calc = "";
            refreshCalc();
        } else {
            RecyclerAdapter.clearString();
        }
    }

    /**
     * 根据输入获取答案
     */
    protected void getAnsStd() {
        if (Function.isDigit(calc)) return;
        if (Function.isExpression(calc)) {
            //记录表达式
            RecyclerAdapter.addString(calc);
            //左右括号的数目
            if (Function.countStr(calc, "(") != Function.countStr(calc, ")")) {
                RecyclerAdapter.addString(errorMapStd("BRACKETS_UNMATCH_ERROR"));
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
                        ans = Function.evaluatePrime(innerExpression);
                        //把里面的表达式替换成结果，并去掉左右括号
                        if (Function.isDigit(ans))
                            calc = calc.substring(0, LeftBracket) + ans + calc.substring(RightBracket + 1);
                        else {
                            RecyclerAdapter.addString(errorMapStd(ans));
                            recordWindow.scrollToPosition(RecyclerAdapter.getItemCount() - 1);
                            return;
                        }
                    }
                    //没有括号
                    else {
                        ans = Function.evaluatePrime(calc);
                        if (!Function.isDigit(ans)) {
                            RecyclerAdapter.addString(errorMapStd(ans));
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
            RecyclerAdapter.addString(errorMapStd("SYNTAX_ERROR"));
        recordWindow.scrollToPosition(RecyclerAdapter.getItemCount() - 1);
    }

    /**
     * 更换计算器模式
     *
     * @param mode 标准计算器/科学计算器
     */
    protected void changeMode(String mode) {
        if (mode == null) mode = "Standard";
        switch (mode) {
            case "Standard":
                Mode = "Standard";
                break;
            case "Scientific": {
                Mode = "Scientific";
                Intent intent = new Intent();
                intent.setClass(stdCalculator.this, sciCalculator.class);  //从stdCalculator跳转到sciCalculator
                startActivity(intent);  //开始跳转
                finish();
                break;
            }
        }
        writeSettings();
    }

    /**
     * 写设置保存数据
     */
    protected void writeSettings() {
        Settings = getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = Settings.edit();
        editor.putString("calc", calc);
        editor.putString("Mode", Mode);
        editor.putString("locale", locale.toString());
        editor.putStringSet("adapter", new HashSet<>(RecyclerAdapter.getArrayList()));
        editor.apply();
    }

    /**
     * 错误类型映射
     *
     * @param error 错误类型
     * @return 错误语句
     */
    protected String errorMapStd(String error) {
        if (error == null) error = "";
        switch (error) {
            case "DIVISOR_ZERO_ERROR":
                return getString(R.string.divideZero);
            case "SYNTAX_ERROR":
                return getString(R.string.syntaxError);
            case "BRACKETS_UNMATCH_ERROR":
                return getString(R.string.checkBracket);
            case "UNEXPECTED_INTERNAL_ERROR":
            default:
                return getString(R.string.internalError);
        }
    }
}