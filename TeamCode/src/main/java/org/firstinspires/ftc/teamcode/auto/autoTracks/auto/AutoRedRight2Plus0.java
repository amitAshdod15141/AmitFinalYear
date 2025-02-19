package org.firstinspires.ftc.teamcode.auto.autoTracks.auto;

import static com.acmerobotics.roadrunner.ftc.Actions.runBlocking;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.RobotHardware;
import org.firstinspires.ftc.teamcode.auto.AutoConstants;
import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.Elevator;
@Config
@Autonomous(name = "2+0 MTI RedRight" , group = "AutoRed")
public class AutoRedRight2Plus0 extends LinearOpMode {
    private final RobotHardware robot = RobotHardware.getInstance();
    ElapsedTime time;

    // subsystems
    Elevator elevator;

    Claw claw;


    AutoConstants autoConstants;
    public static int PIXEL_EXTENSION = 200;

    boolean vision = true;

    public static int tempHeight = 900;
    boolean first = true;

    SequentialAction runMiddle;
    SequentialAction runRight;
    SequentialAction runLeft;

    @Override
    public void runOpMode() {
        first = true;

        time = new ElapsedTime();

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());


        autoConstants = new AutoConstants();


        elevator = new Elevator(true);

        claw = new Claw();
        elevator.setAuto(true);


        Action trajRedLeft =
                robot.drive.actionBuilder(robot.drive.pose)


                        .build();


        runLeft = new SequentialAction(trajRedLeft);

        while (opModeInInit() && !isStopRequested()) {

            telemetry.addLine("Initialized");
            telemetry.update();
        }

        waitForStart();

        if (isStopRequested()) return;


        runBlocking(new ParallelAction(runLeft));


    }
}







