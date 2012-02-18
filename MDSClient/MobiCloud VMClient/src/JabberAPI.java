
	/*
	 * To change this template, choose Tools | Templates
	 * and open the template in the editor.
	 */


import java.io.File;
import java.util.Collection;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

	public class JabberAPI implements MessageListener {
		XMPPConnection connection;
		Collection<RosterEntry> entries;
		private FileTransferManager manager;

		public void setConnection(XMPPConnection connection) {
			this.connection = connection;
			manager = new FileTransferManager(connection);
		}

		public JabberAPI() {
			// turn on the enhanced debugger
			XMPPConnection.DEBUG_ENABLED = true;

		}

		public void login(String userName, String password) throws XMPPException {
			ConnectionConfiguration config = new ConnectionConfiguration(
					"10.5.18.104",
					5222);
			connection = new XMPPConnection(config);

			connection.connect();
			connection.login(userName, password);
		}

		public void fileTransfer(String fileName, String destination)
				throws XMPPException {

			// Create the file transfer manager
			// FileTransferManager manager = new FileTransferManager(connection);
			FileTransferNegotiator.setServiceEnabled(connection, true);
			// Create the outgoing file transfer
			OutgoingFileTransfer transfer = manager
					.createOutgoingFileTransfer(destination);

			// Send the file
			transfer.sendFile(new File(fileName), "You won't believe this!");
			try {
				Thread.sleep(10000);
			} catch (Exception e) {
			}
			System.out.println("Status :: " + transfer.getStatus() + " Error :: "
					+ transfer.getError() + " Exception :: "
					+ transfer.getException());
			System.out.println("Is it done? " + transfer.isDone());
		}

		public void sendMessage(String message, String to) {
			Chat chat = connection.getChatManager().createChat(to, this);
			try {
				chat.sendMessage(message);
			} catch(XMPPException e) {
				e.printStackTrace();
			}
		}

		public void displayBuddyList() {
			Roster roster = connection.getRoster();
	                roster.getEntry("colin");
			entries = roster.getEntries();

			System.out.println("\n\n" + entries.size() + " buddy(ies):");
	                System.out.println("\n" + roster.getEntry("colin"));
			for (RosterEntry r : entries) {
				System.out.println(r.getName());
			}
		}

		public void disconnect() {
			connection.disconnect();
		}

		public void processMessage(Chat chat, Message message) {
			if (message.getType() == Message.Type.chat)
			{
				System.out.println(chat.getParticipant() + " says: "
						+ message.getBody());
			}
		}
		
		public Collection<RosterEntry> getBuddyList()
		{
			return entries;
		}
		
		//OUR PROTOCOL MESSAGING FUNCTION
		public String createGetDirectoryMessage(String serializedFile, String jid)
		{
			return "<MSG><responses><view jid="+jid+">"+serializedFile+"</view></responses></MSG>";
		}
	}
