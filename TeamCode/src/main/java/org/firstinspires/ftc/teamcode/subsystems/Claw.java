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

    }

    @Override
    public void stop() {

    }

    public enum ClawState {
        INTAKE,
        OPEN,

    }


    public ClawState Claw = ClawState.OPEN;

    // LOOK FORM INTAKE
    public static double intakeRight = 0.03 , intakeLeft = 0.97;
    public static double releaseRight =  0.3 , releaseLeft = 0.7 ;

    public static double targetRight = 0 , targetLeft = 0;


    public Claw() {
        this.robot = RobotHardware.getInstance();
        updateState(ClawState.OPEN);
    }

    public void update() {
        updateState(Claw);
    }

    public void updateState(@NotNull ClawState state) {

        switch (state) {

            case INTAKE:

                targetRight = intakeRight;
                targetLeft = intakeLeft;

                break;

            case OPEN:

                targetRight = releaseRight;
                targetLeft = releaseLeft;

                break;

            default:

                state = ClawState.INTAKE;
        }


        robot.clawRightServo.setPosition(targetRight);
        robot.clawLeftServo.setPosition(targetLeft);
    }





    public void setClaw(ClawState state) {
        this.Claw = state;

        update();
    }
}