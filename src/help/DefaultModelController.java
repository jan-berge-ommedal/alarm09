package help;

import no.ntnu.fp.net.co.Connection;
import connection.LACProtocol;
import connection.ModelEditController;

public class DefaultModelController extends ModelEditController{
	

		public DefaultModelController() {
			super(new LACProtocol());
		}

		@Override
		protected Connection getConnection() {
			// TODO Auto-generated method stub
			return null;
		}
	
}
