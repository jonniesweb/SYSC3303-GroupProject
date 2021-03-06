package clientLogic;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ConnectDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textServerIP;
	private JTextField textServerPort;
	private JTextField textClientPort;
	private JButton btnConnect;
	private JButton btnExit;

	/**
	 * For Testing only
	 */
	public static void main(String[] args) {
		try {
			ConnectDialog dialog = new ConnectDialog();
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ConnectDialog() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setResizable(false);
		setAlwaysOnTop(true);
		setTitle("Join A Bomberman Server");
		setBounds(100, 100, 353, 206);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblRequired = new JLabel("Required");
			lblRequired.setBounds(145, 12, 64, 15);
			contentPanel.add(lblRequired);
		}
		{
			JLabel lblServerIP = new JLabel("Server IP");
			lblServerIP.setBounds(12, 34, 97, 15);
			contentPanel.add(lblServerIP);
		}
		{
			textServerIP = new JTextField();
			textServerIP.setBounds(109, 32, 233, 19);
			textServerIP.setText("127.0.0.1");
			contentPanel.add(textServerIP);
			textServerIP.setColumns(10);
		}
		{
			JLabel lblServerPort = new JLabel("Server Port");
			lblServerPort.setBounds(12, 60, 97, 15);
			contentPanel.add(lblServerPort);
		}
		{
			textServerPort = new JTextField();
			textServerPort.setBounds(109, 58, 233, 19);
			textServerPort.setText("8888");
			contentPanel.add(textServerPort);
			textServerPort.setColumns(10);
		}
		{
			JLabel lblOptional = new JLabel("Optional");
			lblOptional.setBounds(146, 92, 61, 15);
			contentPanel.add(lblOptional);
		}
		{
			JLabel lblClientPort = new JLabel("Client Port");
			lblClientPort.setBounds(12, 116, 97, 15);
			contentPanel.add(lblClientPort);
		}
		{
			textClientPort = new JTextField();
			textClientPort.setBounds(109, 114, 233, 19);
			contentPanel.add(textClientPort);
			textClientPort.setColumns(10);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				btnConnect = new JButton("Join");
				btnConnect.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						btnConnect.setEnabled(false);
						btnConnect.setText("Joining...");
					}
				});
				btnConnect.setActionCommand("Join");
				buttonPane.add(btnConnect);
				getRootPane().setDefaultButton(btnConnect);
			}
			{
				btnExit = new JButton("Exit");
				btnExit.setActionCommand("Exit");
				buttonPane.add(btnExit);
			}
		}
	}

	public String getServerIP() {
		return textServerIP.getText();
	}
	
	public String getServerPort() {
		return textServerPort.getText();
	}
	
	public String getClientPort() {
		return textClientPort.getText();
	}
	
	public void addConnectActionListener(ActionListener listener) {
		btnConnect.addActionListener(listener);
	}
	
	public void addExitActionListener(ActionListener listener) {
		btnExit.addActionListener(listener);
	}
}
