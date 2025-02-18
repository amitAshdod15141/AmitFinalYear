

package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.teamcode.RobotHardware;
import org.firstinspires.ftc.teamcode.util.ClawSide;
import org.jetbrains.annotations.NotNull;

@Config
public class Claw implements Subsystem {
    private final RobotHardware robot;

    @Override
    public void play() {

    }

    @Override
    public void loop(boolean allowMotors) {
        updateState(leftClaw, ClawSide.LEFT);
        updateState(rightClaw, ClawSide.RIGHT);
    }

    @Override
    public void stop() {

    }

    public enum ClawState {
        CLOSED,
        INTAKE,
        OPEN,

    }

    public ClawState leftClaw = ClawState.OPEN;
    public ClawState rightClaw = ClawState.OPEN;

    // LOOK FORM INTAKE
    public static double intakeRight = 0.03 , intakeLeft = 0.;
    public static double releaseRight =  0.3 , releaseLeft = 0.7 ;


    public Claw() {
        this.robot = RobotHardware.getInstance();
        updateState(ClawState.OPEN, ClawSide.BOTH);
    }

    public void update() {
        updateState(leftClaw, ClawSide.LEFT);
        updateState(rightClaw, ClawSide.RIGHT);
    }

    public void updateState(@NotNull ClawState state, @NotNull ClawSide side) {
        double positionR = getClawStatePositionRight(state);
        double positionL = getClawStatePositionLeft(state);
        switch (side) {
            case LEFT:
                robot.clawLeftServo.setPosition(positionL);
                this.leftClaw = state;
                break;
            case RIGHT:
                robot.clawRightServo.setPosition(positionR);
                this.rightClaw = state;
                break;
            case BOTH:
                positionL = getClawStatePositionLeft(state);
                robot.clawLeftServo.setPosition(positionL);
                this.leftClaw = state;
                positionR = getClawStatePositionRight(state);
                robot.clawRightServo.setPosition(positionR);
                this.rightClaw = state;
                break;
        }


    }

    private double getClawStatePositionRight(ClawState state) {

                switch (state) {

                    case INTAKE:
                        return intakeRight;
                    case OPEN:
                        return releaseRight;
                    default:
                        return 0.03;
                }

        }

    private double getClawStatePositionLeft(ClawState state) {

        switch (state) {

            case INTAKE:
                return intakeLeft;
            case OPEN:
                return releaseLeft;
            default:
                return 0.97;
        }

    }



    public void setLeftClaw(ClawState leftClaw) {
        this.leftClaw = leftClaw;
    }

    public void setRightClaw(ClawState rightClaw) {
        this.rightClaw = rightClaw;
    }

    public void setBothClaw(ClawState state) {
        this.rightClaw = state;
        this.leftClaw = state;
        update();
    }
}


