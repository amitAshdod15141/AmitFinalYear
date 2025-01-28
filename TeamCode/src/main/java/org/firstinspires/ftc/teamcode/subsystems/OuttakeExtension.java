package org.firstinspires.ftc.teamcode.subsystems;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.RobotHardware;
import org.firstinspires.ftc.teamcode.util.BetterGamepad;
import org.firstinspires.ftc.teamcode.util.PIDFController;
import org.jetbrains.annotations.NotNull;

@Config
public class OuttakeExtension implements Subsystem {

    private final RobotHardware robot;
    double currentTarget = 0;
    public static double limitMultiplier = 1;
    public static double fullLength = 0, halfLength = 0, closed = 0;

    Gamepad gamepad;
    BetterGamepad cGamepad;



    public enum ExtensionPos {
        FULL_LENGTH,
        HALF_LENGTH,
        CLOSED

    }


    public enum TypeExetension
    {

        USE_PREMADE
    }

    ExtensionPos extensionPos = ExtensionPos.CLOSED;

    public OuttakeExtension(Gamepad gamepad) {
        this.robot = RobotHardware.getInstance();
        this.gamepad = gamepad;
        this.cGamepad = new BetterGamepad(gamepad);
    }


    public void updateState(@NotNull TypeExetension exetension) {

        if (gamepad.left_stick_x != 0) {
            robot.sEL.setPosition(currentTarget + (gamepad.left_stick_x * limitMultiplier));
            robot.sER.setPosition(currentTarget + (gamepad.left_stick_x * limitMultiplier));
        } else {
            robot.sEL.setPosition(0);
            robot.sER.setPosition(0);
        }


        switch (exetension)
        {
            case USE_PREMADE:

            switch (extensionPos)
            {
                case FULL_LENGTH:

                    currentTarget = fullLength;

                    break;

                case CLOSED:

                    currentTarget = closed;
                    break;

                case HALF_LENGTH:

                    currentTarget = halfLength;

                    break;

                default:

                    currentTarget = closed;

            }

            break;

            default: exetension = TypeExetension.USE_PREMADE;

        }



        robot.sEL.setPosition(currentTarget);
        robot.sER.setPosition(currentTarget);
    }


    public void update() {
        updateState(TypeExetension.USE_PREMADE);

    }



    public void setExtensionAngle(@NotNull ExtensionPos extensionAngle) {
        this.extensionPos = extensionAngle;

        updateState(TypeExetension.USE_PREMADE);
    }
    public void setTarget(double target) {

    }



    public double getPosRight() {
        return robot.sER.getPosition();
    }

    public double getPosLeft() {
        return robot.sEL.getPosition();
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
