package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Elevator;
import org.firstinspires.ftc.teamcode.subsystems.ReleaseSystem;
import org.firstinspires.ftc.teamcode.subsystems.TelescopicHand;
import org.firstinspires.ftc.teamcode.util.BetterGamepad;
import org.firstinspires.ftc.teamcode.util.ClawSide;

@Config
@TeleOp(name = "Full OpMode Teleop")
public class ReadyOpMode extends LinearOpMode {


    Drivetrain drivetrain;
    Elevator elevator;

    ReleaseSystem releaseSystem;
    Claw claw;

    TelescopicHand telescopicHand;

    ElapsedTime codeTime;
    Gamepad.RumbleEffect customRumbleEffect;    // Use to build a custom rumble sequence.

    // gamepads
    GamepadEx gamepadEx, gamepadEx2;
    BetterGamepad betterGamepad1, betterGamepad2;

    public static boolean firstIntake = true , canRetract = false , stayClosed = false , canIntake = true , extendMore = false , extendLess = false;
    public static double cooldown = 0, COOL_DOWN = 350 , transferDelay = 0 , retractTime = 0 ,target = 0 ,  delayBeforeRetract = 0 , outtakeAlignDelay = 0 ,angleDelay = 0;

    public static double angle , cooldownBasket = 0, clawBarDelay = 0 , extendAngle = 0 , extendIntake = 0 ;
    public enum LiftState {
        RETRACT,
        EXTRACT_HIGH_BASKET,
        EXTRACT_LOW_BAKSET,
        HANG,

        EXTRACT_BAR,
        RETRACT_INTAKE,

        RETARCTED_FIX_BASKET,
        RETARCTED_FIX_BAR,
        INTAKE_SHORT,

        INTAKE_LONG,
        HOVER_SHORT,
        HOVER_LONG,


        EXTAND_MORE,

        EXTAND_LESS
    }


    LiftState liftState = LiftState.RETRACT;


    RobotHardware robot = RobotHardware.getInstance();

    @Override
    public void runOpMode() throws InterruptedException {

        CommandScheduler.getInstance().reset();

        telemetry = new MultipleTelemetry(FtcDashboard.getInstance().getTelemetry());

        gamepadEx = new GamepadEx(gamepad1);
        gamepadEx2 = new GamepadEx(gamepad2);

        betterGamepad1 = new BetterGamepad(gamepad1);
        betterGamepad2 = new BetterGamepad(gamepad2);

        robot.init(hardwareMap, telemetry);

        drivetrain = new Drivetrain(gamepad1, true, true);
        elevator = new Elevator(gamepad2, true, false);
        releaseSystem = new ReleaseSystem();
        telescopicHand = new TelescopicHand(gamepad2, true, false);


        claw = new Claw();

        codeTime = new ElapsedTime();


        releaseSystem.setAngle(ReleaseSystem.Angle.INTAKE);
        releaseSystem.setAngle(ReleaseSystem.Angle.SPIN_MIDDLE);
        claw.updateState(Claw.ClawState.CLOSED , ClawSide.BOTH);

        elevator.setAuto(false);
        telescopicHand.setAuto(false);

        drivetrain.update();
        releaseSystem.update();
        elevator.update();
        telescopicHand.update();
        claw.update();

        customRumbleEffect = new Gamepad.RumbleEffect.Builder()
                .addStep(0.0, 1.0, 500)  //  Rumble right motor 100% for 500 mSec
                .addStep(0.0, 0.0, 300)  //  Pause for 300 mSec
                .addStep(1.0, 0.0, 250)  //  Rumble left motor 100% for 250 mSec
                .addStep(0.0, 0.0, 250)  //  Pause for 250 mSec
                .addStep(1.0, 0.0, 250)  //  Rumble left motor 100% for 250 mSec
                .build();

        while (opModeInInit() && !isStopRequested()) {
            telemetry.addLine("Initialized");
            telemetry.update();
            drivetrain.update();
            releaseSystem.update();

        }

        waitForStart();

        codeTime.reset();


        while (opModeIsActive()) {

            betterGamepad1.update();
            betterGamepad2.update();
            drivetrain.update();
            releaseSystem.update();
            elevator.update();
            telescopicHand.update();
            claw.update();



            if (gamepad2.right_stick_y != 0) {
                telescopicHand.setUsePID( false);
            } else {
                telescopicHand.setUsePID(true);
            }

            if (gamepad2.left_stick_y != 0) {
                telescopicHand.setUsePID( false);
            } else {
                telescopicHand.setUsePID(true);
            }


            if(betterGamepad1.shareOnce())
            {
                drivetrain.maxPower = 0.9;
            }

            systemStateMachine();
        }

    }



    void systemStateMachine () {

        switch (liftState) {

            case RETRACT:


                    telescopicHand.setTarget(TelescopicHand.RETARCT_TELESCOPE);
                    releaseSystem.setAngle(ReleaseSystem.Angle.INTAKE);
                    releaseSystem.setAngle(ReleaseSystem.Angle.SPIN_MIDDLE);
                    elevator.setTarget(0);


                if(stayClosed) { claw.updateState(Claw.ClawState.CLOSED, ClawSide.BOTH); }

               telescopicHand.setTarget(0);



                if (betterGamepad1.rightBumperOnce() && canIntake) {
                    liftState = liftState.HOVER_SHORT;
                    angleDelay = getTime();
                }

                if (betterGamepad1.YOnce()) {
                    telescopicHand.setTarget(TelescopicHand.OUTTAKE_TELESCOPE);

                    transferDelay = getTime();
                    liftState = LiftState.EXTRACT_HIGH_BASKET;
                    canIntake = false;
                }


                if(betterGamepad1.AOnce())
                {
                    telescopicHand.setTarget(TelescopicHand.OUTTAKE_TELESCOPE);
                    transferDelay = getTime();
                    liftState = liftState.EXTRACT_BAR;
                    canIntake = false;
                }

                break;

            case EXTRACT_HIGH_BASKET:


                claw.updateState(Claw.ClawState.CLOSED, ClawSide.BOTH);

                if (betterGamepad1.YOnce()) {
                    liftState = LiftState.EXTRACT_LOW_BAKSET;
                }

                target = Elevator.HIGH_BASKET_LEVEL;

                elevator.setTarget(target);

                if (getTime() - transferDelay >= 300) {
                    releaseSystem.setAngle(ReleaseSystem.Angle.OUTTAKE);
                }

                if (betterGamepad2.dpadUpOnce()) {
                    target += 200;
                }

                if (betterGamepad2.dpadDownOnce()) {
                    target -= 200;
                }

                if (betterGamepad1.AOnce()) {
                    retractTime = getTime();
                    canRetract = true;
                    delayBeforeRetract = getTime();
                    outtakeAlignDelay = getTime();
                }


                if (getTime() - retractTime >= 50 && canRetract) {

                    liftState = liftState.RETARCTED_FIX_BASKET;
                    cooldownBasket = getTime();


                }




                break;

            case EXTRACT_LOW_BAKSET:

                claw.updateState(Claw.ClawState.INTAKE,ClawSide.BOTH);


                if (betterGamepad1.YOnce()) {
                    liftState = LiftState.EXTRACT_HIGH_BASKET;
                }

                elevator.setTarget(Elevator.LOW_BASKET_LEVEL);

                if (getTime() - transferDelay >= 300) {
                    releaseSystem.setAngle(ReleaseSystem.Angle.OUTTAKE);
                }

                if (betterGamepad1.AOnce()) {
                    retractTime = getTime();
                    canRetract = true;
                    delayBeforeRetract = getTime();
                    outtakeAlignDelay = getTime();
                }


                if (getTime() - retractTime >= 50 && canRetract) {

                    liftState = liftState.RETARCTED_FIX_BASKET;
                    cooldownBasket = getTime();


                }



                break;

            case EXTRACT_BAR:


                target = 1200;

                elevator.setTarget(target);

                claw.setBothClaw(Claw.ClawState.CLOSED);

                releaseSystem.setAngle(ReleaseSystem.Angle.OUTTAKE);

                if (betterGamepad1.AOnce()) {
                    retractTime = getTime();
                    canRetract = true;
                    delayBeforeRetract = getTime();
                    outtakeAlignDelay = getTime();
                }

                if (getTime() - retractTime >= 50 && canRetract) {

                    liftState = liftState.RETARCTED_FIX_BAR;
                    cooldown = getTime();
                    clawBarDelay = getTime();

                }
                break;
            case HOVER_SHORT:

                target = elevator.INTAKE_SHORT;


                claw.updateState(Claw.ClawState.OPEN , ClawSide.BOTH);

                elevator.setTarget(target);

                if(betterGamepad1.rightBumperOnce())
                {
                    liftState = liftState.INTAKE_SHORT;
                }

                if(betterGamepad1.leftBumperOnce())
                {
                    liftState = liftState.HOVER_LONG;
                }


                if(betterGamepad1.dpadUpOnce()) {
                    releaseSystem.setAngle(ReleaseSystem.Angle.SPIN_90);
                }

                if(betterGamepad1.dpadRightOnce()) {
                    releaseSystem.setAngle(ReleaseSystem.Angle.SPIN_45);
                }

                if(betterGamepad1.dpadLeftOnce()) {
                    releaseSystem.setAngle(ReleaseSystem.Angle.SPIN_135);
                }

                if(betterGamepad1.dpadDownOnce()) {
                    releaseSystem.setAngle(ReleaseSystem.Angle.SPIN_MIDDLE);
                }

                    break;

            case HOVER_LONG:

                target = elevator.INTAKE_LONG;

                claw.updateState(Claw.ClawState.OPEN , ClawSide.BOTH);

                elevator.setTarget(target);

                if(betterGamepad1.leftBumperOnce())
                {
                    liftState = liftState.INTAKE_LONG;
                }


                if(betterGamepad1.dpadUpOnce()) {
                    releaseSystem.setAngle(ReleaseSystem.Angle.SPIN_90);
                }

                if(betterGamepad1.dpadRightOnce()) {
                    releaseSystem.setAngle(ReleaseSystem.Angle.SPIN_45);
                }

                if(betterGamepad1.dpadLeftOnce()) {
                    releaseSystem.setAngle(ReleaseSystem.Angle.SPIN_135);
                }

                if(betterGamepad1.dpadDownOnce()) {
                    releaseSystem.setAngle(ReleaseSystem.Angle.SPIN_MIDDLE);
                }
                break;

            case INTAKE_SHORT:




                if (getTime() - angleDelay >= 600)
                {
                    angle = telescopicHand.INTAKE_SHORT;
                    telescopicHand.setTarget(angle);

                }

                if (betterGamepad2.dpadRightOnce()) {
                    extendIntake = Elevator.INTAKE_LONG + 166.66;
                    extendAngle = telescopicHand.INTAKE_LONG + 2;

                    elevator.setTarget(extendIntake);
                    telescopicHand.setTarget(extendAngle);
                }

                if (betterGamepad2.dpadLeftOnce()) {

                    extendIntake = Elevator.INTAKE_LONG - 166.66;
                    extendAngle = telescopicHand.INTAKE_LONG - 2;

                    elevator.setTarget(extendIntake);
                    telescopicHand.setTarget(extendAngle);
                }

                if(betterGamepad1.dpadUpOnce()) {
                    releaseSystem.setAngle(ReleaseSystem.Angle.SPIN_90);
                }

                if(betterGamepad1.dpadRightOnce()) {
                    releaseSystem.setAngle(ReleaseSystem.Angle.SPIN_45);
                }

                if(betterGamepad1.dpadLeftOnce()) {
                    releaseSystem.setAngle(ReleaseSystem.Angle.SPIN_135);
                }

                if(betterGamepad1.dpadDownOnce()) {
                    releaseSystem.setAngle(ReleaseSystem.Angle.SPIN_MIDDLE);
                }


                if (betterGamepad1.rightBumperOnce()) {
                    firstIntake = false;
                    liftState = LiftState.RETRACT_INTAKE;
                    stayClosed = true;
                    angleDelay = getTime();
                    delayBeforeRetract = getTime();


                }

                break;

            case INTAKE_LONG:

                if (getTime() - angleDelay >= 600) { telescopicHand.setTarget(TelescopicHand.INTAKE_LONG); }

                elevator.setTarget(target);

                target = Elevator.INTAKE_LONG;


                if (betterGamepad2.dpadRightOnce()) {
                    extendIntake = Elevator.INTAKE_LONG + 166.66;
                    extendAngle = telescopicHand.INTAKE_LONG + 2;

                    elevator.setTarget(extendIntake);
                    telescopicHand.setTarget(extendAngle);
                }

                if (betterGamepad2.dpadLeftOnce()) {

                    extendIntake = Elevator.INTAKE_LONG - 166.66;
                    extendAngle = telescopicHand.INTAKE_LONG - 2;

                    elevator.setTarget(extendIntake);
                    telescopicHand.setTarget(extendAngle);
                }


                if(betterGamepad1.dpadUpOnce()) {
                    releaseSystem.setAngle(ReleaseSystem.Angle.SPIN_90);
                }

                if(betterGamepad1.dpadRightOnce()) {
                    releaseSystem.setAngle(ReleaseSystem.Angle.SPIN_45);
                }

                if(betterGamepad1.dpadLeftOnce()) {
                    releaseSystem.setAngle(ReleaseSystem.Angle.SPIN_135);
                }

                if(betterGamepad1.dpadDownOnce()) {
                    releaseSystem.setAngle(ReleaseSystem.Angle.SPIN_MIDDLE);
                }

                if (betterGamepad1.leftBumperOnce()) {
                    firstIntake = false;
                    liftState = LiftState.RETRACT_INTAKE;
                    stayClosed = true;
                    angleDelay = getTime();
                    delayBeforeRetract = getTime();
                }

                break;

            case RETARCTED_FIX_BASKET:


                claw.updateState(Claw.ClawState.OPEN,ClawSide.BOTH);

                if(getTime() - cooldownBasket >= 400)
                {
                    releaseSystem.setAngle(ReleaseSystem.Angle.INTAKE);
                }

                if(getTime() - outtakeAlignDelay >= 700) { elevator.setTarget(0); }

                if(getTime() - delayBeforeRetract >= 1400) {

                    canIntake = true;
                    canRetract = false;
                    stayClosed = false;
                    liftState = liftState.RETRACT;
                }

                break;

            case RETARCTED_FIX_BAR:

                claw.updateState(Claw.ClawState.INTAKE,ClawSide.BOTH);

                if(getTime() - outtakeAlignDelay >= 300) { elevator.setTarget(0); }

                if(getTime() - clawBarDelay >= 600) { claw.updateState(Claw.ClawState.OPEN,ClawSide.BOTH); }

                if(getTime() - delayBeforeRetract >= 1200) {

                    canIntake = true;
                    canRetract = false;
                    stayClosed = false;
                    liftState = liftState.RETRACT;
                }

                break;

            case RETRACT_INTAKE:

                claw.updateState(Claw.ClawState.CLOSED , ClawSide.BOTH);

                if(getTime() - angleDelay >= 500)
                {
                    telescopicHand.setTarget(0);
                }

                if(getTime() - delayBeforeRetract >= 800) {

                    liftState = liftState.RETRACT;

                }

                break;

            case EXTAND_MORE:

                target += 166.66;
                angle +=  2;

                elevator.setTarget(target);
                telescopicHand.setTarget(angle);

                if (betterGamepad1.leftBumperOnce()) { liftState = liftState.RETRACT_INTAKE; }


            case EXTAND_LESS:

                target -= 166.66;
                angle -=  2;

                elevator.setTarget(target);
                telescopicHand.setTarget(angle);

                if (betterGamepad1.leftBumperOnce()) { liftState = liftState.RETRACT_INTAKE; }

        }

    }




    double getTime()
    {
        return codeTime.nanoseconds() / 1000000;
    }

    boolean cooldowned()
    {
        return (getTime() - cooldown) >= COOL_DOWN;
    }

    void coolDownReset()
    {
        cooldown = getTime();
    }

}
