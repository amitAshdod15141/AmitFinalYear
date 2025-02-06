package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.R;
import org.firstinspires.ftc.teamcode.RobotHardware;
import org.firstinspires.ftc.teamcode.util.BetterGamepad;
import org.jetbrains.annotations.NotNull;

@Config
public class OuttakeExtension implements Subsystem{

    RobotHardware robot;

    Servo sEL;
    Servo sER;

    public static double maxPower = 0.75;


    double currentTargetRight = 0 ,currentTargetLeft = 0;
    public static double limitMultiplier = 0.6;
    public static double fullLengthLeft = 0.03, fullLengthRight = 0.63 ,
            halfLength = 0,
            closedRight = 1, closedLeft = 0.005;

    public enum Target {

        FULL_LENGTH,
        HALF_LENGTH,
        CLOSED

    }
    Gamepad gamepad;
    BetterGamepad cGamepad;

    Target target = Target.CLOSED;

    public OuttakeExtension (Gamepad gamepad)
    {
        this.robot = RobotHardware.getInstance();
        this.gamepad = gamepad;
        this.cGamepad = new BetterGamepad(gamepad);
        this.sEL = robot.hardwareMap.get(Servo.class, "sEL");
        this.sEL.setDirection(Servo.Direction.REVERSE);
        this.sER = robot.hardwareMap.get(Servo.class, "sER");

    }

    public void update ()
    {
        switch(target)
        {
            case CLOSED:

                currentTargetRight = closedRight;
                currentTargetLeft = closedLeft;
                break;

            case HALF_LENGTH:

                currentTargetRight = halfLength;
                currentTargetLeft = halfLength;

                break;

            case FULL_LENGTH:

                currentTargetRight = fullLengthRight;
                currentTargetLeft = fullLengthLeft;

                break;

            default:

                currentTargetRight = closedRight;
                currentTargetLeft = closedLeft;
        }



        sEL.setPosition(currentTargetLeft);
        sER.setPosition(currentTargetRight);


    }


    public void setTarget (@NotNull Target chooseTarget)
    {
        this.target = chooseTarget;

        update();

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
}
