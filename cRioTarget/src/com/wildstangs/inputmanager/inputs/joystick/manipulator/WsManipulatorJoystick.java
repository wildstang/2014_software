package com.wildstangs.inputmanager.inputs.joystick.manipulator;

import com.wildstangs.inputmanager.base.IInput;
import com.wildstangs.inputmanager.base.IInputEnum;
import com.wildstangs.inputmanager.inputs.joystick.IHardwareJoystick;
import com.wildstangs.inputmanager.inputs.joystick.WsJoystickAxisEnum;
import com.wildstangs.inputmanager.inputs.joystick.WsJoystickButtonEnum;
import com.wildstangs.subjects.base.BooleanSubject;
import com.wildstangs.subjects.base.DoubleSubject;
import com.wildstangs.subjects.base.ISubjectEnum;
import com.wildstangs.subjects.base.Subject;
import edu.wpi.first.wpilibj.Joystick;

/**
 *
 * @author Nathan
 */
public class WsManipulatorJoystick implements IInput {

    DoubleSubject enterFlywheelAdjustment;
    DoubleSubject exitFlywheelAdjustment;
    DoubleSubject dPadUpDown;
    DoubleSubject dPadLeftRight;
    final static int numberOfButtons = 12;
    BooleanSubject[] buttons;
    Joystick manipulatorJoystick = null;

    public Subject getSubject(ISubjectEnum subjectEnum) {
        if (subjectEnum == WsJoystickAxisEnum.MANIPULATOR_FRONT_ARM_CONTROL) {
            return enterFlywheelAdjustment;
        } else if (subjectEnum == WsJoystickAxisEnum.MANIPULATOR_BACK_ARM_CONTROL) {
            return exitFlywheelAdjustment;
        } else if (subjectEnum == WsJoystickAxisEnum.MANIPULATOR_D_PAD_UP_DOWN) {
            return dPadUpDown;
        } else if (subjectEnum == WsJoystickAxisEnum.MANIPULATOR_D_PAD_LEFT_RIGHT) {
            return dPadLeftRight;
        } else if (subjectEnum instanceof WsJoystickButtonEnum && ((WsJoystickButtonEnum) subjectEnum).isDriver() == false) {
            return buttons[((WsJoystickButtonEnum) subjectEnum).toValue()];
        } else {
            System.out.println("Subject not supported or incorrect.");
            return null;
        }
    }

    public WsManipulatorJoystick() {
        enterFlywheelAdjustment = new DoubleSubject(WsJoystickAxisEnum.MANIPULATOR_FRONT_ARM_CONTROL);
        exitFlywheelAdjustment = new DoubleSubject(WsJoystickAxisEnum.MANIPULATOR_BACK_ARM_CONTROL);
        dPadUpDown = new DoubleSubject(WsJoystickAxisEnum.MANIPULATOR_D_PAD_UP_DOWN);
        dPadLeftRight = new DoubleSubject(WsJoystickAxisEnum.MANIPULATOR_D_PAD_LEFT_RIGHT);
        manipulatorJoystick = (Joystick) new Joystick(2);
        manipulatorJoystick.setAxisChannel(Joystick.AxisType.kX, 1);
        manipulatorJoystick.setAxisChannel(Joystick.AxisType.kY, 2);
        manipulatorJoystick.setAxisChannel(Joystick.AxisType.kZ, 4);
//        manipulatorJoystick.setAxisChannel(Joystick.AxisType.k, 4);
        manipulatorJoystick.setAxisChannel(Joystick.AxisType.kTwist, 5);
        manipulatorJoystick.setAxisChannel(Joystick.AxisType.kThrottle, 6);

        buttons = new BooleanSubject[numberOfButtons];
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new BooleanSubject(WsJoystickButtonEnum.getEnumFromIndex(false, i));
        }
    }

    public void set(IInputEnum key, Object value) {
        if (key == WsJoystickAxisEnum.MANIPULATOR_FRONT_ARM_CONTROL) {
            enterFlywheelAdjustment.setValue(value);
        } else if (key == WsJoystickAxisEnum.MANIPULATOR_BACK_ARM_CONTROL) {
            exitFlywheelAdjustment.setValue(value);
        } else if (key == WsJoystickAxisEnum.MANIPULATOR_D_PAD_UP_DOWN) {
            dPadUpDown.setValue(value);
        } else if (key == WsJoystickAxisEnum.MANIPULATOR_D_PAD_LEFT_RIGHT) {
            dPadLeftRight.setValue(value);
        } else if (key instanceof WsJoystickButtonEnum && ((WsJoystickButtonEnum) key).isDriver() == false) {
            buttons[((WsJoystickButtonEnum) key).toValue()].setValue(value);
        } else {
            System.out.println("key not supported or incorrect.");
        }
    }

    public Object get(IInputEnum key) {
        if (key == WsJoystickAxisEnum.MANIPULATOR_FRONT_ARM_CONTROL) {
            return enterFlywheelAdjustment.getValueAsObject();
        } else if (key == WsJoystickAxisEnum.MANIPULATOR_BACK_ARM_CONTROL) {
            return exitFlywheelAdjustment.getValueAsObject();
        } else if (key == WsJoystickAxisEnum.MANIPULATOR_D_PAD_UP_DOWN) {
            return dPadUpDown.getValueAsObject();
        } else if (key == WsJoystickAxisEnum.MANIPULATOR_D_PAD_LEFT_RIGHT) {
            return dPadLeftRight.getValueAsObject();
        } else if (key instanceof WsJoystickButtonEnum && ((WsJoystickButtonEnum) key).isDriver() == false) {
            return buttons[((WsJoystickButtonEnum) key).toValue()].getValueAsObject();
        } else {
            return new Double(-100);
        }
    }

    public void update() {
        enterFlywheelAdjustment.updateValue();
        exitFlywheelAdjustment.updateValue();
        dPadUpDown.updateValue();
        dPadLeftRight.updateValue();
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].updateValue();
        }
    }

    public void pullData() {
        if (manipulatorJoystick instanceof IHardwareJoystick) {
            ((IHardwareJoystick) manipulatorJoystick).pullData();
        }
        enterFlywheelAdjustment.setValue(manipulatorJoystick.getY() * -1);
        exitFlywheelAdjustment.setValue(manipulatorJoystick.getZ() * -1);
        //Get data from the D-pad
        //We invert the values so up & left are 1, down & right are -1
        dPadUpDown.setValue(manipulatorJoystick.getThrottle() * -1);
        dPadLeftRight.setValue(manipulatorJoystick.getTwist() * -1);
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setValue(manipulatorJoystick.getRawButton(i + 1));
        }

        //System.out.println("X: "+  manipulatorJoystick.getX() + " Y: " + manipulatorJoystick.getY() + " Z:" + manipulatorJoystick.getZ() + " TH: " + manipulatorJoystick.getThrottle()+ " TW: " + manipulatorJoystick.getTwist());
    }
    
    public void set(Object value)
    {
        this.set((IInputEnum) null, value);
    }
    
    public Subject getSubject()
    {
        return this.getSubject((ISubjectEnum) null);
    }

    public Object get()
    {
        return this.get((IInputEnum) null);
    }

    public void notifyConfigChange() {
    }
}
