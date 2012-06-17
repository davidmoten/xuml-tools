package xuml.tools.jaxb;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

public class SchemaServlet extends HttpServlet {

	private static final long serialVersionUID = 940334942821705645L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//content type as w3c recommendation
		resp.setContentType("text/xml");
		InputStream is= SchemaServlet.class.getResourceAsStream("/xuml.xsd");
		IOUtils.copy(is, resp.getOutputStream());
		is.close();
	}

}
