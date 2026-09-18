import java.awt.*;
import javax.swing.*;

public class SolitaireGUI {
    public static void createAndShowGUI() {
        JFrame frame = new JFrame("Peg Solitaire — Sprint 0 Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Sprint 0 GUI demo: text, lines, checkbox, radio buttons");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        JSeparator sep1 = new JSeparator(SwingConstants.HORIZONTAL);
        sep1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        sep1.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(sep1);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        JCheckBox hintBox = new JCheckBox("Enable move hints");
        hintBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(hintBox);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        radioPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JRadioButton orthogonal = new JRadioButton("Orthogonal");
        JRadioButton diagonal = new JRadioButton("Diagonal");
        ButtonGroup group = new ButtonGroup();
        group.add(orthogonal);
        group.add(diagonal);
        orthogonal.setSelected(true);
        radioPanel.add(new JLabel("Move type:"));
        radioPanel.add(orthogonal);
        radioPanel.add(diagonal);
        panel.add(radioPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));

        // simple custom line (horizontal rule) drawn as a component
        JComponent line = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.GRAY);
                int y = getHeight() / 2;
                g.drawLine(0, y, getWidth(), y);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(400, 12);
            }
        };
        line.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(line);

        frame.getContentPane().add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
