package org.firstinspires.ftc.teamcode.auto;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.teamcode.RobotHardware;
import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.Elevator;

public class UpdateActions {


    private Elevator elevator;


    private Claw claw;
    RobotHardware robot = RobotHardware.getInstance();

    int counter = 0;
    public UpdateActions(Elevator elevator, Claw claw) {
        this.elevator = elevator;
        this.claw = claw;
    }
    public class UpdateSystems implements Action {

        public UpdateSystems() {
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            elevator.setPidControl();
            claw.update();
            robot.drive.updatePoseEstimate();

            return true;
        }
    }

    public Action updateSystems()
    {
        return new UpdateSystems();
    }
}
