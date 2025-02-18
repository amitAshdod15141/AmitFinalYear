package org.firstinspires.ftc.teamcode.auto;

import static org.firstinspires.ftc.teamcode.auto.ActionHelper.activateSystem;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.Elevator;
import org.firstinspires.ftc.teamcode.util.ClawSide;
import org.firstinspires.ftc.teamcode.util.Stopwatch;

public class DepositActions {


    public enum Cycles {
        PRELOAD,
        FIRST_CYCLE,
        SECOND_CYCLE
    }

    ;

    public enum TypeClaws {
        RELEASED,
        LOCKED
    }

    public enum LockSide {
        RIGHT_LOCK,
        LEFT_LOCK,
        BOTH_LOCKS,
    }

    public enum ReleaseSide {
        RIGHT_OPEN,
        LEFT_OPEN,
        BOTH_OPEN,
    }


    private Elevator elevator;

    private Claw claw;

    private boolean activated;

    ReleaseSide releaseSide;
    LockSide lockSide;
    TypeClaws typeClaws;

    public DepositActions(Elevator elevator, Claw claw){
        this.elevator = elevator;
        this.claw = claw;
        typeClaws = TypeClaws.LOCKED;
        releaseSide = ReleaseSide.BOTH_OPEN;
        lockSide = LockSide.BOTH_LOCKS;

    }




    private void moveElevatorByTraj(int elevatorTarget) {
        elevator.setTarget(elevatorTarget);
        elevator.setPidControl();
    }

    //This function will prepare the intake and outtake  for deposit


    public void retractElevator() {
        elevator.setTarget(0);
        elevator.setPidControl();
    }


    public class ReadyForDeposit implements Action {
        Stopwatch readyForDepositTimer;
        int elevator;

        public ReadyForDeposit(int elevator) {
            this.elevator = elevator;
            readyForDepositTimer = new Stopwatch();
            readyForDepositTimer.reset();
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {

            claw.updateState(Claw.ClawState.OPEN ,ClawSide.BOTH);
            moveElevatorByTraj(elevator);

            return false;
        }
    }


    public class MoveOuttake implements Action {
        Stopwatch readyForDepositTimer;


        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {

            return false;
        }
    }

    public class PlacePixel implements Action {

        Stopwatch placePixelTimer;

        long delay = 0;

        public PlacePixel() {

            placePixelTimer = new Stopwatch();

            placePixelTimer.reset();

            delay = 500;

        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {

            return !activateSystem(placePixelTimer, () -> claw.updateState(Claw.ClawState.OPEN ,ClawSide.BOTH), delay);


        }
    }

        public class PlaceIntermediatePixel implements Action {
            Stopwatch placePixelTimer;
            long delay = 0;

            public PlaceIntermediatePixel(Cycles current, long d) {
                placePixelTimer = new Stopwatch();
                placePixelTimer.reset();
                delay = d;
            }

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                return !activateSystem(placePixelTimer, () -> claw.updateState(Claw.ClawState.OPEN ,ClawSide.BOTH), delay);
            }
        }


        public class RetractDeposit implements Action {
            Stopwatch retractDepositTimer;

            public RetractDeposit() {
                retractDepositTimer = new Stopwatch();
                retractDepositTimer.reset();
            }

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                return !activateSystem(retractDepositTimer, () -> retractElevator(), 800);


            }
        }


        public class MoveElevator implements Action {
            Stopwatch retractDepositTimer;
            int target;

            public MoveElevator(int target) {
                this.target = target;
            }

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {

                moveElevatorByTraj(target);
                return false;

            }
        }

        public class MoveClaw implements Action {
            private Claw.ClawState _clawState;
            private ClawSide _clawSide;

            public MoveClaw(Claw.ClawState clawState, ClawSide clawSide) {
                this._clawState = clawState;
                this._clawSide = clawSide;
            }

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                claw.updateState(this._clawState ,ClawSide.BOTH);
                return false;
            }
        }

        public Action retractDeposit() {
            return new RetractDeposit();
        }

        public Action readyForDeposit(int elevator) {
            return new ReadyForDeposit(elevator);
        }


        public Action placePixel() {
            return new PlacePixel();
        }

        public Action moveClaw(Claw.ClawState clawState, ClawSide clawSide) {
            return new MoveClaw(clawState, clawSide);
        }

        public Action placeIntermediatePixel(Cycles currentCycle, long d) {
            return new PlaceIntermediatePixel(currentCycle, d);
        }


        public Action moveElevator(int thisTarget) {
            return new MoveElevator(thisTarget);
        }

}

