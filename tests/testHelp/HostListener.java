package testHelp;

import testHelp.SimpleHost.ServerConnectionThread;


public interface HostListener {

	public void receivedMsg(String msg, ServerConnectionThread thread);
	public void connectionEstablished(ServerConnectionThread thread);
	public void connectionClosed(ServerConnectionThread thread);

	
}
