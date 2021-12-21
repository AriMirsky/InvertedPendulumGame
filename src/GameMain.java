import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowListener;
import java.util.*;
/** Main class for the inverted pendulum game. Creates window with game board, time remaining, start button, and sliders for time and gravity. */
public class GameMain {
    /** Create application window.
     * 
     * Window title is "Inverted Pendulum Game"
     * Game board is in center of window, expands to fill window size
     * Current phase and time remaining is at top; text is centered
     * Start button is at bottom
     * Gravity slider is at right
     * Time slider is at left
     */
    public static void createAndShowGUI(){
        JFrame frame = new JFrame("Inverted Pendulum Game");
        GameComponent game = new GameComponent();
        frame.add(game);

        JLabel phaseLabel = new JLabel("Current phase: " + game.getPhase(), SwingConstants.CENTER);
        JLabel timeLabel = new JLabel("Time remaining: " + game.timeRemaining(), SwingConstants.CENTER);
        frame.add(stackComponents(phaseLabel, timeLabel), BorderLayout.NORTH);

        JButton startButton = new JButton("Start");
        startButton.setFont(startButton.getFont().deriveFont(20.0f));

        JSlider gravitySlider = new JSlider(JSlider.VERTICAL, 1, 500, game.getGravity());
        addSliderLabels(gravitySlider, "Low", "High");
        frame.add(makeSliderPanel(gravitySlider, "Gravity strength"), BorderLayout.EAST);

        JSlider timeSlider = new JSlider(JSlider.VERTICAL, 1000, 20000, game.getPhaseDuration());
        addSliderLabels(timeSlider, "Fast", "Slow");
        frame.add(makeSliderPanel(timeSlider, "Phase duration"), BorderLayout.WEST);

        JSlider positionSlider = new JSlider(JSlider.HORIZONTAL, 0, 480, 240);
        JComponent posSliderPanel = makeSliderPanel(positionSlider, "Position");
        JPanel posSliderBorder = new JPanel();
        posSliderBorder.setLayout(new BoxLayout(posSliderBorder, BoxLayout.LINE_AXIS));
        posSliderBorder.add(Box.createRigidArea(new Dimension(156, 0)));
        posSliderBorder.add(posSliderPanel);
        posSliderBorder.add(Box.createRigidArea(new Dimension(163, 0)));
        JComponent temp = stackComponents(posSliderBorder, startButton);
        frame.add(temp, BorderLayout.SOUTH);
        // ADD VELOCITY / ACCELERATION / JERK HERE


        startButton.addActionListener(e -> {game.startGame();});
        game.addPropertyChangeListener("GamePhase", e -> {phaseLabel.setText("Current phase: " + game.getPhase());});
        game.addPropertyChangeListener("GameTime", e -> {timeLabel.setText("Time remaining: " + (game.timeRemaining() / 1000.0) + " s");}); //POSSIBLY BETTER MORE EFFICIENT WAY OF DOING THIS
        gravitySlider.addChangeListener(e -> {game.setGravity(gravitySlider.getValue());});
        timeSlider.addChangeListener(e -> {game.setPhaseDuration(timeSlider.getValue());});
        positionSlider.addChangeListener(e -> {game.setPosition(positionSlider.getValue());});

        frame.pack();
        frame.setVisible(true);
    }

    /** Label `slider`'s minimum value with `minLabel` and its maximum value with `maxLabel`. */
    private static void addSliderLabels(JSlider slider, String minLabel, String maxLabel){
        Hashtable<Integer, JLabel> labels = new Hashtable<>();

        labels.put(slider.getMinimum(), new JLabel(minLabel));
        labels.put(slider.getMaximum(), new JLabel(maxLabel));

        slider.setLabelTable(labels);
        slider.setPaintLabels(true);
    }

    /** Place `slider` in a new padded panel with top label `title` and return the panel. */
    private static JComponent makeSliderPanel(JSlider slider, String title){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font(label.getFont().getName(), Font.PLAIN, label.getFont().getSize() * 2));
        panel.add(label);

        panel.add(slider);

        return panel;
    }

    /** Returns a component with `top` on top of `bottom`, both center aligned. */
    private static JComponent stackComponents(JComponent top, JComponent bottom){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        top.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottom.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(top);
        panel.add(bottom);

        return panel;
    }
}
