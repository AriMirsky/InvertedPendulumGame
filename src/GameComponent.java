import javax.swing.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;

/** GUI componenet for inverted pendulum game. Maintains and visualizes game state and response to mouse inputs. Allows programmatic control of game settings and property access to game state. */
public class GameComponent extends JPanel{
    /** Duration each phase is active for [ms]. */
    private int phaseDuration = 10000;

    /** Strength of gravity. */
    private int gravity;

    /** Length of the pendulum. */
    private final int length = 100;

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

        gravity = 100;

        setPreferredSize(new Dimension(480, 360));
    }

    /** Start a new game using current settings. Progress from any previous or ongoing game is reset. */
    public void startGame(){
        isActive = true;
        timer.restart();
        phase = Phase.POSITION;
        repaint();
        mainGameLoop();
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
        /** The width of the pendulum when drawn to the screen. */
        int width;

        /** The horizontal position of pivot of the pendulum. */
        int x;

        /** The vertical position of the pivot of the pendulum. */
        int y = 200;

        /** The angle between the pendulum and the vertical. */
        double theta;

        /** The rate of change of theta. */
        double thetaDot;

        /** The rate of change of thetaDot. */
        double thetaDoubleDot;

        /** Whether the pendulum is horizontal. If it is, the game is over. */
        boolean horizontal = false;

        int xUsed;

        int xPrev;

        double xDot;

        double xDoubleDot;

        /** Simulate the physics of the pendulum after a `timeElapsed` [ms] amount of time. */
        public void step(int timeElapsed){
            thetaDoubleDot = ((double)gravity) / length * Math.sin(theta) + xDoubleDot * Math.cos(theta) / length;
            thetaDot += thetaDoubleDot * timeElapsed / 1000;
            theta += thetaDot * timeElapsed / 1000;
            xDoubleDot = ((xUsed - xPrev) / timeElapsed - xDot)/timeElapsed * 500;
            xDot = xUsed - xPrev;
            xPrev = xUsed;
            xUsed = x;
            testHorizontal();
            firePropertyChange("GameTime", 0, 0);
        }

        /** If the pendulum is horizontal, stop the game and notify everything which should know. */
        private void testHorizontal(){
            if(Math.abs(theta) > (Math.PI/2)){
                theta = Math.copySign(Math.PI, theta);
                horizontal = true;
                stopGame();
            }
        }

        /** Draws the pendulum on the object `g`. */
        public void paintPendulum(Graphics g){
            g.setColor(Color.BLUE);
            double cosTheta = Math.cos(theta);
            double sinTheta = Math.sin(theta);
            double width2 = width / 2;
            int[] pointsX = {(int)(x + cosTheta * width2), (int)(x - cosTheta * width2), (int)(x + sinTheta * length - cosTheta * width2), (int)(x + sinTheta * length + cosTheta * width2)};
            int[] pointsY = {(int)(y + sinTheta * width2), (int) (y - sinTheta * width2), (int) (y - cosTheta * length - sinTheta * width2), (int) (y - cosTheta * length + sinTheta * width2)};
            g.fillPolygon(pointsX, pointsY, 4);
        }

        /** Creates a pendulum with default parameters. */
        public Pendulum(){
            theta = 0.1;
            thetaDot = 0;
            thetaDoubleDot = 0;
            x = 240;
            xUsed = x;
            xPrev = x;
            width = 20; //ADD BETTER LOCATION
        }
    }

    /** Main loop for the game. Periodically updates the pendulum, and is responsible for changing the current user interaction method based on phase. */
    private void mainGameLoop(){
        Thread mainThread = new Thread(() -> {while(phase != Phase.WINNER && phase != Phase.LOSER){
            pendulum.step(1000 / 60);
            //System.out.println(pendulum.thetaDoubleDot);
            repaint();
            try {
                Thread.sleep(1000 / 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }});
        mainThread.start();
        Thread killThread = new Thread(() -> { //DOESN'T CURRENTLY WORK
            try {
                mainThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Calculation thread stopped.");
            System.exit(1);
        });
        killThread.start();
    }

    /** Changes the position of the pivot of the pendulum and updates pendulum accordingly. */
    public void setPosition(int newPosition){
        pendulum.x = newPosition;
    }
}
