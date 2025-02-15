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
@TeleOp(name = "Outtake Test", group = "A")
public class OuttakeTest extends CommandOpMode {

    private final RobotHardware robot = RobotHardware.getInstance();
    BetterGamepad gamepadEx;
    Outtake outtake;
    Claw claw;

    public static Outtake.Angle angle = Outtake.Angle.INTAKE;
    public static boolean DEBUG = true;


    @Override
    public void run() {
        outtake.update();

        if(!DEBUG)
        {
            if(gamepad1.right_bumper)
            {
                claw.updateState(Claw.ClawState.INTAKE);
                outtake.setAngle(Outtake.Angle.OUTTAKE);
            }
            else if(gamepad1.left_bumper)
            {
                outtake.setAngle(Outtake.Angle.INTAKE);
                claw.updateState(Claw.ClawState.OPEN);
            }
        }
        else
        {
            outtake.setAngle(angle);
        }

        telemetry.update();
    }

    @Override
    public void initialize() {
        gamepadEx = new BetterGamepad(gamepad1);
        robot.init(hardwareMap, telemetry);

        outtake = new Outtake();
        claw = new Claw();
    }

}