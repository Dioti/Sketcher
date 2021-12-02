import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

@SuppressWarnings("serial")
public class LSystemTree extends JFrame{
    JButton generateBut;
    int currentAction = 1;

    public static void main(String[] args) {
        new LSystemTree();
    }

    public LSystemTree() {
        this.setSize(600, 600);
        this.setTitle("L-System Tree");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        JPanel buttonPanel = new JPanel();
        Box box = Box.createHorizontalBox();
        generateBut = makeButton("Generate", 1);
        box.add(generateBut);
        buttonPanel.add(box);
        Map<String, String> rules = new HashMap<>();
        rules.put("F", "FF+[+F-F-F]-[-F+F+F]");
        this.add(buttonPanel,  BorderLayout.NORTH);
        this.add(new TreeDrawing("F", 22.5, rules), BorderLayout.CENTER);
        this.setVisible(true);
    }

    public JButton makeButton(String text, final int actionNum) {
        JButton theBut = new JButton();
        theBut.setText(text);
        theBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentAction = actionNum;
                System.out.println("actionNum: " + actionNum);
                repaint();
            }
        });
        return theBut; 
    }

    private class TreeDrawing extends JComponent{
        private String axiom;
        private String sentence;
        private double angle;
        private Map<String, String> rules;
        private int len;
        private Stack<AffineTransform> transformStack;
        public TreeDrawing(String axiom, double angle, Map<String, String> rules) {
            this.axiom = axiom;
            this.sentence = axiom;
            this.angle = Math.toRadians(angle);
            this.rules = rules;
            len = 100;
            transformStack = new Stack<>();
        }

        public void generate() {
            len /= 2;
            String nextSentence = "";
            for(int i = 0; i < sentence.length(); i++) {
                char current = sentence.charAt(i);
                boolean found = false;
                if(rules.containsKey(String.valueOf(current))) {
                    found = true;
                    nextSentence += rules.get(String.valueOf(current));
                }
                if(!found) {
                    nextSentence += current;
                }
            }
            sentence = nextSentence;
        }

        private void turtleDraw(Graphics2D g2d) {
            g2d.translate(getWidth() / 2, getHeight());
            for(int i = 0; i < sentence.length(); i++) {
                char current = sentence.charAt(i);
                if(current == 'F') {
                    g2d.drawLine(0, 0, 0, -len);
                    g2d.translate(0, -len);
                } else if(current == '+') {
                    g2d.rotate(angle);
                } else if(current == '-') {
                    g2d.rotate(-angle);
                } else if(current == '[') {
                    transformStack.push(g2d.getTransform());
                } else if(current == ']') {
                    g2d.setTransform(transformStack.pop());
                }
            }
            generate();
            System.out.println(sentence);
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            turtleDraw(g2d);
            g2d.dispose();
        }
    }
}