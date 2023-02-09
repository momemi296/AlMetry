// 2022オブジェクト指向設計演習最終課題 J221293 向田征史

import java.awt.*;
import java.util.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

// グラフを描画するCanvasクラス
public class GraphArea extends Canvas {
    private int originX = 250; // 原点のx座標
    private int originY = 250; // 原点のy座標
    private int xAxisLength = 500; // 軸の長さ
    private int yAxisLength = 500; // 軸の長さ
    private int arrowheadSize = 8; // 軸の先端の大きさ
    private int gridInterval = 50; // グリッドの間隔
    private ArrayList<Point> points = new ArrayList<Point>(); // グラフの点
    private int xScale = 1;
    private int yScale = 1;
    private String xText = "";
    private String yText = "";

    // コンストラクタ
    public GraphArea() {
        setPreferredSize(new Dimension(xAxisLength, yAxisLength));
    }

    // 与えられた文字列を元にグラフを作成する
    public void createGraph(String x_text, String y_text) {
        Function xt = FunctionMaker.createFunction(x_text); // y = X(t)
        Function yt = FunctionMaker.createFunction(y_text); // y = Y(t)

        // 作成した関数がエラーの場合グラフを作成しない
        if (xt.isErrorFunction() || yt.isErrorFunction()){
            return;
        }

        // 文字列を保存しておく
        this.xText = x_text;
        this.yText = y_text;

        // 点を描画する座標を初期化する
        points.clear();

        // グラフを描く点を追加する
        for (int i = -20000; i < 20000; i++){
            // 無限大やNaNの値を含む座標の場合、無効な点とする
            if (Double.isInfinite(xt.calculate((double)i/1000*xScale*yScale))
                || Double.isNaN(xt.calculate((double)i/1000*xScale*yScale)) 
                || Double.isInfinite(yt.calculate((double)i/1000*xScale*yScale))
                || Double.isNaN(yt.calculate((double)i/1000*xScale*yScale))) {
                points.add(new Point(0, 0, false)); // 無効な点
                continue;
            }
            // 点を描画する座標を求める
            int x = (int)(originX + gridInterval/(double)xScale*xt.calculate((double)i/1000*xScale*yScale));
            int y = (int)(originY - gridInterval/(double)yScale*yt.calculate((double)i/1000*xScale*yScale));
            points.add(new Point(x, y, true)); // 有効な点
        }
        repaint();
    }

    // x軸のスケールを設定するメソッド
    public void setXScale(int scale) {
        this.xScale = scale;
    }

    // ｙ軸のスケールを設定するメソッド
    public void setYScale(int scale) {
        this.yScale = scale;
    }
    
    @Override
    public void paint(Graphics g) {
        drawGraph(g);
    }

    // グラフを描画するメソッド
    public void drawGraph(Graphics g) {
        // 背景を黒で塗りつぶす
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, xAxisLength, yAxisLength);

        drawAxis(g); // 軸の描画
        drawScale(g); // 軸の目盛りの描画
        drawFormula(g); // 式の描画

        // 曲線の描画
        g.setColor(new Color(0, 255, 255));
        for(int i = 0; i < points.size() - 1; i++){
            // 直前と同じ位置の点は描画しない
            if (points.get(i).x == points.get(i + 1).x && points.get(i).y == points.get(i + 1).y) {
                continue;
            }

            // 無効な点があれば描画しない
            if(points.get(i).isValid && points.get(i + 1).isValid){
                g.drawLine(points.get(i).x, points.get(i).y, points.get(i + 1).x, points.get(i + 1).y);
            }
        }
    }

    //点クラス
    private class Point {
        public int x; // 点のx座標
        public int y; // 点のy座標
        public boolean isValid = false; // 有効な点かどうか
        public Point(double x, double y, boolean isValid) {
            this.x = (int)x;
            this.y = (int)y;
            this.isValid = isValid;
        }
    }

    // 画像として保存するメソッド
    public void savePicture(String filename) {
        BufferedImage readImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_BGR);
        Graphics2D g = readImage.createGraphics();
        drawGraph(g);
        try {
            ImageIO.write(readImage, "png", new File(filename + ".png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //軸を描画する
    private void drawAxis(Graphics g) {
        g.setColor(Color.GREEN);
        // x軸の描画
        g.drawLine(originX - xAxisLength/2, originY, originX + xAxisLength/2, originY);
        g.drawLine(originX + xAxisLength/2, originY, 
            originX + xAxisLength/2 - arrowheadSize, originY - arrowheadSize);
        g.drawLine(originX + xAxisLength/2, originY, 
            originX + xAxisLength/2 - arrowheadSize, originY + arrowheadSize);
        // y軸の描画
        g.drawLine(originX, originY - yAxisLength/2, originX, originY + yAxisLength/2);
        g.drawLine(originX, originY - yAxisLength/2, originX + arrowheadSize, 
            originY - yAxisLength/2 + arrowheadSize);
        g.drawLine(originX, originY - yAxisLength/2, originX - arrowheadSize, 
            originY - yAxisLength/2 + arrowheadSize);
    }

    // 目盛りを描画する
    private void drawScale(Graphics g) {
        
        // x軸の目盛りを描画
        for(int i = -4; i <= 4; i++) {
            if(i == 0) continue;
            int x = originX + i * gridInterval;
            int y1 = originY - xAxisLength/2;
            int y2 = originY + xAxisLength/2;
            g.setColor(new Color(0, 80, 0));
            g.drawLine(x, y1, x, y2);
            g.setColor(new Color(255, 0, 0));
            g.drawString(String.valueOf(i * xScale), x, originY + 10);
        }
        // y軸の目盛りを描画
        for(int i = -4; i <= 4; i++) {
            if(i == 0) continue;
            int x1 = originX - yAxisLength/2;
            int x2 = originX + yAxisLength/2;
            int y = originY + i * gridInterval;
            g.setColor(new Color(0, 80, 0));
            g.drawLine(x1, y, x2, y);
            g.setColor(new Color(255, 0, 0));
            int stringWidth = g.getFontMetrics().stringWidth(String.valueOf(-i * yScale));
            g.drawString(String.valueOf(-i * yScale), originX - stringWidth, y + 10);
        }
        g.setColor(new Color(255, 0, 0));
        g.drawString("0", originX - 10, originY + 10);
    }

    // グラフに式を描画するメソッド
    private void drawFormula(Graphics g) {
        String x_text = "x(t) = " + xText;
        String y_text = "y(t) = " + yText;
        int xStringWidth = g.getFontMetrics().stringWidth(x_text);
        int yStringWidth = g.getFontMetrics().stringWidth(y_text);
        int maxSiringWidth = Math.max(xStringWidth, yStringWidth);
        g.drawString(x_text, getWidth() - 50 - maxSiringWidth, getHeight() - 20);
        g.drawString(y_text, getWidth() - 50 - maxSiringWidth, getHeight() - 8);
    }
}
