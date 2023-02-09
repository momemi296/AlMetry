// 2022オブジェクト指向設計演習最終課題 J221293 向田征史

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class App {
    public static void main(String[] args) {

        JFrame frame = new JFrame("AlMetry");
        frame.setContentPane(new MainPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

// メインとなるPanel
class MainPanel extends JPanel {
    private GraphArea graphArea = new GraphArea(); // グラフを描画するCanvas
    private OptionPanel optionPanel; // ボタンなどを配置するPanel
    private TextPanel textPanel; // 式を入力するPanel
    private FileNamePanel filenamePanel; // 保存するファイルの名前を入力するPanel
    private GraphAreaSizePanel graphAreaSizePanel; // グラフの描画領域を指定するフィールドを配置するPanel

    public MainPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // 要素を縦に並べる
        setPreferredSize(new Dimension(500, 620)); // 500ｘ620のサイズにする。
        graphArea = new GraphArea(); // グラフを描画するCanvas
        textPanel = new TextPanel(); // 式を入力するPanel
        optionPanel = new OptionPanel(); // ボタンなどを配置するPanel
        filenamePanel = new FileNamePanel(); // 保存するファイルの名前を入力するPanel
        graphAreaSizePanel = new GraphAreaSizePanel(); // グラフの描画領域を指定するフィールドを配置するPanel

        JPanel control_panel = new JPanel();
        control_panel.setPreferredSize(new Dimension(500, 120));
        control_panel.setLayout(new GridLayout(2,2));
        control_panel.add(textPanel);
        control_panel.add(graphAreaSizePanel);
        control_panel.add(optionPanel);
        control_panel.add(filenamePanel);

        add(graphArea);
        add(control_panel);

        setBackground(Color.BLACK);
    }

    // ボタンなどを配置するPanel
    class OptionPanel extends JPanel {
        private JButton saveButton; // グラフを画像として保存するButton
        private JButton drawButton; // グラフを描画するButton
        
        public OptionPanel() {
            saveButton = new JButton("保存"); // グラフを画像として保存するButton
            drawButton = new JButton("描画"); // グラフを描画するButton
            drawButton.addActionListener(new DrawButtonListener());
            saveButton.addActionListener(new SaveButtonListener());
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS)); // 要素を横に並べる
            add(saveButton);
            add(drawButton);
        }
    }

    // グラフのスケールを操作するJPanel
    class GraphAreaSizePanel extends JPanel {
        JLabel xScaleLabel = new JLabel("X scale:");
        JLabel yScaleLabel = new JLabel("Y scale:");
        String[] combodata = {"1", "5", "10", "50", "100", "1000"};
        JComboBox<String> xScaleComboBox = new JComboBox<String>(combodata);
        JComboBox<String> yScaleComboBox = new JComboBox<String>(combodata);
        public GraphAreaSizePanel() {
            add(xScaleLabel);
            add(xScaleComboBox);
            add(yScaleLabel);
            add(yScaleComboBox);
        }

        // 選択しているx軸のスケールを返すメソッド
        public int getXComboValue() {
            String value = (String)xScaleComboBox.getSelectedItem();
            return Integer.parseInt(value);
        }

        // 選択しているy軸のスケールを返すメソッド
        public int getYComboValue() {
            String value = (String)yScaleComboBox.getSelectedItem();
            return Integer.parseInt(value);
        }
    }

    // 保存するファイルの名前を操作するJPanel
    class FileNamePanel extends JPanel{
        JLabel filenamePanel = new JLabel("Filename:");
        JTextField textField; // 式を入力するTextField
        public FileNamePanel() {
            textField = new JTextField(15);
            textField.setText("sample");
            add(filenamePanel);
            add(textField);
        }
    }

    // 式を入力するフィールドなどを表示するパネル
    class TextPanel extends JPanel {
        private JTextField xField; // x(t)の式を入力するフィールド
        private JTextField yField; // y(t)の式を入力するフィールド

        public TextPanel() {
            JPanel panel1 = new JPanel(); // x(t)の部分
            JPanel panel2 = new JPanel(); // y(t)の部分
            JLabel xLabel = new JLabel("x(t) = ");// x(t) = のラベル
            JLabel yLabel = new JLabel("y(t) = ");// y(t) = のラベル
            xField = new JTextField(20); // x(t)の式を入力するフィールド
            yField = new JTextField(20); // y(t)の式を入力するフィールド
            panel1.add(xLabel); // x(t) = のラベルを追加
            panel1.add(xField); // x(t)の式を入力するフィールドを追加
            panel2.add(yLabel); // y(t) = のラベルを追加
            panel2.add(yField); // y(t)の式を入力するフィールドを追加
            add(panel1); // x(t)の部分を追加
            add(panel2); // y(t)の部分を追加
            // レイアウトを整える
            setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));
            setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        }

        public String getXFieldText() {
            return xField.getText();
        }

        public String getYFieldText() {
            return yField.getText();
        }
    }

    // Drawボタンを押した時の処理
    class DrawButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
             // 式を入力するテキストフィールドの入力されている文字列を取得
            String x_text = textPanel.getXFieldText(); // x = X(t)
            String y_text = textPanel.getYFieldText(); // y = Y(t)
            graphArea.setXScale(graphAreaSizePanel.getXComboValue()); // スケールの設定
            graphArea.setYScale(graphAreaSizePanel.getYComboValue()); // スケールの設定
            graphArea.createGraph(x_text, y_text); // グラフを作成し、描画する
        }
    }

    // Saveボタンを押した時の処理
    class SaveButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            graphArea.savePicture(filenamePanel.textField.getText());
        }
    }
}