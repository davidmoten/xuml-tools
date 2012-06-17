package xuml.tools.gae;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DatastoreServlet extends HttpServlet {

	private static final long serialVersionUID = 7659767941927427251L;

	private static final String keyKind = "diagram";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String entity = req.getParameter("entity");
			String property = req.getParameter("property");
			String mime = req.getParameter("mime");
			resp.setContentType(mime);
			DatastoreText ds = new DatastoreText();
			String result = ds.get(keyKind, entity, property);
			if (result != null)
				resp.getOutputStream().write(result.getBytes());
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String entity = req.getParameter("entity");
		String property = req.getParameter("property");
		String value = req.getParameter("value");
		DatastoreText ds = new DatastoreText();
		ds.put(keyKind, entity, property, value);
	}

}
