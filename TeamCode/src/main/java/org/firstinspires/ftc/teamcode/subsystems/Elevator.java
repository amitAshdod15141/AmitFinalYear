package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.RobotHardware;
import org.firstinspires.ftc.teamcode.util.BetterGamepad;
import org.firstinspires.ftc.teamcode.util.PIDFController;

@Config
public class Elevator implements Subsystem
{

    private final RobotHardware robot = RobotHardware.getInstance();
    public static double ELEVATOR_INCREMENT = 70;
    public static double BASE_LEVEL = 200;
    public static double MAX_LEVEL = 500;
    public static double HANG_OPEN = 400;
    public static double HANG = 400;
    double currentTarget = 0;
    boolean usePID = true;
    public static double maxPower = 0.75;
    public static double kPR = 0.0075, kIR = 0, kDR = 0.01;
    public DcMotorEx elevatorMotor;
    Gamepad gamepad;
    BetterGamepad cGamepad;
    PIDFController controller;
    PIDFController.PIDCoefficients pidCoefficients = new PIDFController.PIDCoefficients();


    boolean isAuto, firstPID = false;
    public Elevator(Gamepad gamepad, boolean isAuto, boolean firstPID)
    {
        this.isAuto = isAuto;
        this.elevatorMotor = robot.hardwareMap.get(DcMotorEx.class, "mE");


        if(firstPID && !isAuto)
        {
            elevatorMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        }
        else if(firstPID && isAuto)
        {
            elevatorMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            elevatorMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        }
        else if (isAuto)
        {
            elevatorMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            elevatorMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        else
        {
            elevatorMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        this.elevatorMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        this.gamepad = gamepad;
        this.cGamepad = new BetterGamepad(gamepad);

        pidCoefficients.kP = kPR;
        pidCoefficients.kI = kIR;
        pidCoefficients.kD = kDR;

        controller = new PIDFController(pidCoefficients);


    }

    public Elevator(boolean isAuto)
    {
        this.isAuto = isAuto;

        this.elevatorMotor = robot.hardwareMap.get(DcMotorEx.class, "mER");
        if (isAuto)
        {
            elevatorMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            elevatorMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        else
        {
            elevatorMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            elevatorMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        this.elevatorMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        pidCoefficients.kP = kPR;
        pidCoefficients.kI = kIR;
        pidCoefficients.kD = kDR;



        controller = new PIDFController(pidCoefficients);
    }

    public void setFirstPID(boolean firstPID) {
        this.firstPID = firstPID;
    }

    public void update() {

        if(!firstPID)
        {
            cGamepad.update();

            if (usePID)
            {
                setPidControl();
            }
            else
            {
                elevatorMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

                if(gamepad.right_stick_y != 0 && !gamepad.right_stick_button)
                {
                    if((-gamepad.right_stick_y) < 0)
                    {
                        elevatorMotor.setPower(Range.clip(-gamepad.right_stick_y, -maxPower/2, maxPower/2));
                    }
                    else
                    {
                        elevatorMotor.setPower(Range.clip(-gamepad.right_stick_y, -maxPower, maxPower));
                    }
                }
                else if(gamepad.right_stick_y != 0 && gamepad.right_stick_button)
                {
                    elevatorMotor.setPower(Range.clip(-gamepad.right_stick_y, -maxPower/2, maxPower/2));
                }
                else
                {
                    elevatorMotor.setPower(0);
                }

            }
        }
    }


    public void setPidControl()
    {
        controller.updateError(currentTarget - elevatorMotor.getCurrentPosition());

        elevatorMotor.setPower(controller.update());
    }

    public void setTarget(double target)
    {
        if(target > MAX_LEVEL)
        {
            this.currentTarget = MAX_LEVEL;
        }
        else
        {
            this.currentTarget = target;
        }
    }
    //Move elevator for auto
    public void move(double target)
    {
        this.setTarget(target);
        this.setPidControl();
    }

    public double getTargetRight()
    {
        return this.currentTarget;
    }

    public double getTargetLeft()
    {
        return this.currentTarget;
    }

    public double getPos()
    {
        return elevatorMotor.getCurrentPosition();
    }




    public void setUsePID(boolean usePID) {
        this.usePID = usePID;
    }

    public void setAuto(boolean isAuto)
    {
        this.isAuto = isAuto;
    }

    @Override
    public void play() {

    }

    @Override
    public void loop(boolean allowMotors) {
        isAuto = true;
        update();
    }

    @Override
    public void stop() {

    }

    public PIDFController getController() {
        return controller;
    }


}