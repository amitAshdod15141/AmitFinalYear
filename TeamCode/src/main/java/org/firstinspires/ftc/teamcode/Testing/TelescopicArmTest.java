package org.firstinspires.ftc.teamcode.Testing;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.CommandScheduler;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.RobotHardware;
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Elevator;
import org.firstinspires.ftc.teamcode.subsystems.TelescopicHand;
import org.firstinspires.ftc.teamcode.util.BetterGamepad;

@Config
@TeleOp(name = "TelescopicArm Test", group = "A")
public class TelescopicArmTest extends LinearOpMode {

    private final RobotHardware robot = RobotHardware.getInstance();

    TelescopicHand telescopicHand;
    BetterGamepad gamepadEx;
    public static double target = 0;

    @Override
    public void runOpMode() {
        gamepadEx = new BetterGamepad(gamepad1);

        robot.init(hardwareMap, telemetry);

        telescopicHand = new TelescopicHand(gamepad1, true, true);
        telescopicHand.setAuto(false);


        telescopicHand.update();

        waitForStart();

        while (opModeIsActive())
        {
            gamepadEx.update();

            if(gamepad1.right_stick_y != 0)
            {
                telescopicHand.setUsePID(false);
            }
            else
            {
                telescopicHand.setUsePID(true);

            }
            telescopicHand.setTarget(target);

            telescopicHand.update();

        }
    }

}