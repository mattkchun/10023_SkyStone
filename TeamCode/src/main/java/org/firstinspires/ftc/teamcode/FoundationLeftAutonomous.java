package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

/*
 * Created by Chun on 11/4/19 for 10023.
 */

@Autonomous
//@Disabled

public class FoundationLeftAutonomous extends BaseRobot {
    private int stage = 0;
    //backward -5.8 in

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
                //horizontal right to foundation
                if (auto_horizontal(0.7, 15)) {
                    reset_drive_encoders();
                    stage++;
                }
                break;
            case 1:
                //drive to middle of foundation
//                if (auto_drive(-0.5, 5.8)) {
//                    reset_drive_encoders();
//                    stage++;
//                }
                if (auto_drive(0.5, 5)) {
                    reset_drive_encoders();
                    stage++;
                }
                break;
            case 2:
                //lower arm
                if (get_motor_enc(foundationMotor) < -100) {
                    foundation(-0.1);
                    timer.reset();
                    stage++;
                } else
                    foundation(-1);
                break;
            case 3:
                //wait for arm to lower then horizontal left to wall
                if (timer.time() > 0.3) {
                    if (auto_horizontal(-0.8, 40)) {
                        reset_drive_encoders();
                        stage++;
                    }
                }
                break;
            case 4:
                //raise arm
                if (get_motor_enc(foundationMotor) > -60) {
                    foundation(0);
                    stage++;
                } else
                    foundation(1);
                break;
            case 5:
                //horizontal left to wall
                if (auto_horizontal(-0.5, 5)) {
                    reset_drive_encoders();
                    stage++;
                }
                break;
            case 6:
                //drive to under bridge
                if (auto_drive(-0.8, 20)) {
                    reset_drive_encoders();
                    stage++;
                }
                break;
            default:
                break;
        }
    }
}