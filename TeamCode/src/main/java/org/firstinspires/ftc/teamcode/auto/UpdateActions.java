package org.firstinspires.ftc.teamcode.auto;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.teamcode.RobotHardware;
import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.Elevator;
import org.firstinspires.ftc.teamcode.subsystems.ReleaseSystem;
import org.firstinspires.ftc.teamcode.subsystems.TelescopicHand;

public class UpdateActions {


    private Elevator elevator;

    private TelescopicHand telescopicHand;

    private ReleaseSystem releaseSystem;
    private Claw claw;
    RobotHardware robot = RobotHardware.getInstance();

    int counter = 0;
    public UpdateActions(Elevator elevator, Claw claw , TelescopicHand telescopicHand , ReleaseSystem releaseSystem)
    {
        this.elevator = elevator;
        this.claw = claw;
        this.releaseSystem = releaseSystem;
        this.telescopicHand = telescopicHand;

    }
    public class UpdateSystems implements Action {

        public UpdateSystems() {
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {

            telescopicHand.setPidControl();
            elevator.setPidControl();
            claw.update();
            releaseSystem.update();

            robot.drive.updatePoseEstimate();

            return true;
        }
    }

    public Action updateSystems()
    {
        return new UpdateSystems();
    }
}
