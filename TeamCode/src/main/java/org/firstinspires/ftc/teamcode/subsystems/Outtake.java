package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.teamcode.RobotHardware;
import org.jetbrains.annotations.NotNull;
@Config
public class Outtake implements Subsystem {

    private final RobotHardware robot;

    public static double almostIntakeHandPivot = 0.35, intakeHandPivot = 0.5;
    public static double outtakeHandPivot = 0.75;
    public static double floorHandPivot = 0, goBackRelease = 0.05;

    public static double releaseSampleHand = 0.65 ;
    public static double defaultOuttakeHandPivot = 0.7;
    public static double hangHand = 0.6;

    public static double power = 1;

    public static void setOuttakeHandPivot(double outtakeHandPivot) {
        Outtake.outtakeHandPivot = outtakeHandPivot;
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
        RELEASE_SAMPLE

    }

    public enum Type {
        HAND
    }

    Angle angle = Angle.INTAKE;

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
                    case ALMOST_INTAKE:
                        this.robot.outtakeHandServo.setPosition(almostIntakeHandPivot);

                        break;
                    case INTAKE:
                        this.robot.outtakeHandServo.setPosition(intakeHandPivot);

                        break;
                    case OUTTAKE:
                        this.robot.outtakeHandServo.setPosition(outtakeHandPivot);
                        break;
                    case FLOOR:
                        this.robot.outtakeHandServo.setPosition(floorHandPivot);
                        break;
                    case HANG:
                        this.robot.outtakeHandServo.setPosition(hangHand);
                        break;
                    case RELEASE_SAMPLE:
                        this.robot.outtakeHandServo.setPosition(releaseSampleHand);
                        break;
                }
                break;
        }
    }


    public void releasePixel()
    {
        outtakeHandPivot -= goBackRelease;
    }
    public void resetOuttake()
    {

        outtakeHandPivot = defaultOuttakeHandPivot;
    }

}