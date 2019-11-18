package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

/*
 * Created by Chun on 11/4/19 for 10023.
 */

@Autonomous
@Disabled

public class WorkingLeftFarSkystoneAutonomous extends BaseRobot {
    private int stage = 0;
    private double inchesMoved = 0;
    private boolean second = false;
    private int dropOff = 50;

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
                //horizontal right to blocks
                if (auto_horizontal(0.8, 10)) {
                    reset_drive_encoders();
                    stage++;
                }
                break;
            case 1:
                //drive to touch wall
                if (auto_drive(-0.8, 18)) {
                    reset_drive_encoders();
                    stage++;
                }
                break;
            case 2:
                //horizontal right to blocks (until close enough to scan color)
                if (auto_horizontal(0.2, 20) || skystoneSensor.red()>200) {
                    reset_drive_encoders();
                    stage++;
                }
                break;
            case 3:
                //drive forward (scanning for the black skystone)
                //power 0.22
                if (auto_drive(0.235, 40) || (checkBlack(skystoneSensor.red(),skystoneSensor.blue()) && (!second || skystoneSensor.red()>150))) {
                    drive(0,0,0,0);
                    double avgEnc = 1.0*(Math.abs(get_motor_enc(leftFrontDriveMotor)) + Math.abs(get_motor_enc(rightFrontDriveMotor)))/2;
                    inchesMoved = avgEnc/(ConstantVariables.K_PPIN_DRIVE * 1.414);
                    reset_drive_encoders();
                    stage++;
                }
                break;
            case 4:
                //horizontal right to skystone
                if (auto_horizontal(0.25, 1.2)) {
                    reset_drive_encoders();
                    stage++;
                }
                break;
            case 5:
                //lower arm
                if (get_motor_enc(foundationMotor) < -100) {
                    foundation(0);
                    timer.reset();
                    stage++;
                } else
                    foundation(-1);
                break;
            case 6:
                //wait for arm to lower then horizontal left away from skystones
                if (timer.time() > 0.3) {
                    if (auto_horizontal(-0.7, 6)) {
                        reset_drive_encoders();
                        if (second)
                            stage = 14;
                        else
                            stage++;
                    }
                }
                break;
            case 7:
                //drive to construction side
                if (auto_drive(0.8, dropOff - inchesMoved)) {
                    reset_drive_encoders();
                    stage++;
                }
                break;
            case 8:
                //raise arm
                if (get_motor_enc(foundationMotor) > -60) {
                    foundation(0);
                    stage++;
                } else
                    foundation(1);
                break;
            case 9:
                //horizontal left away from stone (skipped)
                if (auto_horizontal(-1, 4)) {
                    reset_drive_encoders();
                    stage+= 2;
                }
                break;
            case 10:
                //turn back to straight (skipped)
                if (auto_turn(-0.5,8)) {
                    reset_drive_encoders();
                    stage++;
                }
                break;
            case 11:
                //drive back to skystones
                if (auto_drive(-1, dropOff - 15)) {
                    reset_drive_encoders();
                    second = true;
                    stage = 1;
                }
                break;
            case 12:
                //horizontal right to blocks (skipped)
                if (auto_horizontal(1, 5)) {
                    reset_drive_encoders();
                    second = true;
                    stage = 1;
                }
                break;
            case 13:
                //drive to touch wall (skipped)
                if (auto_drive(-0.5, 7)) {
                    reset_drive_encoders();
                    second = true;
                    stage = 1;
                }
                break;
            case 14:
                //drive to construction side again
                if (auto_drive(0.8, dropOff - 5 - inchesMoved)) {
                    reset_drive_encoders();
                    stage++;
                }
                break;
            case 15:
                //raise arm
                if (get_motor_enc(foundationMotor) > -61) {
                    foundation(0);
                    stage++;
                } else
                    foundation(1);
                break;
            case 16:
                //horizontal left away from stone
                if (auto_horizontal(-1, 2.1)) {
                    reset_drive_encoders();
                    stage++;
                }
                break;
            case 17:
                //turn to angle inwards
                if (auto_turn(-0.5,15)) {
                    reset_drive_encoders();
                    stage++;
                }
                break;
            case 18:
                //drive back to under bridge
                if (auto_drive(-1, 10)) {
                    reset_drive_encoders();
                    stage++;
                }
                break;
            default:
                break;
        }
    }
}