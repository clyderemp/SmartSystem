package smarthub.sc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JPanel;


public class ButtonPanel extends JPanel {
	
	public DashboardMainFrame dashboard;

	private static final long serialVersionUID = 1L;
	public JButton turnOffAlarm;
	public JButton switchOnOff;
	private JButton fixButton;
	private JButton soundAlarm;
	private JButton returnBtn;


	public ButtonPanel() {
		createContents();
	}

	public void createContents() {
		setLayout(new BorderLayout());
		
		switchOnOff = new JButton();
		switchOnOff.setText("Switch On/Off");
		switchOnOff.setFont(new Font("",Font.PLAIN, 20));
		
		turnOffAlarm = new JButton();
		turnOffAlarm.setText("Open HUB");
		turnOffAlarm.setFont(new Font("",Font.PLAIN, 20));
		
		
	
	}

	public JButton getSwitchOnOff() {
		return switchOnOff;
	}

	public JButton getTurnOffButton() {
		return turnOffAlarm;
	}
	
	public JButton getFixedButtton() {
		return fixButton;
	}
	
	public JButton getSoundAlarmButton() {
		return soundAlarm;
	}
	
	public JButton getReturnBtn() {
		return returnBtn;
	}

}
