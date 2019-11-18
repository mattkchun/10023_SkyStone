package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

/*
 * Created by Chun on 11/4/19 for 10023.
 */

@Autonomous
//@Disabled

public class ForwardRightAutonomous extends BaseRobot {
    private int stage = 0;

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void loop() {
        super.loop();
        switch (stage) {
            case -1:
                //delay start
                if (timer.time()>5) {
                    stage++;
                }
                break;
            case 0:
                //drive forward to under bridge
                if (auto_drive(0.7, 10)) {
                    reset_drive_encoders();
                    stage++;
                }
                break;
            case 1:
                //horizontal right to center
                if (auto_horizontal(0.8, 14)) {
                    reset_drive_encoders();
                    stage++;
                }
                break;
            default:
                break;
        }
    }
}