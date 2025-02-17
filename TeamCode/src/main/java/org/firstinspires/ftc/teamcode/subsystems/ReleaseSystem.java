package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.teamcode.RobotHardware;
import org.jetbrains.annotations.NotNull;

@Config
public class ReleaseSystem  implements Subsystem {

    private RobotHardware robot;

    public static double outtakeHandPivot = 0.33 ,intakeHandPivot = 1;

    public static double outtakeSpinRight = 0.87, outtakeSpinMiddle= 0.88, outtakeSpinLeft = 0.1505;
    public static double hangHand = 0.6;

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
        OUTTAKE,
        FLOOR,
        HANG,

        SPIN_RIGHT,
        SPIN_LEFT,

        SPIN_MIDDLE

    }

    public enum Type {
        SPIN,
        HAND

    }

    Angle angle = Angle.INTAKE;

    public ReleaseSystem() {
        this.robot = RobotHardware.getInstance();
    }


    public void update() {
        updateState(Type.HAND);
        updateState(Type.SPIN);

    }

    public void setAngle(@NotNull Angle angle) {
        this.angle = angle;

        updateState(Type.HAND);
        updateState(Type.SPIN);
    }

    public void updateState(@NotNull Type type) {


        switch (type) {
            case SPIN:
                switch (angle) {
                    case SPIN_RIGHT:
                        this.robot.clawSpinServo.setPosition(outtakeSpinRight);
                    case SPIN_LEFT:
                        this.robot.clawSpinServo.setPosition(outtakeSpinLeft);
                        break;
                    case SPIN_MIDDLE:
                        this.robot.clawSpinServo.setPosition(outtakeSpinMiddle);
                        break;
                }
            case HAND:
                switch (angle) {
                    case INTAKE:
                        this.robot.clawHandServo.setPosition(intakeHandPivot);
                        break;
                    case OUTTAKE:
                        this.robot.clawHandServo.setPosition(outtakeHandPivot);
                        break;
                    case HANG:
                        this.robot.clawHandServo.setPosition(hangHand);
                        break;
                }
                break;
        }
    }

}

