package org.firstinspires.ftc.teamcode.Testing;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.CommandOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.RobotHardware;
import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.Outtake;
import org.firstinspires.ftc.teamcode.util.BetterGamepad;
import org.firstinspires.ftc.teamcode.util.ClawSide;

@Config
@TeleOp(name = "OuttakeExtension  Test", group = "A")
public class OuttakeExtensionTest extends CommandOpMode {

    private final RobotHardware robot = RobotHardware.getInstance();
    BetterGamepad gamepadEx;

    public static double move = 0;


    public static Outtake.Angle angle = Outtake.Angle.INTAKE;
    public static boolean DEBUG = true;


    @Override
    public void run() {


        if (gamepad1.right_bumper) {
            robot.sEL.setPosition(move);
            robot.sER.setPosition(move);
        } else if (gamepad1.left_bumper) {
            robot.sEL.setPosition(0);
            robot.sER.setPosition(0);
        }


        telemetry.update();
    }

    @Override
    public void initialize() {
        gamepadEx = new BetterGamepad(gamepad1);
        robot.init(hardwareMap, telemetry);


    }

}