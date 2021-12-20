import javax.swing.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Random;
import java.awt.event.ActionEvent;
import javax.swing.*;

/** GUI componenet for inverted pendulum game. Maintains and visualizes game state and response to mouse inputs. Allows programmatic control of game settings and property access to game state. */
public class GameComponent extends JPanel{
    /** Duration each phase is active for [ms]. */
    private int phaseDuration = 10000;

    /** Strength of gravity. */
    private int gravity;

    /** Length of the pendulum. */
    private final int length = 10;

    /** Encapsulates state of pendulum. */
    private Pendulum pendulum = new Pendulum();

    /** Timer to trigger changing phases. */
    private Timer timer;

    /** Time that the timer is started. */
    private long timerStartTime;

    /** Whether a game is currently being played. */
    private boolean isActive = false;

    /** Current phase of the game. */
    private Phase phase;

    /** Construct a new GameComponent with default settings. */
    public GameComponent(){
        timer = new Timer(phaseDuration, (ActionEvent e) -> timeout());
        timer.setInitialDelay(0);
        timer.setCoalesce(true);
        timerStartTime = System.currentTimeMillis();

        phase = Phase.NOTSTARTED;

        gravity = 10;

        setPreferredSize(new Dimension(480, 360));
    }

    /** Start a new game using current settings. Progress from any previous or ongoing game is reset. */
    public void startGame(){
        isActive = true;
        timer.restart();
        phase = Phase.POSITION;
        repaint();
    }

    /** Stop current game. Takes effect immediately (triggers a repain request). All background tasks are cancelled when game is stopped. */
    public void stopGame(){
        timer.stop();
        isActive = false;
        repaint();
    }

    /** Handle timer actions by changing phases. If the phase is not yet on jerk, go to the next phase and repain. Otherwise, stop the game and announce that the game is won. If no game is currently active, do nothing. */
    private void timeout(){
        if (!isActive) return;
        timerStartTime = System.currentTimeMillis();
        switch(phase){
            case POSITION:
                //NOT YET IMPLEMENTED FULLY
        }
    }

    /** Returns the current phase as a string. */
    public String getPhase(){
        return phase.toString();
    }

    /** Changes the current phase to `newPhase` and notifies observers. */
    private void setPhase(Phase newPhase){
        Phase oldPhase = phase;
        phase = newPhase;
        firePropertyChange("GamePhase", oldPhase, newPhase);
    }

    /** Returns the current time remaining in the phase [ms]. */
    public int timeRemaining(){
        return phaseDuration - (int)(System.currentTimeMillis() - timerStartTime);
    }

    /** Returns the length of each phase [ms]. */
    public int getPhaseDuration(){
        return phaseDuration;
    }

    /** Sets the gravity in the simulation to `newGravity`. */
    public void setGravity(int newGravity){
        gravity = newGravity;
    }

    /** Returns the current value of the gravity. */
    public int getGravity(){
        return gravity;
    }

    /** Changes the phase duration to `newPhaseDuration` [ms].*/
    public void setPhaseDuration(int newPhaseDuration){
        phaseDuration = newPhaseDuration;
    }

    /** Visualize the current game state by painting on `g`. If game is inactive, fill component area with black. Otherwise, draw pendulum at current location on top of default JPanel background. */
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        if(isActive){
            pendulum.paintPendulum(g);
        }else{
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        repaint();
    }

    /** Represents the pendulum the game is based on. Maintains current angle and whether it is currently horizontal. Able to update itself according to physics and paint itself. */
    private class Pendulum{
        int x;
        int y = 100;
        double theta;
        double thetaDot;
        double thetaDoubleDot;
        boolean horizontal = false;

        public void step(int timeElapsed){
            thetaDoubleDot = gravity / length * Math.sin(theta); //ADD USER CONTROL
            thetaDot += thetaDoubleDot * timeElapsed / 1000;
            theta += thetaDot * timeElapsed / 1000;
            testHorizontal();
        }

        private void testHorizontal(){
            if(Math.abs(theta) > Math.PI){
                theta = Math.copySign(Math.PI, theta);
                horizontal = true;
                stopGame();
            }
        }

        public void paintPendulum(Graphics g){
            g.setColor(Color.BLUE);
            g.drawLine(x, y, x+(int)(length * Math.sin(theta)), y+(int)(length * Math.cos(theta)));
        }

        public Pendulum(){
            theta = 0.01;
            thetaDot = 0;
            thetaDoubleDot = 0;
            x = 100; //ADD USER INTERACTION
        }
    }
}
