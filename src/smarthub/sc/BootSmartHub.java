package smarthub.sc;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

//Java Imports
import javax.swing.JFrame;

import com.yakindu.core.ITimerService;

import smarthub.ScaledTimeTimerService;
import smarthub.sc.SmartTVSystem.State;

public class BootSmartHub extends DashboardMainFrame {

	//private static final long serialVersionUID = -8909693541678814631L;
	
	//Statecharts
	protected SmartHubSystem hubSystem;
	protected SmartFireSystem fireSystem;
	protected SmartTVSystem tvSystem;
	protected SmartLightSystem lightSystem;
	protected LED led;


	public static String tv_onoff;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DashboardMainFrame panel;
	
	protected ITimerService timer;
	
	public static int temperature;

	public static void main(String[] args)throws InterruptedException {
		
		BootSmartHub application = new BootSmartHub();

		application.init();
		application.setupStatemachine();
		application.run();
	}
	
	//Setup the DashboardMenu statemachine
	protected void setupStatemachine() {
		
		tvSystem = new SmartTVSystem();
		led = new LED();
		fireSystem = new SmartFireSystem();
		
		
		tvSystem.setTimerService(new ScaledTimeTimerService(5.0));
		led.setTimerService(new ScaledTimeTimerService(5.0));
		fireSystem.setTimerService(new ScaledTimeTimerService(5.0));
		//
		setState(JFrame.EXIT_ON_CLOSE);
		//new DashboardMainFrame().createContents();
    };
    
    //Simulate the statechart
	protected void run() {
		
		Random r = new Random();
		
		fireSystem.sensors.setCarbon_threshold(r.nextLong(200));
		fireSystem.sensors.setSmoke_threshold(r.nextLong(200));
		
		tvSystem.enter();
		led.enter();
		fireSystem.enter();
		fireSystem.raiseToggle();
		System.out.println("Light system entered:"+led.isActive());
		System.out.println("TV system entered:"+tvSystem.isActive());
		System.out.println("Fire system entered:"+fireSystem.isActive());
		
		
		class refresh extends TimerTask {
		    public void run() {
		    	systemData(tvSystem, led, fireSystem); //Refresh Values
		    }
		}

		// And From your main() method or any other method
		Timer timer = new Timer();
		timer.schedule(new refresh(), 0, 500);

		//
        smartFire_switch.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Random r = new Random();
				
				fireSystem.sensors.setCarbon_threshold(r.nextLong(200));
				fireSystem.sensors.setSmoke_threshold(r.nextLong(200));
				
				// TODO Auto-generated method stub
				if(SF_status) {
					smartFirePanel.setBackground(Color.WHITE);
					smartFire_switch.setText("Alarm OFF");
					fireSystem.mode.raiseManual_alarm_off();
				}
				else { //to turn on
					smartFirePanel.setBackground(Color.GRAY.brighter());
					smartFire_switch.setText("Alarm ON");
					fireSystem.mode.raiseManual_alarm_on();
				}
			}
        });
        smokeDetected.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				fireSystem.detect().raiseSmoke();
				boolean smoke = fireSystem.detect().getSmokeDetected();
				System.out.println("Smoke Detected:"+smoke);
			}
        });
        carbonDetected.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				fireSystem.detect().raiseCarbon();
				boolean smoke = fireSystem.detect().getCarbonDetected();
				System.out.println("Carbon Detected:"+smoke);
			}
        });
        
        
		//TV SWITCH
        smartTV_switch.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(STV_status) {
					tvSystem.raiseTurn_off();
					STV_status = false;
				}
				else if(!STV_status){ //to turn on
					
					tvSystem.raiseTurn_on();
					STV_status = true;
				}
			}
        });
        
		//LIGHT SWITCH
        smartLight_switch.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(SL_status) {
					led.raiseOff();
					STV_status = false;
				}
				else if(!SL_status){ //to turn on
					led.raiseOn();
					STV_status = true;
				}
			}
        });

	}
	
	public void systemData(SmartTVSystem tvSystem, LED led, SmartFireSystem fireSystem) {

		updatePowerConsumption();
		
		/************************/
		/*
		 * SMART FIRE
		 * 
		 * */

		SF_status = fireSystem.alarm.getSound();
		String SF_mode = fireSystem.message.getStatus().toString();
		String SF_sensor = fireSystem.message.getSensor().toString();
		
		String firealarm_status = "Status: "+ SF_mode;
		
		if(SF_mode=="SAFE")
			smartFire_message.setForeground(Color.green);
		else if(SF_mode=="WARNING")
			smartFire_message.setForeground(Color.orange);
		else if(SF_mode.contains("DANGER"))
			smartFire_message.setForeground(Color.red);

		smartFire_message.setText(firealarm_status);
		smartFire_sensors.setText(SF_sensor);
		
		if(SF_status) {
			tvSystem.raiseTurn_off();
			led.raiseOff();
			smartFirePanel.setBackground(Color.gray.brighter());
			smartFire_switch.setText("Alarm is ON");
			buttons[4].setText("Shutting off all devices now...Turn off ALARM first..");
		}
		else { //to turn on
			smartFirePanel.setBackground(Color.white);
			smartFire_switch.setText("Alarm is OFF");
		}
		
		
		/************************/
		/*
		 * SMART TV
		 * 
		 * */
		
		boolean isTVon = tvSystem.isStateActive(State.MAIN_SMARTTVSYSTEM_TVSTATUS_ON);
		long threshold = tvSystem.getThreshold();
		long tv_temp = tvSystem.heat().getLevel();

		//Check Temperature
		if(tv_temp >= threshold) {
			buttons[4].setText("TV Overheat! Turning off! (CLICK TO CLEAR)");
			tvSystem.raiseTurn_off();
		}
		
		//Get status
		STV_status = isTVon;
		
		//Assign Smart TV temperature
		stv_temp = tv_temp;
		//Assign Smart TV WiFi Connection
		
		if(tvSystem.wifi().getConnection())
			stv_message = "WiFi CONNECTED";
		else
			stv_message = "WiFi FAILED";
		
		if(STV_status) {
			smartTVPanel.setBackground(Color.gray.brighter());
			smartTV_switch.setText("Turn OFF");
		}
		else { //to turn on
			smartTVPanel.setBackground(Color.white);
			smartTV_switch.setText("Turn ON");
		}
    	smartTV_message.setText(stv_message);
        smartTV_heat.setText("Heat Level: "+stv_temp+"%");
		
		/************************/
		/*
		 * SMARTLIGHT
		 * 
		 * */
		
		boolean light_status = led.getIsLightON();
		long light_temp = led.light.getTemperature();
		//long light_threshold = led.light.getThreshold();
		boolean light_connection = led.wifi.getConnection();
		
		//System.out.println(light_connection);
		
		SL_status = light_status;
		
		if(SL_status) {
			smartLightPanel.setBackground(Color.gray.brighter());
			smartLight_switch.setText("Turn OFF");
		}
		else { //to turn on
			smartLightPanel.setBackground(Color.white);
			smartLight_switch.setText("Turn ON");
		}
		
		if(light_connection)
			smartLight_message.setText("WiFi CONNECTED");
		else
			smartLight_message.setText("WiFi FAILED");
		
		sl_temp = light_temp;
		smartLight_heat.setText("Heat level: "+light_temp+"%");
		
		
		
		if(SL_status && STV_status)
			buttons[2].setText("Turn OFF all systems");
		else
			buttons[2].setText("Turn ON all systems");
		updateTotalTemp();
	
	}
	
	public void updateTotalTemp() {
		float total_temp = 18;
		float TV_Temp = (total_temp * (stv_temp))/100;
		float LED_Temp = (total_temp * (sl_temp))/100;
		total_temp += (TV_Temp + LED_Temp);
		buttons[1].setText("Room Temperature: "+total_temp+" C");
		
		if(total_temp > 30) {
			buttons[1].setForeground(Color.orange);
			buttons[4].setText("Warning: Room Temperature has reached 30 C");
		}
		else if(total_temp > 40) {
			buttons[1].setForeground(Color.red);
			buttons[4].setText("Warning: Room Temperature has reached 40 C");
		}
		else
			buttons[1].setForeground(Color.black);
			
	}
	
	public void updatePowerConsumption() {
		float total_power = 0;
		long tv_power = tvSystem.power().getElectricConsumption();
		long light_power = led.power().getElectricConsumption();
		total_power = (tv_power + light_power)/2;
		System.out.println("POWER"+total_power);

		buttons[0].setText("Power Consumption: "+total_power+" kWh");
		
		if(total_power > 200) {
			buttons[0].setForeground(Color.orange);
			buttons[4].setText("Warning: Power Consumption has reached 50%");
		}
		else if(total_power > 400) {
			buttons[0].setForeground(Color.red);
			buttons[4].setText("Warning: Power Consumption has reached 100%");
		}
		else
			buttons[0].setForeground(Color.black);
			
	}
}
