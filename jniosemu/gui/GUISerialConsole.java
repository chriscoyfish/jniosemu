package jniosemu.gui;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.border.*;

import jniosemu.events.*;
import jniosemu.emulator.*;

/**
 * Creates and manages the GUI component of the serial console.
 */
 public class GUISerialConsole extends JFrame
                              implements ActionListener, EventObserver {

	/**
	 * Reference to EventManager used to receive
	 * and send events.
	 */
	private transient EventManager eventManager;

	/**
	 * Text area for received data from serial.
	 */
	private JTextArea recvTextArea;

	/**
	 * Text area where user enter data to be sent to serial.
	 */
	private JTextArea sendTextArea;

	/**
	 * Initiates the creation of GUI components and adds itself to
	 * the Event Manager as an observer.
	 *
	 * @post      eventManager reference is set for this object.
	 * @calledby  GUIManager.setup()
	 * @calls     setup(), EventManager.addEventObserver()
	 *
	 * @param  eventManager  The Event Manager object.
	 */
	public GUISerialConsole(EventManager eventManager)
	{
		super("Serial Console");

		this.eventManager = eventManager;

		setup();

		// add events to listen to
		EventManager.EVENT[] events = {
			EventManager.EVENT.SERIAL_OUTPUT,
			EventManager.EVENT.EMULATOR_RESET,
			EventManager.EVENT.EMULATOR_READY,
			EventManager.EVENT.EMULATOR_CLEAR
		};

		this.eventManager.addEventObserver(events, this);
	}

	/**
	 * Setup GUI components and attributes.
	 *
	 * @post      components created and added to panel
	 * @calledby  GUISerialConsole
	 */
	private void setup()
	{
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		recvTextArea = new JTextArea();
		
		recvTextArea.setEditable(false);
		recvTextArea.setLineWrap(true);
		recvTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

		// put scrollbars around received data text area
		JScrollPane recvScrollPane =
		    new JScrollPane(recvTextArea,
		                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
		                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		sendTextArea = new JTextArea() {
			/**
			 * Override to intercept KeyEvents and send typed character.
			 */
			protected void processEvent(AWTEvent e)
			{
				if (e instanceof KeyEvent && e.getID() == KeyEvent.KEY_TYPED)
				{
					eventManager.sendEvent(
						EventManager.EVENT.SERIAL_INPUT, 
						((KeyEvent) e).getKeyChar()
					);
				} 
				
				// let textarea process keyevent as usual
				super.processEvent(e);
			}
    };

		sendTextArea.setEditable(true);
		sendTextArea.setLineWrap(true);
		sendTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

		// put scrollbars around send data text area
		JScrollPane sendScrollPane =
		    new JScrollPane(sendTextArea,
		                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
		                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		JPanel recvPanel = new JPanel(new BorderLayout());
		JLabel recvLabel = new JLabel("Received");
		recvLabel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		recvPanel.add(recvLabel, BorderLayout.PAGE_START);
		recvPanel.add(recvScrollPane, BorderLayout.CENTER);
		
		JPanel sendPanel = new JPanel(new BorderLayout());
		JLabel sendLabel = new JLabel("Send");
		sendLabel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		sendPanel.add(sendLabel, BorderLayout.PAGE_START);
		sendPanel.add(sendScrollPane, BorderLayout.CENTER);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
		                            recvPanel, sendPanel);
		splitPane.setDividerLocation(150);
		
    // button
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    
    JButton button = new JButton("Close");
    button.addActionListener(this);
    buttonPanel.add(button);

    // container
    Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());
    
    JPanel contentPanel = new JPanel(new BorderLayout());
    contentPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    
    contentPanel.add(splitPane, BorderLayout.CENTER);
    contentPanel.add(buttonPanel, BorderLayout.PAGE_END);
    
    contentPane.add(contentPanel, BorderLayout.CENTER);
	}

	public void update(EventManager.EVENT eventIdentifier, Object obj)
	{
		switch (eventIdentifier) {
			case SERIAL_OUTPUT:
				// append character last
				recvTextArea.append( ((Character) obj).toString() );
				
				// move caret to last position to force scroll
				recvTextArea.setCaretPosition(recvTextArea.getDocument().getLength());

				break;
			case EMULATOR_RESET:
			case EMULATOR_READY:
			case EMULATOR_CLEAR:
				recvTextArea.setText("");
				sendTextArea.setText("");
				break;
		}
	}

	/**
	 * Invoked when a GUI action occurs..
	 *
	 * @param  e  action event object
	 */
	public void actionPerformed(ActionEvent e) {
	  setVisible(false);
	  dispose();
	}

}