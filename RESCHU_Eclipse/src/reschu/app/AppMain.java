package reschu.app;

import java.awt.*;  
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import info.clearthought.layout.TableLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import reschu.constants.*;
import reschu.game.controller.Reschu;

public class AppMain implements ActionListener
{
	final private boolean WRITE_TO_DATABASE = false;
	
	private String _username;
	private int _gamemode = MyGameMode.ADMINISTRATOR_MODE;
	private int _group;
	private int _section;
	private int _mode;
	private int _strategy;
	private JFrame _frmLogin;
	private JButton _btnStart;
	private JComboBox _cmbBoxGroup, _cmbBoxSection, _cmbBoxMode, _cmbBoxStrategy;
	private JTextField _cmbTextUserID;
	private Reschu reschu;
	
	/**
	 * When tutorial is finished, RESCHU automatically restarts in the training mode.
	 * @throws IOException 
	 * @throws NumberFormatException
	 */
    public void Restart_Reschu() throws NumberFormatException, IOException {
        if( _gamemode == MyGameMode.TUTORIAL_MODE ) {
        	_gamemode = MyGameMode.TRAIN_MODE;
        }
        reschu.gameEnd();
        reschu.dispose();
        reschu = null;
        
        initRESCHU(_username, _group, _section, _mode, _strategy);
    }
    
	private void initRESCHU(String username, int group, int section, int mode, int strategy) throws NumberFormatException, IOException {
		// Setting _section again seems counter-intuitive here. 
		// Since we are differentiating between administrators and subjects,
		// we need to update the section number here again.
		_group = group;
		_section = section;
		_mode = mode;
		_strategy = strategy;
	    
		// Create an instance of RESCHU (JFrame)
		reschu = new Reschu(_group, _section, _mode, _strategy, _username, this, WRITE_TO_DATABASE);		      
		reschu.pack();
		reschu.setVisible(true);
		reschu.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}
	
	private void setFrmLogin() {
		TitledBorder border; 
		ImageIcon imgIcon;
		
		JLabel lblHAL, lblGroup, lblSection, lblMode, lblStrategy, lblUserId;  
		
		JPanel pnl = new JPanel();
		JPanel pnlInside = new JPanel();

		String[] group = {"Group 1", "Group 2", "Group 3"};
		String[] section = {"Section 1", "Section 2"};
		String[] mode = {"Practice", "Experiment"};
		String[] strategy = {"Waypoint", "Target"};
		
		border = BorderFactory.createTitledBorder("");
		
		lblHAL = new JLabel();
		lblUserId = new JLabel("User ID");
		lblGroup = new JLabel("Group");
		lblSection = new JLabel("Section");
		lblMode = new JLabel("Mode");
		lblStrategy = new JLabel("Strategy");
		_btnStart = new JButton("START"); 
		_btnStart.addActionListener(this);
		_cmbTextUserID = new JTextField();
		_cmbTextUserID.addActionListener(this);
		_cmbBoxGroup = new JComboBox(group);
		_cmbBoxGroup.addActionListener(this);
		_cmbBoxSection = new JComboBox(section);	
		_cmbBoxSection.addActionListener(this);
		_cmbBoxMode = new JComboBox(mode);
		_cmbBoxMode.addActionListener(this);
		_cmbBoxStrategy = new JComboBox(strategy);
		_cmbBoxStrategy.addActionListener(this);
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File("Pictures/HAL/HAL.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
					
		imgIcon = new ImageIcon(img);  //HERE
		lblHAL = null; 
		lblHAL = new JLabel("", imgIcon, JLabel.CENTER);
		
		_frmLogin = new JFrame("RESCHU");
		_frmLogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		_frmLogin.setLayout(new GridLayout(0,1));
		_frmLogin.setResizable(false);
		_frmLogin.add(pnl);
		_frmLogin.setLocation(300,300);
		_frmLogin.setAlwaysOnTop(true);
		_frmLogin.setVisible(true);
        
		double sizeMain[][] = {{TableLayout.FILL, 50, 238, 50, TableLayout.FILL}, 
				{10, 145, 200, TableLayout.FILL}};				
		double sizeInside[][] = {{TableLayout.FILL, 60, 10, 140, TableLayout.FILL}, 
				{TableLayout.FILL, 5, 0, 25, 5, 25, 5, 25, 5, 25, 5, 25, 10, 25, TableLayout.FILL}};
		
		pnlInside.setLayout(new TableLayout(sizeInside));
		pnlInside.setBorder(border);
		pnlInside.add(lblUserId, "1,3");
		pnlInside.add(_cmbTextUserID, "3, 3");
		pnlInside.add(lblMode, "1,5");
		pnlInside.add(_cmbBoxMode, "3,5");
		pnlInside.add(lblGroup, "1,7");
		pnlInside.add(_cmbBoxGroup, "3,7");
		pnlInside.add(lblSection, "1,9");
		pnlInside.add(_cmbBoxSection, "3,9");
		pnlInside.add(lblStrategy, "1,11");
		pnlInside.add(_cmbBoxStrategy, "3,11");
		pnlInside.add(_btnStart, "1,13, 3,13");

		_btnStart.setEnabled(true);					
		
		pnl.setLayout(new TableLayout(sizeMain));
		pnl.setBorder(border);
		pnl.setBackground(Color.WHITE);
		pnl.add(lblHAL, "1,1, 3,1");
		pnl.add(pnlInside, "2,2");
 
		_frmLogin.setSize(400, 400);
	}

	public void actionPerformed(ActionEvent ev) {
		if( !_cmbTextUserID.getText().equals("") ) {  
			_username = _cmbTextUserID.getText();
		}
		if( ev.getSource() == _cmbBoxGroup ) {  
			_group = _cmbBoxGroup.getSelectedIndex();
		}
		if( ev.getSource() == _cmbBoxSection ) {  
			_section = _cmbBoxSection.getSelectedIndex();
		}
		if( ev.getSource() == _cmbBoxStrategy ) {  
			_strategy = _cmbBoxStrategy.getSelectedIndex();
		}
		if( ev.getSource() == _cmbBoxMode ) {  
			_mode = _cmbBoxMode.getSelectedIndex();
			if(_mode == 0) {
				_cmbBoxGroup.setEnabled(false);
				_cmbBoxSection.setEnabled(false);
				_cmbBoxStrategy.setEnabled(false);
			}
			else {
				_cmbBoxGroup.setEnabled(true);
				_cmbBoxSection.setEnabled(true);
				_cmbBoxStrategy.setEnabled(true);
			}
		}
		if( ev.getSource() == _btnStart ) {
			try {
				initRESCHU(_username, _group, _section, _mode, _strategy);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}							
			_frmLogin.setVisible(false); 		
			_frmLogin.dispose();
		}
	}
	
	static public void main (String argv[]) {   
		SwingUtilities.invokeLater(new Runnable() {
            public void run () {
    			AppMain app = new AppMain();
    			app.setFrmLogin();
            }
        });			
	}    
}