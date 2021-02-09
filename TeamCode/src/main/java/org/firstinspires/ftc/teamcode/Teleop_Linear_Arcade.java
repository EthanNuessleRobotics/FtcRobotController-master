/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;


/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="Teleop Linear Arcade OpMode 2020-2021", group="Linear Opmode")
//@Disabled
public class Teleop_Linear_Arcade extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftFrontDrive = null;
    private DcMotor rightFrontDrive = null;

    private DcMotor leftBackDrive = null;
    private DcMotor rightBackDrive = null;


    private DcMotor launcher = null;
    private CRServo intake = null;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        leftFrontDrive  = hardwareMap.get(DcMotor.class, "lfdrive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "rfdrive");
        leftBackDrive = hardwareMap.get(DcMotor.class, "lbdrive");
        rightBackDrive = hardwareMap.get(DcMotor.class, "rbdrive");
        intake = hardwareMap.get(CRServo.class, "intake");
        launcher = hardwareMap.get(DcMotor.class, "launcher");

        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);
        intake.setDirection(DcMotorSimple.Direction.FORWARD);
        launcher.setDirection(DcMotorSimple.Direction.REVERSE);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        //Set intake on variable (start with intake off)
        boolean intakeChanged = false;
        boolean isStrafing = false;
        boolean launcherChanged = false;


        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Setup a variable for each drive wheel to save power level for telemetry
            double backward = -1;
            double forward = 1;
            double turningFactor = 0.75;



            // Choose to drive using either Tank Mode, or POV Mode
            // Comment out the method that's not used.  The default below is POV.

            // POV Mode uses left stick to go forward, and right stick to turn.
            // - This uses basic math to combine motions and is easier to drive straight.

            //Check if start is pressed (intake toggle button) if on turn off or vice versa
            if (gamepad1.start && !intakeChanged) {
                if (intake.getPower() == 1){
                    intake.setPower(0);
                } else {
                    intake.setPower(1);
                }
                intakeChanged = true;
            }
            if (!gamepad1.start) {
                intakeChanged = false;
            }

            //Launcher Toggle
            if (gamepad1.a && !launcherChanged) {
                if (launcher.getPower() == 1){
                    launcher.setPower(0);
                } else {
                    launcher.setPower(1);
                }
                launcherChanged = true;
            }
            if (!gamepad1.a) {
                launcherChanged = false;
            }

            //Straifing

            //NEED TO FIX ISSUE WHERE STRAIFING DOES NOT TURN OFF
            if(gamepad1.right_bumper) {
                leftFrontDrive.setPower(-1);
                rightFrontDrive.setPower(1);
                leftBackDrive.setPower(1);
                rightBackDrive.setPower(-1);
                isStrafing = true;
            }else if(gamepad1.left_bumper) {
                leftFrontDrive.setPower(1);
                rightFrontDrive.setPower(-1);
                leftBackDrive.setPower(-1);
                rightBackDrive.setPower(1);
                isStrafing = true;
            }else{
                leftFrontDrive.setPower(0);
                rightFrontDrive.setPower(0);
                leftBackDrive.setPower(0);
                rightBackDrive.setPower(0);
                isStrafing = false;
            }

            if(!isStrafing){
                if (gamepad1.left_stick_x == 0){
                    leftBackDrive.setPower(forward * -gamepad1.left_stick_y);
                    leftFrontDrive.setPower(forward * -gamepad1.left_stick_y);
                    rightBackDrive.setPower(forward * -gamepad1.left_stick_y);
                    rightFrontDrive.setPower(forward * -gamepad1.left_stick_y);

                }
                else if (-gamepad1.left_stick_y == 0){
                    leftBackDrive.setPower(forward * gamepad1.left_stick_x);
                    leftFrontDrive.setPower(backward * gamepad1.left_stick_x);
                    rightBackDrive.setPower(forward * gamepad1.left_stick_x);
                    rightFrontDrive.setPower(backward * gamepad1.left_stick_x);

                }
                else if((gamepad1.left_stick_x*-gamepad1.left_stick_y)>0){
                    if(gamepad1.left_stick_x > 0){
                        rightFrontDrive.setPower(forward);
                        leftBackDrive.setPower(forward);
                    }
                    else{
                        rightFrontDrive.setPower(-forward);
                        leftBackDrive.setPower(-forward);
                    }
                    rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                    leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                }
                else if((gamepad1.left_stick_x*-gamepad1.left_stick_y)<0){
                    if(gamepad1.left_stick_x < 0){
                        leftFrontDrive.setPower(forward);
                        rightBackDrive.setPower((forward));
                    }
                    else{
                        leftFrontDrive.setPower(-forward);
                        rightBackDrive.setPower(-forward);
                    }
                    rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                    leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                }
            }









            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Motors", "left (%.2f), right (%.2f)", forward, backward);
            telemetry.update();
        }
    }
}