package apps;

import javax.swing.JList;

public class BlinkingList extends JList{
	private boolean blink=false;
	private boolean running = true;
	private JList list;
	
	public BlinkingList() {
		super();
		list = this;
		Thread t = new Thread(){
			public void run(){
				while(running){
					blink = (blink ? false : true);				
					list.repaint();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		t.start();
	}
	
	public boolean isBlink(){
		return blink;
	}
	
	

}
