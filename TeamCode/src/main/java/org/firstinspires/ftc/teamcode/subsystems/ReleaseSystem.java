package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.teamcode.RobotHardware;
import org.jetbrains.annotations.NotNull;

@Config
public class ReleaseSystem  implements Subsystem {

    private RobotHardware robot;

    public static double almostIntakeHandPivot = 0.35, intakeHandPivot = 0.22, intakeClawPivot = 0.25;
    public static double outtakeHandPivot = 0.7, outtakeClawPivot = .675;
    public static double floorHandPivot = 0.85, floorClawPivot = 0.5, goBackRelease = 0.05;

    public static double releaseStackHand = 0.65, releaseStackClaw = .4;
    public static double outtakeSpinIntake = 0.87, outtakeSpinOuttake = 0.88, outtakeSpin45 = 0.1505;
    double defaultOuttakeSpinOuttake = 0.875, defaultOuttakeHandPivot = 0.7;
    public static double outtakeSpinDouble = 0.0325;
    public static double hangHand = 0.6, handClaw = .4;

    public static double power = 1;

    public static void setOuttakeHandPivot(double outtakeHandPivot) {
        ReleaseSystem.outtakeHandPivot = outtakeHandPivot;
    }

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
        ALMOST_INTAKE,
        OUTTAKE,
        FLOOR,
        HANG,
        RELEASE_STACK

    }

    public enum Type {
        CLAW,
        HAND

    }

    Angle angle = Angle.INTAKE;

    public ReleaseSystem() {
        this.robot = RobotHardware.getInstance();
    }


    public void update() {
        updateState(Type.HAND);
        updateState(Type.CLAW);

    }

    public void setAngle(@NotNull Angle angle) {
        this.angle = angle;

        updateState(Type.HAND);
        updateState(Type.CLAW);
    }

    public void updateState(@NotNull Type type) {


        switch (type) {
            case CLAW:
                switch (angle) {
                    case INTAKE:
                    case ALMOST_INTAKE:
                        this.robot.clawSpinServo.setPosition(outtakeSpinIntake);
                        break;
                    case OUTTAKE:
                        this.robot.clawSpinServo.setPosition(outtakeSpinOuttake);
                        break;
                }
            case HAND:
                switch (angle) {
                    case ALMOST_INTAKE:
                        this.robot.clawHandServo.setPosition(almostIntakeHandPivot);
                        break;
                    case INTAKE:
                        this.robot.clawHandServo.setPosition(intakeHandPivot);
                        break;
                    case OUTTAKE:
                        this.robot.clawHandServo.setPosition(outtakeHandPivot);
                        break;
                    case FLOOR:
                        this.robot.clawHandServo.setPosition(floorHandPivot);
                        break;
                    case HANG:
                        this.robot.clawHandServo.setPosition(hangHand);
                        break;
                }
                break;
        }
    }

    public void spinOuttake(int direction) {
        outtakeSpinOuttake += (outtakeSpin45 * direction);

        outtakeSpinOuttake = Math.max(0, Math.min(1, outtakeSpinOuttake));

        this.robot.clawSpinServo.setPosition(outtakeSpinOuttake);
    }

    public void releasePixel() {
        outtakeHandPivot -= goBackRelease;
    }

    public void resetOuttake() {
        outtakeSpinOuttake = defaultOuttakeSpinOuttake;
        outtakeHandPivot = defaultOuttakeHandPivot;
    }

}

