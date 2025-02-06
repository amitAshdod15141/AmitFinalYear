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
    public static double intake = 0;
    public static double release =  0.05;

    public static double target = 0;


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

                target = intake;

                break;

            case OPEN:

                target = release;

                break;

            default:

            state = ClawState.INTAKE;
        }


        robot.outtakeClawServo.setPosition(target);
    }





    public void setClaw(ClawState state) {
        this.Claw = state;

        update();
    }
}