package org.firstinspires.ftc.teamcode;

import static com.acmerobotics.roadrunner.ftc.Actions.runBlocking;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.auto.AutoConstants;
import org.firstinspires.ftc.teamcode.auto.DepositActions;
import org.firstinspires.ftc.teamcode.auto.UpdateActions;
import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.Elevator;
import org.firstinspires.ftc.teamcode.subsystems.ReleaseSystem;
import org.firstinspires.ftc.teamcode.subsystems.TelescopicHand;
import org.firstinspires.ftc.teamcode.util.ClawSide;

@Autonomous(name = "RoadRunnerAutoMeepMeep", group = "Autonomous")
public class AutoRedBasket extends LinearOpMode {


    Claw claw;

    Elevator elevator;

    TelescopicHand telescopicHand;

    ReleaseSystem releaseSystem;

  DepositActions depositActions;
    UpdateActions updateActions;


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


        elevator.setAuto(true);

        depositActions = new DepositActions(elevator , claw, telescopicHand , releaseSystem);
        updateActions = new UpdateActions(elevator, claw ,telescopicHand , releaseSystem);




        SequentialAction intakeSample = new SequentialAction(

                depositActions.moveElevator(1000),
                new SleepAction(0.2),
                depositActions.moveTelescopic(-6),
                new SleepAction(0.2),
                depositActions.moveClaw(Claw.ClawState.CLOSED, ClawSide.BOTH)

        );


        SequentialAction retractIntake = new SequentialAction(

                depositActions.moveTelescopic(0),
                new SleepAction(0.2),
                depositActions.moveElevator(0)

        );

        SequentialAction releaseSample = new SequentialAction(

                depositActions.moveTelescopic(97),
                new SleepAction(0.5),
                new InstantAction(() -> depositActions.moveElevator(3700)),
                new SleepAction(0.2),
                depositActions.moveOuttake(ReleaseSystem.Angle.OUTTAKE),
                new SleepAction(0.2),
                depositActions.moveClaw(Claw.ClawState.OPEN, ClawSide.BOTH)
        );

        SequentialAction retractElevator = new SequentialAction(

                depositActions.moveOuttake(ReleaseSystem.Angle.INTAKE),
                new SleepAction(0.4),
                depositActions.moveElevator(0),
                new SleepAction(0.4),
                depositActions.moveTelescopic(0)

        );


        while (opModeInInit() && !isStopRequested()) {

            claw.updateState(Claw.ClawState.CLOSED,ClawSide.BOTH);
            releaseSystem.setAngle(ReleaseSystem.Angle.INTAKE);

        }

        waitForStart();



        if (isStopRequested()) return;

        // Define the trajectory sequence
        Action redTrajIntake = robot.drive.actionBuilder(startPoseRedLeft)
                .strafeToLinearHeading(new Vector2d(-54, -54), Math.toRadians(45))
                .afterTime(0.2 , releaseSample)
               // .afterTime(0.7 , retractElevator)
                // TODO: Implement action for putting the sample

                /*
                .strafeToLinearHeading(new Vector2d(-48, -42), Math.toRadians(90)) // Strafe left 13 inches
                // TODO: Implement action for taking the sample

                .afterTime(0.7 , intakeSample)
                .afterTime(0.2, retractIntake)

                .strafeToLinearHeading(new Vector2d(-54, -54), Math.toRadians(45))
                // TODO: Implement action for putting the sample
                .afterTime(0.2 , releaseSample)
                .afterTime(0.7 , retractElevator)

                .strafeToLinearHeading(new Vector2d(-59, -42), Math.toRadians(90))
                .afterTime(1 , intakeSample)
                .afterTime(0.2, retractIntake)
                // TODO: Implement action for taking the sample

                .strafeToLinearHeading(new Vector2d(-54, -54), Math.toRadians(45))
                .afterTime(0.2 , releaseSample)
                .afterTime(0.7 , retractElevator)
                // TODO: Implement action for putting the sample

                // Parking sequence:
                .strafeToLinearHeading(new Vector2d(-48, -62), Math.toRadians(90))

                 */
                .build();

        // Follow the trajectory sequence
        runBlocking(new ParallelAction(
                updateActions.updateSystems(),
                redTrajIntake
        ));


        telemetry.addData("Status", "Autonomous Complete");
        telemetry.update();
    }
}
