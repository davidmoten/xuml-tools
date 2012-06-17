package xuml.tools.jaxb;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

public class FindServlet extends HttpServlet {

	private static final long serialVersionUID = -1341129411093311417L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/plain");
		IOUtils.copy(getClass().getResourceAsStream("/find.json"),
				resp.getOutputStream());
	}

}
