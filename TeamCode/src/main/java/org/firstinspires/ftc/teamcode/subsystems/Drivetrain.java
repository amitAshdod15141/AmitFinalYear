package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.RobotHardware;
import org.firstinspires.ftc.teamcode.util.BetterGamepad;

@Config
public class Drivetrain{

    boolean blueAlliance;

    private BetterGamepad _cGamepad1;
    boolean first = true;

    private final RobotHardware robot;
    public static double maxPower = 1;
    public static double slowerSpin = 0.8;
    public double power = 0, twist = 0;
    double botHeading = 0, y = 0, x = 0, rotY = 0, rotX = 0;

    //Constructor
    public Drivetrain(Gamepad gamepad1, boolean blueAlliance, boolean debug)
    {
        this.robot = RobotHardware.getInstance();

        // gamepad helper to see if pressed button once
        this._cGamepad1 = new BetterGamepad(gamepad1);

        power = maxPower;

        this.blueAlliance = blueAlliance;

        resetAngle(debug);
    }

    public void update() {
        _cGamepad1.update();


        y = Range.clip(-_cGamepad1.left_stick_y, -power, power); // Remember, Y stick value is reversed
        x = Range.clip(_cGamepad1.left_stick_x, -power, power);
        twist = Range.clip(_cGamepad1.right_stick_x, -power * slowerSpin, power * slowerSpin);

        if (_cGamepad1.XOnce()) {
            robot.imu.resetYaw();
        }

        double botHeading = robot.imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS) + robot.getImuOffset();

        // Rotate the movement direction counter to the bot's rotation
        double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
        double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

        rotX = rotX * 1.1;  // Counteract imperfect strafing


        robot.dtFrontRightMotor.setPower(rotY - rotX - twist) ;
        robot.dtBackRightMotor.setPower(rotY + rotX - twist) ;
        robot.dtFrontLeftMotor.setPower(rotY + rotX + twist) ;
        robot.dtBackLeftMotor.setPower(rotY - rotX + twist) ;

    }
    public void resetAngle(boolean debug)
    {
        robot.imu.resetYaw();
    }


    public void fast()
    {
        power = maxPower;
        slowerSpin = 0.8;
    }

    public void superSlow()
    {
        power = maxPower / 2.3;
        slowerSpin = 0.6;
    }

    public double getAngle()
    {
        return robot.imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS) + robot.getImuOffset();
    }
}
