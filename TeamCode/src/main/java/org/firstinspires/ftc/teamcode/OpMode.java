package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.RobotHardware;
import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Elevator;
import org.firstinspires.ftc.teamcode.subsystems.Outtake;
import org.firstinspires.ftc.teamcode.util.BetterGamepad;

@Config
@TeleOp(name = "OpMode Teleop")
public class OpMode extends LinearOpMode {

    // robot
    private final RobotHardware robot = RobotHardware.getInstance();

    // subsystems
    Drivetrain drivetrain;
    Elevator elevator;

    Outtake outtake;
    Claw claw;

    ElapsedTime codeTime;
    Gamepad.RumbleEffect customRumbleEffect;    // Use to build a custom rumble sequence.


    // gamepads
    GamepadEx gamepadEx, gamepadEx2;
    BetterGamepad betterGamepad1, betterGamepad2;

    // delays
    public static double WAIT_DELAY_TILL_OUTTAKE = 150, WAIT_DELAY_TILL_CLOSE = 250, COOL_DOWN = 350;
    public static double DEFAULT_INTAKE_EXTEND_PRECENTAGE = 42.5, SHORT_INTAKE_EXTEND_PRECENTAGE = 25;
    // variables
    double elevatorReset = 0, previousElevator = 0;
    double elevatorTarget = 0 , startXDelay = 0, cooldown = 0;
    int openedXTimes = 0, ACount = 0;
    boolean firstOuttakeAngle = true, retract = false, canIntake = true;
    boolean hang = false, XPressed = false;
    boolean  firstOuttake = true;
    boolean rightClaw = true, leftClaw = true;


    public enum OuttakeState {
        OUTTAKE,
        OUTTAKE_LONG
    }

    public enum LiftState {
        RETRACT,
        EXTRACT,
        STUCK_2,
        HANG
    }

    LiftState liftState = LiftState.RETRACT;
    OuttakeState outtakeState = OuttakeState.OUTTAKE;

    @Override
    public void runOpMode() {
        CommandScheduler.getInstance().reset();

        telemetry = new MultipleTelemetry(FtcDashboard.getInstance().getTelemetry());

        gamepadEx = new GamepadEx(gamepad1);
        gamepadEx2 = new GamepadEx(gamepad2);

        betterGamepad1 = new BetterGamepad(gamepad1);
        betterGamepad2 = new BetterGamepad(gamepad2);

        robot.init(hardwareMap, telemetry);

        drivetrain = new Drivetrain(gamepad1, true, false);
        elevator = new Elevator(gamepad2, false, false);
        outtake = new Outtake();
        claw = new Claw();
        claw.setBothClaw(Claw.ClawState.INTAKE);
        outtake.setAngle(Outtake.Angle.INTAKE);
        elevator.setAuto(false);

        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD));



        robot.imu.initialize(parameters);



        codeTime = new ElapsedTime();

        claw.update();
        outtake.update();

        customRumbleEffect = new Gamepad.RumbleEffect.Builder()
                .addStep(0.0, 1.0, 500)  //  Rumble right motor 100% for 500 mSec
                .addStep(0.0, 0.0, 300)  //  Pause for 300 mSec
                .addStep(1.0, 0.0, 250)  //  Rumble left motor 100% for 250 mSec
                .addStep(0.0, 0.0, 250)  //  Pause for 250 mSec
                .addStep(1.0, 0.0, 250)  //  Rumble left motor 100% for 250 mSec
                .build();


        while (opModeInInit() && !isStopRequested())
        {
            telemetry.addLine("Initialized");
            telemetry.update();
            claw.update();
            outtake.update();

        }

        waitForStart();

        codeTime.reset();


        while (opModeIsActive())
        {

            betterGamepad1.update();
            betterGamepad2.update();
            drivetrain.update();

            outtake.update();
            elevator.update();
            claw.update();



            if(gamepad2.left_trigger != 0 || gamepad1.left_trigger != 0)
            {
                drivetrain.superSlow();
            }




            if (gamepad2.right_stick_y != 0) {
                elevator.setUsePID(false);
            } else {
                elevator.setUsePID(true);
            }



            if(betterGamepad1.shareOnce())
            {
                drivetrain.maxPower = 0.9;
            }

            elevatorStateMachine();


            telemetry.addData("left" , leftClaw);
            telemetry.addData("right" , rightClaw);
            telemetry.update();
        }
    }





    void elevatorStateMachine()
    {
        switch (liftState) {
            case RETRACT:
                firstOuttake = true;
                elevator.setTarget(0);

                canIntake = true;

                if (betterGamepad1.YOnce())
                {
                    previousElevator = getTime();
                    claw.setBothClaw(Claw.ClawState.CLOSED);
                    liftState = LiftState.EXTRACT;
                }
                else if(betterGamepad2.dpadUpOnce())
                {
                    liftState = LiftState.HANG;
                }
                else if(betterGamepad2.XOnce())
                {
                    liftState = LiftState.STUCK_2;
                }
                break;
            case EXTRACT:

                elevatorTarget = elevator.MAX_LEVEL;

                if(gamepad2.right_stick_y != 0)
                {
                    elevatorTarget = elevator.getPos() - (openedXTimes * (Elevator.ELEVATOR_INCREMENT));
                }

                if ((getTime() - previousElevator) >= WAIT_DELAY_TILL_OUTTAKE) {
                    outtake.setAngle(Outtake.Angle.OUTTAKE);
                }

                if(betterGamepad1.dpadRightOnce() && cooldowned())
                {
                    claw.setRightClaw(Claw.ClawState.OPEN);
                }


                if(betterGamepad2.rightBumperOnce())
                {
                    elevatorTarget += 150;
                    coolDownReset();
                }
                else if(betterGamepad2.leftBumperOnce())
                {
                    elevatorTarget -= 150;
                    coolDownReset();
                }
                else if(betterGamepad2.dpadLeftOnce())
                {
                    elevatorTarget -= 415;
                    coolDownReset();
                }
                else if(betterGamepad2.dpadRightOnce())
                {
                    elevatorTarget += 415;
                    coolDownReset();
                }

                if(betterGamepad2.touchpadOnce())
                {
                    switchOuttake();
                }

                if(betterGamepad1.YOnce() || betterGamepad1.AOnce())
                {
                    claw.setBothClaw(Claw.ClawState.OPEN);

                    ACount++;

                }

                if ((ACount > 1 && cooldowned()) || (betterGamepad2.shareOnce() && cooldowned()))  {
                    claw.setBothClaw(Claw.ClawState.OPEN);

                    openedXTimes++;


                    elevatorReset = getTime();
                    retract = true;
                    ACount = 0;
                } else if ((getTime() - elevatorReset) >= WAIT_DELAY_TILL_CLOSE && retract)
                {
                    retract = false;
                    firstOuttakeAngle = true;
                    outtake.resetOuttake();
                    outtake.setAngle(Outtake.Angle.INTAKE);
                    liftState = LiftState.RETRACT;
                }
                break;
            case HANG:
                elevatorTarget = elevator.HANG;

                outtake.setAngle(Outtake.Angle.HANG);

                if(betterGamepad2.rightBumperOnce())
                {
                    elevatorTarget += 100;
                }
                else if(betterGamepad2.leftBumperOnce())
                {
                    elevatorTarget -= 100;
                }

                if(betterGamepad2.dpadUpOnce())
                {
                    hang = !hang;
                }

                if(!hang)
                {
                    elevator.setTarget(Elevator.HANG_OPEN);
                }
                else
                {
                    elevator.setTarget(Elevator.HANG);
                }


                if (betterGamepad1.AOnce() || betterGamepad1.leftBumperOnce() || betterGamepad2.shareOnce())
                {
                    liftState = LiftState.RETRACT;
                    outtake.setAngle(Outtake.Angle.INTAKE);
                }

                break;
            case STUCK_2:
                outtake.setAngle(Outtake.Angle.OUTTAKE);

                elevatorTarget = elevator.MAX_LEVEL;

                if(betterGamepad2.XOnce())
                {
                    claw.setBothClaw(Claw.ClawState.CLOSED);
                    startXDelay = getTime();
                    XPressed = true;
                    liftState = LiftState.RETRACT;
                }
                break;
            default:
                liftState = LiftState.RETRACT;
                break;
        }
    }


    double getTime()
    {
        return codeTime.nanoseconds() / 1000000;
    }


    void switchOuttake()
    {
        switch (outtakeState)
        {
            case OUTTAKE:
                outtakeState = OuttakeState.OUTTAKE_LONG;
                break;
            case OUTTAKE_LONG:
                outtakeState = OuttakeState.OUTTAKE;
                break;
        }
    }

    boolean cooldowned()
    {
        return (getTime() - cooldown) >= COOL_DOWN;
    }

    void coolDownReset()
    {
        cooldown = getTime();
    }

    void openCloseClaws()
    {
        if(leftClaw)
        {
            claw.setRightClaw(Claw.ClawState.INTAKE);
        }
        else
        {
            claw.setRightClaw(Claw.ClawState.CLOSED);
        }
        if(rightClaw)
        {
            claw.setLeftClaw(Claw.ClawState.INTAKE);
        }
        else
        {
            claw.setLeftClaw(Claw.ClawState.CLOSED);
        }
    }


}
