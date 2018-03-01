package MAIN;
import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import UTILL.Client_Settings;

public class Login_Window extends JFrame {

	private static final String SERVER_IP = Client_Settings.SERVER_IP;
	private static final int SERVER_PORT = Client_Settings.SERVER_PORT;

	static GridBagLayout gBag;

	static TextField tx = new TextField();
	static TextField tx2 = new TextField("35.166.179.39");

	private static Frame frame = new Frame("QPLAY");
	static Button bt1 = new Button("Log in");
	static Label lb = new Label("nick name: ");
	static Label lb2 = new Label("Server: ");
	static Label lb3 = new Label("Please Log In");

	public Login_Window() {
		init_set();
	}

	public static void init_set() {
		WindowHandler listener = new WindowHandler();
		gBag = new GridBagLayout();

//		gbinsert(lb2, 0, 0, 1, 1, 0, 0);
//		gbinsert(tx2, 1, 0, 1, 1, 1, 0);

		gbinsert(lb, 0, 1, 1, 1, 0, 0);
		gbinsert(tx, 1, 1, 1, 1, 0, 0);
		gbinsert(bt1, 2, 0, 2, 2, 0, 0);

		gbinsert(lb3, 1, 3, 1, 2, 0, 0);

		bt1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				lb3.setText("Log In...");
				lb3.setText("Waiting for other players...");
				
				String nick = tx.getText();
//				
				if (nick.length() > 13)
					return;
				
				String serverip = tx2.getText();
				
//				if(serverip.isEmpty()) {
//					serverip = SERVER_IP;
//				}
				
//				System.out.println(serverip);
				
				Client client = new Client(SERVER_IP, SERVER_PORT, nick);
				Thread current_user_thread = new Thread(client);
				current_user_thread.start();
				
				frame.setEnabled(false);
				frame.setVisible(false);
			}
		});

		frame.setLayout(gBag);
		frame.setBackground(Color.white);
		frame.addWindowListener(listener);
		frame.setResizable(true);
		frame.setSize(300, 150);
		frame.setVisible(true);

		screenInit(frame);
	}

	public static void gbinsert(Component c, int x, int y, int w, int h, double wx, double wy) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = w;
		gbc.gridheight = h;
		gbc.weightx = wx;
		gbc.weighty = wy;
		gBag.setConstraints(c, gbc);
		frame.add(c);
	}

	public static void screenInit(Frame frame) {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

		int xpos = (int) (screen.getWidth() / 2) - frame.getWidth() / 2;
		int ypos = (int) (screen.getHeight() / 2) - frame.getHeight() / 2;

		frame.setLocation(xpos, ypos);
	}

}

class WindowHandler implements ActionListener, WindowListener {

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		try {
			Thread.sleep(300);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		System.exit(0);
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		System.out.println("windows closing");
		System.exit(1);
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
	}
}

