package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.RobotHardware;
import org.firstinspires.ftc.teamcode.util.ClawSide;
import org.firstinspires.ftc.teamcode.util.wrappers.BetterSubsystem;
import org.jetbrains.annotations.NotNull;

@Config
public class Outtake implements Subsystem {

    private final RobotHardware robot;

    public static double  intakeHandLeftPivot = 0.3, intakeHandRightPivot = 0.96,  intakeClawPivot = 0.25;
    public static double outtakeHandRightPivot = 1, outtakeHandLeftPivot = 0;
    public static double goBackRelease = 0.05;

    public static double releaseStackHand = 0.65 , releaseStackClaw = .4;

    public static double hangHand = 0.6, handClaw = .4;

    public static double power = 1;


    @Override
    public void play() {

    }

    @Override
    public void loop(boolean allowMotors) {
        update();
    }

    @Override
    public void stop() {

    }

    public enum Angle {
        INTAKE,
        OUTTAKE,
        HANG,

    }

    public enum Type {
        HAND
    }

    Angle angle = Angle.OUTTAKE;

    public Outtake() {
        this.robot = RobotHardware.getInstance();
    }


    public void update() {
        updateState(Type.HAND);
    }

    public void setAngle(@NotNull Angle angle) {
        this.angle = angle;

        updateState(Type.HAND);
    }

    public void updateState(@NotNull Type type) {


        switch (type) {

            case HAND:
                switch (angle) {
                    case INTAKE:
                        this.robot.outtakeHandRightServo.setPosition(intakeHandRightPivot);
                        this.robot.outtakeHandLeftServo.setPosition(intakeHandLeftPivot);
                        break;
                    case OUTTAKE:
                        this.robot.outtakeHandRightServo.setPosition(outtakeHandRightPivot);
                        this.robot.outtakeHandLeftServo.setPosition(outtakeHandLeftPivot);
                        break;
                    case HANG:
                        this.robot.outtakeHandRightServo.setPosition(hangHand);
                        this.robot.outtakeHandLeftServo.setPosition(hangHand);

                        break;
                }
                break;
        }
    }



}