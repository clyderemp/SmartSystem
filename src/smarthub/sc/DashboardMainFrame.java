package smarthub.sc;

import java.awt.*;
import java.awt.event.*;

import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.*;

public class DashboardMainFrame extends JFrame{

	/**
	 *
	 */
	//Statecharts
	protected SmartHubSystem hubSystem;
	protected SmartFireSystem fireSystem;
	protected SmartTVSystem tvSystem;
	protected SmartLightSystem lightSystem;
	
	private static final long serialVersionUID = 1L;
	
	public BootSmartHub bootSmartHub;
	
	
    private final int hGap = 5;
    private final int vGap = 5;
    
    public JButton switchBtn;
    public JButton openBtn;
    
    public String MainDashBtn[] = {"Power Consumption (kWh): 0 kWh","Temp Level: 32 C","Turn ON all Systems", "Exit Hub", "Notification Bar"};
    public boolean allsystem_status = false,
    				SF_status = false, STV_status = false, SL_status = false;
    
    
    private String[] borderConstraints = {
        BorderLayout.PAGE_START,
        BorderLayout.LINE_START,
        BorderLayout.CENTER,
        BorderLayout.LINE_END,
        BorderLayout.PAGE_END
    };

    public static JButton[] buttons;

    private GridBagConstraints gbc;

    public JPanel borderPanel, smartFirePanel, smartTVPanel, smartLightPanel;
    
    public JButton smartFire_switch, smokeDetected, carbonDetected,
    				smartTV_switch,	
    				smartLight_switch;
    
	public JTextPane smartFire_message, smartFire_sensors,
						smartTV_message, smartTV_heat,
						smartLight_message, smartLight_heat;
    
    public String sf_switch, sf_message, sf_sensors,
    				stv_switch, stv_message, stv_heat;
    public long stv_temp, sl_temp;
    
   // public JLabel sf_status;
    
    //private JPanel gridPanel;
    //private JPanel gridBagPanel;

	public Object contentPane;

	protected void init() {
		//bootSmartHub.initStatecharts();
		this.addWindowListener(new WindowAdapter() {			
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});		
		this.createContents();
	}
	
    public DashboardMainFrame() {
        buttons = new JButton[16];
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;   
        gbc.insets = new Insets(hGap, vGap, hGap, vGap);
    }

    public void createContents() {
    	
        JFrame frame = new JFrame("Smart Home Dashboard");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel contentPane = new JPanel(
                        new GridLayout(0, 1, hGap, vGap));
        contentPane.setBorder(
            BorderFactory.createEmptyBorder(hGap, vGap, hGap, vGap));
        borderPanel = new JPanel(new BorderLayout(hGap, vGap));
        borderPanel.setBorder(BorderFactory.createTitledBorder("Main Hub"));
        borderPanel.setOpaque(true);
        borderPanel.setBackground(Color.WHITE);
        
        //MainDashBtn[0] = bootSmartHub.message;
        
        for (int i = 0; i < 5; i++) {
            buttons[i] = new JButton(MainDashBtn[i]);
            borderPanel.add(buttons[i], borderConstraints[i]);
        }
        
        contentPane.add(borderPanel);

        
		
		//TURNING ON AND OFF ALL SYSTEMS
        buttons[2].addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(allsystem_status) {
					buttons[2].setText("Turn OFF all systems");
					allsystem_status = false;
					if(!STV_status)
						smartTV_switch.doClick();
					if(!SL_status)
						smartLight_switch.doClick();
				}
				else {
					buttons[2].setText("Turn ON all systems");
					allsystem_status = true;
					if(STV_status)
						smartTV_switch.doClick();
					if(SL_status)
						smartLight_switch.doClick();
				}
			}
        });
        
        
        //Exit Hub
        buttons[3].addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			}
        	
        });
        //Notification
        buttons[4].addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				buttons[4].setText("Notifications Cleared");
			}
        	
        });
        
        /*********************/
		contentPane.add(createFireAlarmPanel());
        contentPane.add(createTVPanel());
        contentPane.add(createLightPanel());

        


        frame.setContentPane(contentPane);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }
    
    /*
     * ******************************
     */
    public Component createFireAlarmPanel() {
        // Smart Fire
        smartFirePanel = new JPanel(new FlowLayout(
                FlowLayout.CENTER, hGap, vGap));
        smartFirePanel.setBorder(
        		BorderFactory.createTitledBorder("Smart Fire System"));
        
        smartFirePanel.setOpaque(true);
        smartFirePanel.setBackground(Color.gray.brighter());
        
        smartFire_switch = new JButton();
        smartFire_switch.setText("Switch ON");
       
        smokeDetected = new JButton();
        smokeDetected.setText("Smoke Detected");
        carbonDetected = new JButton();
        carbonDetected.setText("Carbon Detected");
        
        smartFire_message = new JTextPane();
        smartFire_message.setText("Status: Safe");
        smartFire_sensors = new JTextPane();
        smartFire_sensors.setText("Sensors Triggered: NONE");
        

        smartFirePanel.add(smartFire_message);
        smartFirePanel.add(smartFire_switch);
        smartFirePanel.add(smokeDetected);
        smartFirePanel.add(carbonDetected);
        smartFirePanel.add(smartFire_sensors);
        
        return smartFirePanel;
    }
    
    //Smart TV Panel
    public Component createTVPanel() {
        /***********/
        
        smartTVPanel = new JPanel(new FlowLayout(
                FlowLayout.CENTER, hGap, vGap));
        smartTVPanel.setBorder(
        		BorderFactory.createTitledBorder("Smart TV System"));
        
        smartTVPanel.setOpaque(true);
        smartTVPanel.setBackground(Color.gray.brighter());
        //BUTTONS
        smartTV_switch = new JButton();
        smartTV_message = new JTextPane();
        smartTV_heat = new JTextPane();
        //ADD TO PANEL
        smartTVPanel.add(smartTV_switch);
        smartTVPanel.add(smartTV_message, FlowLayout.LEFT);
        smartTVPanel.add(smartTV_heat);
		
		return smartTVPanel;
    }
    
    //Smart Light Panel
    public Component createLightPanel() {
        /***********/
        
    	smartLightPanel = new JPanel(new FlowLayout(
                FlowLayout.CENTER, hGap, vGap));
    	smartLightPanel.setBorder(
        		BorderFactory.createTitledBorder("Smart Light System"));
        
    	smartLightPanel.setOpaque(true);
    	smartLightPanel.setBackground(Color.gray.brighter());
    	//BUTTONs
        smartLight_switch = new JButton();
        smartLight_message = new JTextPane();
        smartLight_heat = new JTextPane();
        

        
        smartLightPanel.add(smartLight_switch);
        smartLightPanel.add(smartLight_message, FlowLayout.LEFT);
        smartLightPanel.add(smartLight_heat);
        //smartLightPanel.add(smartLight_heat);
		
		return smartLightPanel;
    }

}