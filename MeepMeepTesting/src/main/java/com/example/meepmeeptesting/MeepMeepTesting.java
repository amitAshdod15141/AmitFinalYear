package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import org.rowlandhall.meepmeep.MeepMeep;
import org.rowlandhall.meepmeep.roadrunner.DefaultBotBuilder;
import org.rowlandhall.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(600);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                // Set custom robot dimensions (width, length)
                .setDimensions(14.75, 15)
                .followTrajectorySequence(drive -> drive.trajectorySequenceBuilder(new Pose2d(-35, -62, Math.toRadians(90)))
                        .lineToLinearHeading(new Pose2d(-48, -42, Math.toRadians(90))) // Strafe left 13 inches (sideways)
                        .forward(1)
                        // TODO: Here, implement the action for taking the sample

                        .lineToLinearHeading(new Pose2d(-54, -54, Math.toRadians(45)))
                        // TODO: Here, implement the action for Putting the sample

                        .lineToLinearHeading(new Pose2d(-59, -42, Math.toRadians(90)))
                        .forward(1)
                        // TODO: Here, implement the action for taking the sample

                        .lineToLinearHeading(new Pose2d(-54, -54, Math.toRadians(45)))
                        // TODO: Here, implement the action for Putting the sample

                        //here is the parking sequence:
                        .forward(5)
                        .lineToLinearHeading(new Pose2d(-48, -62, Math.toRadians(90)))
                        .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_INTOTHEDEEP_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}
