// 2022オブジェクト指向設計演習最終課題 J221293 向田征史

// 項になりうる式のインターフェース
interface Term {
    double calculate(double t);
}

// 三角関数の種類
enum TriType {
    Sin,
    Cos,
    Tan,
}

// tの値をそのまま返す項
class NormalTerm implements Term {
    private Function innerFunction; // 合成関数の場合、自分の内側にある関数
    public NormalTerm(Function innerFunction) {
        this.innerFunction = innerFunction;
    }
    public double calculate(double t) {
        double t2 = t;
        if(innerFunction != null) t2 = innerFunction.calculate(t);
        return t2;
    }
}

// 定数項
class ConstantTerm implements Term {
    private double constant; // 定数
    public ConstantTerm(double constant) {
        this.constant = constant;
    }
    public double calculate(double t) {
        return constant;
    }
}

// 三角関数の項クラス
class TriTerm implements Term {
    private TriType triType; // 三角関数の種類
    private Function innerFunction; // 合成関数の場合、自分の内側にある関数

    // コンストラクタ
    TriTerm(TriType triType, Function innerFunction) {
        this.triType = triType; // 三角関数の種類
        this.innerFunction = innerFunction; // 合成関数
    }

    // 計算結果を返す
    public double calculate(double t) {
        double t2 = t;
        if(innerFunction != null) t2 = innerFunction.calculate(t);
        switch(triType) {
            case Sin:
                return Math.sin(t2);
            case Cos:
                return Math.cos(t2);
            case Tan:
                return Math.tan(t2);
            default:
                return 0;
        }
    }
}

// 対数関数の項クラス
class LogTerm implements Term {
    private double base; // 対数の底
    private Function innerFunction; // 合成関数の場合、自分の内側にある関数

    // コンストラクタ
    LogTerm(double base, Function innerFunction) {
        this.base = base; // 三角関数の種類
        this.innerFunction = innerFunction; // 合成関数
    }

    // 計算結果を返す
    public double calculate(double t) {
        double t2 = t;
        if(innerFunction != null) t2 = innerFunction.calculate(t);
        return Math.log(t2)/Math.log(base);
    }
}