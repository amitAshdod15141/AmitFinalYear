package org.firstinspires.ftc.teamcode;

import static com.acmerobotics.roadrunner.ftc.Actions.runBlocking;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.auto.AutoConstants;
import org.firstinspires.ftc.teamcode.roadrunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.Elevator;
import org.firstinspires.ftc.teamcode.subsystems.ReleaseSystem;
import org.firstinspires.ftc.teamcode.subsystems.TelescopicHand;

@Autonomous(name = "RoadRunnerAutoMeepMeep", group = "Autonomous")
public class AutoRedBasket extends LinearOpMode {


    Claw claw;

    Elevator elevator;

    TelescopicHand telescopicHand;

    ReleaseSystem releaseSystem;



    public AutoConstants autoConstants;
    RobotHardware robot = RobotHardware.getInstance();
    @Override
    public void runOpMode() {
        Pose2d startPoseRedLeft = new Pose2d(-35, -62, Math.toRadians(90));

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        robot.init(hardwareMap , telemetry, startPoseRedLeft);

        elevator = new Elevator(true);

        claw = new Claw();

        releaseSystem = new ReleaseSystem();

        telescopicHand = new TelescopicHand(true);

        

        waitForStart();

        if (isStopRequested()) return;

        // Define the trajectory sequence
        Action redTrajIntake = robot.drive.actionBuilder(startPoseRedLeft)
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

        // Follow the trajectory sequence
        runBlocking(redTrajIntake);


        telemetry.addData("Status", "Autonomous Complete");
        telemetry.update();
    }
}
