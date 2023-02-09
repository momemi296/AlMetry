// 2022オブジェクト指向設計演習最終課題 J221293 向田征史

import java.util.ArrayList;

// 文字列から関数を作成するクラス
public class FunctionMaker {

    private static final int TARMNAME_LENGTH = 3; // sinやcosのような関数名の長さ
    private static final int BRANKET_LENGTH = 1; // 文字列 "(" の長さ

    // 文字列を元に関数を作成する
    public static Function createFunction(String text) {
        text = text.replaceAll("　", ""); //全角スペースを消去
        text = text.replaceAll(" ", ""); //半角スペースを消去
        Function f = new Function(); // 生成する関数
        for(int index = 0; index < text.length(); index++) {
            char c = text.charAt(index);
            if (Character.isDigit(c) || c == '-') { // 数値の場合
                if(c == '-') {
                    if(index != 0 && text.charAt(index - 1) != '(') {
                        f.addOperators(Operator.Puls);
                    }
                }
                for(int i = index + 1; i <= text.length(); i++){
                    // 末尾にたどり着いたとき
                    if(i == text.length()){
                        String numSring = text.substring(index, i);
                        if(numSring.equals("-")) numSring = "-1"; // 数値の文字列が-だったら-1とみなす
                        f.addTerm(new ConstantTerm(Double.parseDouble(numSring)));
                        index = i - 1;
                        break;
                    }

                    // 数値以外の文字にたどり着いた場合
                    if(!Character.isDigit(text.charAt(i)) && text.charAt(i) != '.'){
                        String numSring = text.substring(index, i); // 数値の文字列
                        if(numSring.equals("-")) numSring = "-1"; // 数値の文字列が-だったら-1とみなす
                        f.addTerm(new ConstantTerm(Double.parseDouble(numSring)));
                        // 次の文字が演算子以外の文字だったら、*の省略とみなす　4t -> 4*t
                        if(text.charAt(i) != '+' 
                            && text.charAt(i) != '*' 
                            && text.charAt(i) != '-' 
                            && text.charAt(i) != ')' 
                            && text.charAt(i) != '^') {
                            f.addOperators(Operator.Mul);
                        }
                        index = i - 1;
                        break;
                    }
                    
                }
            } else { // 数値でない場合
                if (c == '+') {f.addOperators(Operator.Puls);}
                else if (c == '*') {f.addOperators(Operator.Mul);}
                else if (c == '^') {f.addOperators(Operator.Pow);}
                else if (c == 'p') {
                    if(index + 1 < text.length() && text.charAt(index + 1) == 'i'){
                        f.addTerm(new ConstantTerm(Math.PI));
                        index += 1;
                    }
                
                }  else if (c == 'e') { // 自然対数の底e
                    f.addTerm(new ConstantTerm(Math.E));
                } else if (c == '(') { // 括弧"("
                    int bracketsCounter = 1; // "("の数 - ")"の数
                    for(int i = index + 1; i < text.length(); i++) {
                        if (text.charAt(i) == ')') {
                            bracketsCounter--;
                        } else if(text.charAt(i) == '(') {
                            bracketsCounter++;
                        }
                        if(bracketsCounter == 0) {
                            Function innerFunction = createFunction(text.substring(index + 1, i));
                            f.addTerm(new NormalTerm(innerFunction));
                            index = i;
                            break;
                        }
                    }
                    if(bracketsCounter != 0) {
                        Function errorFunction =  new Function();
                        errorFunction.error();
                        return errorFunction;
                    }
                } else if (c == 't') {
                    int i = checkTerm(text, "tan", index); // tan(t)か検知し、")"の位置のindexを取得
                    if(i == -1){
                        f.addTerm(new NormalTerm(null));
                    } else {
                        // ()の中の文字列を元に関数を生成
                        Function innerFunction = createFunction(text.substring(index + TARMNAME_LENGTH + BRANKET_LENGTH, i));
                        f.addTerm(new TriTerm(TriType.Tan, innerFunction)); // 関数にtanの項を追加
                        index = i;
                    }
                } else if (c == 's') {
                    int i = checkTerm(text, "sin", index); // sin(t)か検知する")"の位置のindexを取得
                    if(i == -1) return createErrorFunction(); // 文字列を正しく読み取れなかった場合エラーを返す
                    // ()の中の文字列を元に関数を生成
                    Function innerFunction = createFunction(text.substring(index + TARMNAME_LENGTH + BRANKET_LENGTH, i));
                    f.addTerm(new TriTerm(TriType.Sin, innerFunction)); // 関数にsinの項を追加
                    index = i;
                } else if (c == 'c') {
                    int i = checkTerm(text, "cos", index); // cos(t)か検知する")"の位置のindexを取得
                    if(i == -1) return createErrorFunction(); // 文字列を正しく読み取れなかった場合エラーを返す
                    // ()の中の文字列を元に関数を生成
                    Function innerFunction = createFunction(text.substring(index + TARMNAME_LENGTH + BRANKET_LENGTH, i));
                    f.addTerm(new TriTerm(TriType.Cos, innerFunction)); // 関数にcosの項を追加
                    index = i;
                } else if (c == 'l') {
                    int i = checkTerm(text, "log", index); // tan(t)か検知する")"の位置のindexを取得
                    if(i == -1) return createErrorFunction(); // 文字列を正しく読み取れなかった場合エラーを返す
                    // ()の中の文字列を元に関数を生成
                    Function innerFunction = createFunction(text.substring(index + TARMNAME_LENGTH + BRANKET_LENGTH, i));
                    f.addTerm(new LogTerm(Math.E, innerFunction)); // 関数にlogの項を追加
                    index = i;
                }
            }
        }
        return f;
    }

    // エラーを持つ関数を作成するメソッド
    private static Function createErrorFunction() {
        Function errorFunction =  new Function();
        errorFunction.error();
        return errorFunction;
    }

    /* 指定の文字列の部分文字列が、関数名(sinやlogなど)と一致しているか調べるメソッド
     * ')'を最後に発見した位置を返す
     * 正しく関数を作れないエラーが発生したら-1を返す。
     */ 
    private static int checkTerm(String text, String pattarn, int index) {
        String checkText = ""; // 調べる部分文字列
        try {
            checkText = text.substring(index, index + pattarn.length()); // 部分文字列を作成
        } catch(StringIndexOutOfBoundsException e) {
            return -1; // error
        }
        
        if(pattarn.equals(checkText)){
            index += TARMNAME_LENGTH + BRANKET_LENGTH; // 関数名と括弧"("の分だけ調べる位置をずらす
            int bracketsCounter = 1; // "("の数 - ")"の数を数えるカウンター
            for(int i = index; i < text.length(); i++) {
                if (text.charAt(i) == ')') { // ')'を発見したとき
                    bracketsCounter--;
                } else if(text.charAt(i) == '(') { // '('を発見したとき
                    bracketsCounter++;
                }
                if(bracketsCounter == 0) { // "("の数と")"の数がおなじになったとき
                    index = i;
                    return index; // ')'を最後に発見した位置を返す
                }
            }
            return -1; // error
        } else {
            return -1; // error
        }
    }
}

// 演算子の種類の定義
enum Operator {
    Puls, // +
    Mul, // *
    Pow // ^
}

// 関数（数学的な意味での）のクラス
class Function {
    private ArrayList<Term> terms = new ArrayList<Term>(); // 関数に含まれる項
    private ArrayList<Operator> operators = new ArrayList<Operator>(); // 項と項をつなぐ演算子]
    private boolean errorFlag = false; // エラーが起きて正しく関数が作成できなかったらtrue

    // 項を追加するメソッド
    public void addTerm(Term term) {
        terms.add(term);
    }

    // 演算子を追加するメソッド
    public void addOperators(Operator operator) {
        operators.add(operator);
    }

    public ArrayList<Term> getTerms() {return terms;} // すべての項を得るメソッド
    public ArrayList<Operator> getOperators() {return operators;} // すべての演算子を得るメソッド

    // 式の値を計算するメソッド
    public double calculate(double t) {
        ArrayList<Double> numbers = new ArrayList<Double>(); // 項の値を格納する変数
        for (Term term : terms) numbers.add(term.calculate(t)); // 項の値をそれぞれ計算
        ArrayList<Operator> copyOperators = new ArrayList<Operator>(operators); // 演算子のリストをディープコピー

        operatorCul(numbers, copyOperators, Operator.Pow); // 累乗を先に計算
        operatorCul(numbers, copyOperators, Operator.Mul); // 乗算を次に計算
        operatorCul(numbers, copyOperators, Operator.Puls); // 加算を計算
        
        double returnNumber = 0;
        try {
            returnNumber = numbers.get(0);
        } catch(IndexOutOfBoundsException e) {
            errorFlag = true;
        } 

        return returnNumber; // 式の値を返す
    }

    // 関数をエラーにするメソッド
    public void error() {
        System.out.println("Error!!");
        errorFlag = true;
    }

    // 関数のエラー状態を返すメソッド
    public boolean isErrorFunction() {
        return errorFlag;
    }

    /* 演算子による式の値の計算を行うメソッド
     * 指定した演算子による計算を行った後の各項の値と残った演算子のリストを返す
     * ArrayList<Double> numbers        各項の値
     * ArrayList<Operator> operators    演算子のリスト
     * Operator operator                計算を行う演算子
     */ 
    private void operatorCul(ArrayList<Double> numbers, ArrayList<Operator> operators, Operator operator) {
        for (int i = 0; i < operators.size(); i++) {
            // 指定の演算子にたどり着いたとき
            if (operators.get(i) == operator) {
                // 演算子の前と後の項の値をその演算子で計算する。
                switch(operator) { // 演算子による処理分岐
                    case Puls: // 加算を行う
                        numbers.set(i, numbers.get(i) + numbers.get(i + 1));
                        break;
                    case Mul: // 乗算を行う
                        numbers.set(i, numbers.get(i) * numbers.get(i + 1));
                        break;
                    case Pow: // 累乗計算を行う
                        numbers.set(i, Math.pow(numbers.get(i), numbers.get(i + 1)));
                        break;
                }
                numbers.remove(i + 1); // 計算済みの値の削除
                operators.remove(i); // 計算済みの演算子の削除
                i -= 1; // リストが短くなった分の調整
            }
        }
    }
}