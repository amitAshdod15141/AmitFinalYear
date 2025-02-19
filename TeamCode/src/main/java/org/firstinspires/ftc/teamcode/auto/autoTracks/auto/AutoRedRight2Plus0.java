package org.firstinspires.ftc.teamcode.auto.autoTracks.auto;

import static com.acmerobotics.roadrunner.ftc.Actions.runBlocking;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.RobotHardware;
import org.firstinspires.ftc.teamcode.auto.AutoConstants;
import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.Elevator;
@Config
@Autonomous(name = "3+0 RedLeft" , group = "AutoRed")
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


        robot.init(hardwareMap ,telemetry, autoConstants.startPoseRedLeft);
        autoConstants = new AutoConstants();


        elevator = new Elevator(true);

        claw = new Claw();
        elevator.setAuto(true);


        Action trajRedLeft =
                robot.drive.actionBuilder(robot.drive.pose)

                        .strafeToLinearHeading(new Vector2d(-54, -54), Math.toRadians(45))
                        // TODO: Implement action for putting the sample

                        .strafeToLinearHeading(new Vector2d(-48, -42), Math.toRadians(90)) // Strafe left 13 inches
                        // TODO: Implement action for taking the sample

                        .strafeToLinearHeading(new Vector2d(-54, -54), Math.toRadians(45))
                        // TODO: Implement action for putting the sample

                        .strafeToLinearHeading(new Vector2d(-59, -42), Math.toRadians(90))
                        // TODO: Implement action for taking the sample

                        .strafeToLinearHeading(new Vector2d(-54, -54), Math.toRadians(45))
                        // TODO: Implement action for putting the sample

                        // Parking sequence:
                        .strafeToLinearHeading(new Vector2d(-48, -62), Math.toRadians(90))

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







