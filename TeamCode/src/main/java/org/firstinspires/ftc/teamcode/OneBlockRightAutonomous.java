package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

/*
 * Created by Chun on 11/4/19 for 10023.
 */

@Autonomous
//@Disabled

public class OneBlockRightAutonomous extends BaseRobot {
    private int stage = 0;
    private double inchesMoved = 0;
    private boolean second = false;
    private int dropOff = 48;

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
                if (auto_drive(0.7, 20)) {
                    reset_drive_encoders();
                    stage++;
                }
                break;
            case 2:
                //horizontal right to blocks (until close enough to scan color)
                //0.3
                if (auto_horizontal(0.2, 18) || skystoneSensor.red()>260) {
                    drive(0,0,0,0);
                    reset_drive_encoders();
                    stage++;
                }
                break;
            case 3:
                //drive backward (scanning for the black skystone)
                //power 0.22, inches 40
                if (auto_drive(-0.14, 20) || (checkBlack(skystoneSensor.red(),skystoneSensor.blue()) && (!second || skystoneSensor.blue()>160))) {
                    drive(0,0,0,0);
                    double avgEnc = 1.0*(Math.abs(get_motor_enc(leftFrontDriveMotor)) + Math.abs(get_motor_enc(rightFrontDriveMotor)))/2;
                    inchesMoved = avgEnc/(ConstantVariables.K_PPIN_DRIVE * 1.414);
                    reset_drive_encoders();
                    stage++;
                }
                break;
            case 4:
                //horizontal right to skystone
                if (auto_horizontal(0.25, 4) || skystoneSensor.red() > 300) {
                    drive(0,0,0,0);
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
                    if (auto_horizontal(-0.7, 7)) {
                        reset_drive_encoders();
                        stage++;
                    }
                }
                break;
            case 7:
                //drive to construction side
                if (auto_drive(-0.8, dropOff - 5 - inchesMoved)) {
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
                //horizontal left away from stone
                if (auto_horizontal(-1, 2.1)) {
                    reset_drive_encoders();
                    stage+=2;
                }
                break;
            case 10:
                //turn to angle inwards
                if (auto_turn(0.5,10)) {
                    reset_drive_encoders();
                    stage++;
                }
                break;
            case 11:
                //drive back to under bridge
                if (auto_drive(1, 10)) {
                    reset_drive_encoders();
                    stage++;
                }
                break;
            case 12:
                //horizontal right to center
                if (auto_horizontal(1, 8)) {
                    reset_drive_encoders();
                    stage++;
                }
                break;
            default:
                break;
        }
    }
}